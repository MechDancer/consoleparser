package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.*

/**
 * 语义分析和执行器
 */
class Parser {
	private val rules = mutableMapOf<Int, Rule>()
	private val library = mutableMapOf<Int, Function>()

	/** 添加规则 */
	infix fun access(example: String) {
		if (example.isNotBlank()) {
			val (id, sentence) = Rule.build(example)
			rules[id] = Rule(sentence, example)
		}
	}

	/** 添加规则 */
	operator fun set(id: Int, example: String) {
		if (example.isNotBlank())
			rules[id] = Rule(example.split().cleanup(), example)
	}

	/** 添加动作 */
	operator fun set(id: Int, function: Function) {
		library[id] = function
	}

	/** 添加规则和动作 */
	operator fun set(example: String, function: Function) {
		if (example.isBlank()) return
		((library.keys.max() ?: 0) + 1)
			.let { id ->
				this[id] = example
				this[id] = function
			}
	}

	/** 解析并执行指令 */
	operator fun invoke(command: String) {
		//分词
		val sentence = command.split().cleanup()
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
					}
					Incomplete -> {
						append("incomplete command: ")
						sentence.dropLast(1).forEach { append("${it.text} ") }
						append("...")
					}
				}
			}
			System.err.println(info)
		}
	}

	/** 将操作打包，以便在实际执行之前插入检查等操作 */
	infix fun pack(command: String) = { invoke(command) }

	private companion object {
		//指令不完整
		const val ambiguousText = "there are more than one rules match this sentence, please check your rules"

		//规则集空
		val noRule = Result(Error, "no rule")

		//判断多个元素相等
		@JvmStatic
		fun <T> equal(vararg list: T) =
			list.size < 2 || (0 until list.size - 1).all { list[it] == list[it + 1] }
	}

	//匹配成功，执行
	private fun success(sentence: Sentence, best: Int) =
		library[best]?.invoke(sentence)?.let(::Result)
			?: Result(Error, "no function bind rule \"${rules[best]}]\"")

	//有多个指令匹配
	private fun ambiguous(list: Iterable<Int>) =
		Result(Error, ambiguousText)

	//无任何规则匹配
	private fun none(size: Int, best: Map.Entry<Int, Int>) =
	//句子除了结束符全部匹配 && 不能匹配结束符是因为规则比句子长
		if (size == 1 + best.value && size < rules[best.key]!!.dim)
			Result(Incomplete)
		else
			Result(Error, "", best.value)

	//解析指令
	private fun parse(sentence: Sentence): Result {
		//匹配长度表
		val lengths = rules.mapValues { it.value[sentence] }
		//完全匹配表
		val perfect = rules.filter { equal(sentence.size, it.value.dim, lengths[it.key]) }.keys
		//最佳匹配项
		val best = lengths.maxBy { it.value }
		return when {
			//唯一匹配
			perfect.size == 1 -> success(sentence, perfect.first())
			//歧义匹配
			perfect.size > 1  -> ambiguous(perfect)
			//无法匹配
			best != null      -> none(sentence.size, best)
			//规则集空
			else              -> noRule
		}
	}
}
