package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.*

/**
 * 语义分析和执行器
 */
class Parser {
	private val rules = mutableListOf<Rule>()
	private val library = mutableMapOf<Int, Function>()

	/** 添加规则 */
	infix fun access(example: String) {
		if (example.isNotBlank())
			rules += Rule.build(example)
	}

	/** 添加规则 */
	operator fun set(id: Int, example: String) {
		if (example.isNotBlank())
			rules += Rule(id, example.split().cleanup())
	}

	/** 添加动作 */
	operator fun set(id: Int, function: Function) {
		library[id] = function
	}

	/** 添加规则和动作 */
	operator fun set(example: String, function: Function) {
		if (example.isBlank()) return
		val id = library.keys.max() ?: 0
		this[id] = example
		this[id] = function
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
					sentence.dropLast(1).forEachIndexed { i, it ->
						print(if (i == result.where) "> ${it.text} < " else it.text)
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

	private companion object {
		val Int.notFound get() = "no function bind id[$this]"
		const val ambiguous = "there are more than one rules match this sentence, please check your rules"
	}

	private fun parse(sentence: Sentence): Result {
		//匹配长度表
		val lengths = rules.associate { it to it[sentence] }
		//完全匹配表
		val success = rules.filter {
			sentence.size == it.dim && sentence.size == lengths[it]
		}
		return when {
			//唯一匹配
			success.size == 1 -> {
				success.first().id.let {
					library[it]
						?.invoke(sentence)
						?: Result(it.notFound, false)
				}
			}
			//歧义匹配
			success.size > 1  ->
				Result(ambiguous, false)
			//无法匹配
			else              ->
				lengths.maxBy { it.value }?.let { best ->
					if (sentence.size - 1 == best.value //句子除了结束符全部匹配
						&&                              //不能匹配结束符是因为规则比句子长
						sentence.size < best.key.dim)
						Result(Incomplete)
					else
						Result(Error, best.value)
				} ?: Result(Error, 0, "no rule")
		}
	}
}
