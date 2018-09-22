package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.*

/**
 * 语义分析和执行器
 */
class Parser {
	//规则-动作表
	private val library = mutableMapOf<Rule, Action>()

	/** 指令分析 */
//	abstract fun analyze(command: String): Pair<Sentence, Sentence>

	/** 添加规则和动作 */
	operator fun set(example: String, action: Action) {
		example.erase()
			//检查 规则有效 且 不存在相同规则
			.takeIf { sentence ->
				sentence.isNotEmpty() && (library match sentence).values.none { it.success }
			}?.let { library[it] = action }
			?: throw RuntimeException("rule \"$example\" already exist")
	}

	/** 解析并执行指令 */
	operator fun invoke(command: String) {
		//分词
		val sentence = command.cleanup()
		if (sentence.isEmpty()) return
		//指令分析
		//val (inner, user) = analyze(command)
		val inner = listOf<Token<*>>()
		val user = sentence
		//用户指令匹配
		val matchers = library match user
		//内部指令有效
		if (inner.isNotEmpty()) {

		}
		//内部指令无效
		else {
			//解析
			val result = parse(user, matchers)
			//反馈
			val (success, info) = feedback(user, result)
			//显示
			(if (success) System.out else System.err).println(info)
		}
	}

	/**
	 * 匹配结果
	 * @param length  匹配长度
	 * @param success 是否匹配成功
	 * @param action  对应操作
	 */
	private data class Matcher(
		val length: Int,
		val success: Boolean,
		val action: Action
	)

	private companion object {
		//指令不完整
		const val ambiguousText = "there are more than one rules match this sentence, please check your rules"

		//规则集空
		val noRule = Result(Error, "no rule")

		//判断多个元素相等
		@JvmStatic
		fun <T> equal(vararg list: T) =
			list.size < 2 || (0 until list.size - 1).all { list[it] == list[it + 1] }

		//无任何规则完全匹配
		@JvmStatic
		fun cannotMatch(size: Int, best: Map.Entry<Rule, Int>): Result {
			//最佳匹配规则-匹配长度
			val (rule, length) = best
			//句子除了结束符全部匹配 && 不能匹配结束符是因为规则比句子长
			val incomplete by lazy { size == 1 + length && size < rule.dim }
			//完全无法匹配
			val nothing by lazy { length == 0 }
			return when {
				incomplete -> Result(Incomplete, rule.tip)
				nothing    -> Result(Error, "no rule matched", length)
				else       -> Result(Error, rule.tip, length)
			}
		}

		//添加提示信息
		@JvmStatic
		fun StringBuilder.appendTip(result: Result) {
			if (result.info.isNotBlank()) {
				appendln()
				append(result.info)
			}
		}

		//解析指令
		@JvmStatic
		fun parse(
			sentence: Sentence,
			matchers: Map<Rule, Matcher>
		): Result {
			val perfects = matchers.filter { it.value.success }   //最优匹配
			val action by lazy { perfects.values.first().action } //操作
			return when {
				//唯一匹配
				perfects.size == 1 -> action(sentence).let(::Result)
				//歧义匹配
				perfects.size > 1  -> throw RuntimeException(ambiguousText)
				//无任何规则完全匹配
				else               ->
					matchers
						.mapValues { it.value.length }
						.maxBy { it.value }
						?.let { best -> cannotMatch(sentence.size, best) }
						?: noRule
			}
		}

		//组织反馈信息
		@JvmStatic
		fun feedback(sentence: Sentence, result: Result) =
			if (result.positive) true to result.info
			else false to StringBuilder().apply {
				when (result.what) {
					Success    -> Unit
					Failure    -> append(result.info)
					Error      -> {
						append("invalid command: ")
						sentence.map { it.text }
							.forEachIndexed { i, text ->
								append(if (i == result.where) "> $text < " else "$text ")
							}
						appendTip(result)
					}
					Incomplete -> {
						append("incomplete command: ")
						sentence.forEach { append("${it.text} ") }
						append("...")
						appendTip(result)
					}
				}
			}.toString()

		//匹配
		@JvmStatic
		infix fun Library.match(sentence: Sentence) =
			mapValues {
				val length = it.key[sentence]
				Matcher(length, equal(sentence.size, it.key.dim, length), it.value)
			}
	}
}
