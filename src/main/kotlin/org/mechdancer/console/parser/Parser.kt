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
		//反馈：默认在错误流输出
		with(System.err) {
			when (result.what) {
				Success    -> System.out.println(result.info)
				Failure    -> println(result.info)
				Error      -> {
					print("invalid command: ")
					sentence
						.dropLast(1)
						.map { it.text }
						.forEachIndexed { i, text ->
							print(if (i == result.where) "> $text < " else "$text ")
						}
					println()
				}
				Incomplete -> {
					print("incomplete command: ")
					sentence.dropLast(1).forEach { print("${it.text} ") }
					println("...")
				}
			}
		}
	}

	/** 将操作打包，以便在实际执行之前插入检查等操作 */
	infix fun pack(command: String) = { invoke(command) }

	private companion object {
		//指令不完整
		const val ambiguous = "there are more than one rules match this sentence, please check your rules"

		//判断多个元素相等
		@JvmStatic
		fun <T> equal(vararg list: T) =
			list.size < 2 || (0 until list.size - 1).all { list[it] == list[it + 1] }
	}

	private fun parse(sentence: Sentence): Result {
		//匹配长度表
		val lengths = rules.mapValues { it.value[sentence] }
		//完全匹配表
		val success = rules.filter { equal(sentence.size, it.value.dim, lengths[it.key]) }.keys
		return when {
			//唯一匹配
			success.size == 1 -> {
				success.first().let {
					library[it]
						?.invoke(sentence)
						?: Result("no function bind rule \"${rules[it]}]\"", false)
				}
			}
			//歧义匹配
			success.size > 1  -> Result(ambiguous, false)
			//无法匹配
			else              ->
				lengths.maxBy { it.value }?.let { best ->
					if (sentence.size == 1 + best.value //句子除了结束符全部匹配
						&&                              //不能匹配结束符是因为规则比句子长
						sentence.size < rules[best.key]?.dim ?: 0)
						Result(Incomplete)
					else
						Result(Error, best.value)
				} ?: Result(Error, 0, "no rule")
		}
	}
}
