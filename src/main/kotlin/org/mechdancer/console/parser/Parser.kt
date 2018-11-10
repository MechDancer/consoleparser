package org.mechdancer.console.parser

import org.mechdancer.console.scanner.defaultScanners
import org.mechdancer.console.scanner.scanBy
import org.mechdancer.console.token.Token

/**
 * 语义分析和执行器
 */
class Parser {
	//用户指令集
	private val userLibrary = mutableMapOf<Rule, Action>()
	//内部指令集
	private val coreLibrary = mapOf<Rule, CoreAction>(
		":help" scanBy defaultScanners to { sentence, matchers ->
			buildString {
				(matchers
					 .filter { it.value.length == sentence.size }
					 .takeIf { it.isNotEmpty() } ?: userLibrary)
					.keys
					.forEach { appendln(it.ruleView()) }
				deleteCharAt(lastIndex)
			}
		}
	)

	/** 指令分析 */
	private fun analyze(sentence: Sentence) =
		if (sentence.firstOrNull()?.text == ":")
			sentence.take(2) to sentence.drop(2)
		else
			emptySentence to sentence

	/** 添加规则和动作 */
	operator fun set(example: String, action: Action) {
		(example scanBy defaultScanners)
			//检查 规则有效 且 不存在相同规则
			.takeIf { sentence ->
				sentence.isNotEmpty() && (userLibrary match sentence).values.none { it.success }
			}?.let { userLibrary[it] = action }
		?: throw RuntimeException("rule \"$example\" already exist")
	}

	/** 解析并执行指令 */
	operator fun invoke(script: String): List<Result> =
		script
			.reader()
			.readLines()
			.filterNot(String::isBlank)
			.map { it scanBy defaultScanners }
			.filterNot(Sentence::isEmpty)
			.map { sentence ->
				//指令分析
				val (inner, user) = analyze(sentence)
				//用户指令匹配
				val matchers = userLibrary match user
				//解析 - 反馈
				if (inner.isNotEmpty())
				//内部指令有效
					coreLibrary
						.filter { equal(inner.size, it.key.dim, it.key[inner]) }
						.toList()
						.firstOrNull()
						?.second
						?.invoke(user, matchers)
						?.let {
							if (it !is Throwable) Result(-1, it)
							else Result(-2, it.message)
						}
					?: cannotMatch
				//内部指令无效
				else parse(user, matchers)
			}

	private companion object {
		//指令不完整
		const val ambiguousText = "there are more than one rules match this sentence, please check your rules"

		//规则集空
		val noRule = Result(0, UnsupportedOperationException("no rule"))

		//无法匹配
		val cannotMatch = Result(0, IllegalArgumentException("can't match any rule"))

		//指令不全
		fun incompelete(tip: String) = Result(-3, IllegalArgumentException(tip))

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
				incomplete -> incompelete(rule.tip)
				nothing    -> cannotMatch
				else       -> Result(length, rule.tip)
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
				perfects.size == 1 -> Result(-1, action(sentence))
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

		@JvmStatic
		val Result.message
			get() = when (data) {
				is String    -> data
				is Throwable -> data.message ?: ""
				else         -> ""
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
