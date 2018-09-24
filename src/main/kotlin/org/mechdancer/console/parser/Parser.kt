package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.*
import org.mechdancer.console.parser.TokenType.Word

/**
 * 语义分析和执行器
 */
class Parser {
	//用户指令集
	private val userLibrary = mutableMapOf<Rule, Action>()
	//内部指令集
	private val coreLibrary = mapOf<Rule, CoreAction>(
		listOf(Token(Word, ":help")) to { sentence, matchers ->
			true to buildString {
				(matchers
					.filter { it.value.length == sentence.size }
					.takeIf { it.isNotEmpty() } ?: userLibrary)
					.keys
					.forEach { appendln(it.ruleView()) }
				deleteCharAt(lastIndex)
			}
		},
		listOf(Token(Word, ":do")) to { sentence, matchers ->
			feedback(sentence, parse(sentence, matchers))
		}
	)

	/** 指令分析 */
	private fun analyze(sentence: Sentence) =
		if (sentence.firstOrNull()?.text?.startsWith(':') == true)
			sentence.take(1) to sentence.drop(1)
		else
			emptySentence to sentence

	/** 添加规则和动作 */
	operator fun set(example: String, action: Action) {
		example.erase()
			//检查 规则有效 且 不存在相同规则
			.takeIf { sentence ->
				sentence.isNotEmpty() && (userLibrary match sentence).values.none { it.success }
			}?.let { userLibrary[it] = action }
			?: throw RuntimeException("rule \"$example\" already exist")
	}

	/** 解析并执行指令 */
	operator fun invoke(script: String) {
		script
			.reader()
			.readLines()
			.forEach { command ->
				//分词
				val sentence = command.cleanup()
				if (sentence.isEmpty()) return
				//指令分析
				val (inner, user) = analyze(sentence)
				//用户指令匹配
				val matchers = userLibrary match user
				//解析 - 反馈
				val (success, info) =
					if (inner.isNotEmpty())
					//内部指令有效
						coreLibrary
							.filter { equal(inner.size, it.key.dim, it.key[inner]) }
							.toList()
							.firstOrNull()
							?.second
							?.invoke(user, matchers)
							?: cannotMatch
					//内部指令无效
					else feedback(user, parse(user, matchers))
				//显示
				(if (success) System.out else System.err).println(info)
			}
	}

	private companion object {
		//指令不完整
		const val ambiguousText = "there are more than one rules match this sentence, please check your rules"

		//规则集空
		val noRule = Result(Error, "no rule")

		//无法匹配
		val cannotMatch = false to "can't match any rule"

		val emptySentence = listOf<Token<*>>()

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
			else false to buildString {
				when (result.what) {
					Success    -> Unit
					Failure    -> append(result.info)
					Error      -> {
						append("invalid command: ")
						sentence.map { it.text }
							.forEachIndexed { i, text ->
								append(if (i == result.where) "> $text < " else "$text ")
							}
						if (result.info.isNotBlank())
							append("\n${result.info}")
					}
					Incomplete -> {
						append("incomplete command: ")
						sentence.forEach { append("${it.text} ") }
						append("...")
						if (result.info.isNotBlank())
							append("\n${result.info}")
					}
				}
			}

		//匹配
		@JvmStatic
		infix fun Library.match(sentence: Sentence) =
			mapValues {
				val length = it.key[sentence]
				Matcher(length, equal(sentence.size, it.key.dim, length), it.value)
			}
	}
}
