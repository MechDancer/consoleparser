package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.*

/**
 * 语义分析和执行器
 */
class Parser {
	//规则-动作表
	private val library = mutableMapOf<Rule, Action>()

	//检查规则是否已经存在
	private fun check(sentence: Sentence) =
		sentence.size > 1 && library.keys.none { equal(sentence.size, it.dim, it[sentence]) }

	/** 添加规则和动作 */
	operator fun set(example: String, action: Action) {
		example.erase()
			.takeIf(::check)
			?.let { library[it] = action }
			?: throw RuntimeException("rule \"$example\" already exist")
	}

	/** 解析并执行指令 */
	operator fun invoke(command: String) {
		//分词
		val sentence = command.cleanup()
		if (sentence.size == 1) return
		//匹配
		val result = parse(sentence)
		//反馈
		if (result.positive) println(result.info)
		else {
			val info = StringBuilder().apply {
				when (result.what) { //默认在错误流输出
					Success    -> Unit
					Failure    -> append(result.info)
					Error      -> {
						append("invalid command: ")
						sentence
							.dropLast(1)
							.map { it.text }
							.forEachIndexed { i, text ->
								append(if (i == result.where) "> $text < " else "$text ")
							}
						if (result.info.isNotBlank()) {
							appendln()
							append(result.info)
						}
					}
					Incomplete -> {
						append("incomplete command: ")
						sentence.dropLast(1).forEach { append("${it.text} ") }
						append("...")
						if (result.info.isNotBlank()) {
							appendln()
							append(result.info)
						}
					}
				}
			}
			System.err.println(info)
		}
	}

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
			return if (size == 1 + length && size < rule.dim)
				Result(Incomplete, rule.tip)
			else if (length == 0)
				Result(Error, "no rule matched", length)
			else
				Result(Error, rule.tip, length)
		}
	}

	//解析指令
	private fun parse(sentence: Sentence): Result {
		//匹配长度表
		val lengths = library.mapValues { it.key[sentence] }
		//完全匹配表
		val perfect = library.filter { equal(sentence.size, it.key.dim, lengths[it.key]) }
		return when {
			//唯一匹配
			perfect.size == 1 -> perfect.values.first()(sentence).let(::Result)
			//歧义匹配
			perfect.size > 1  -> throw RuntimeException(ambiguousText)
			//无任何规则完全匹配
			else              ->
				lengths
					.maxBy { it.value }
					?.let { best -> cannotMatch(sentence.size, best) }
					?: noRule
		}
	}
}
