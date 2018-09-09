package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.*

class Parser {
	private val rules = mutableListOf<Rule>()

	val library = mutableMapOf<Int, Function>()

	/**
	 * 添加规则
	 */
	operator fun plusAssign(example: String) {
		if (example.isBlank()) return
		rules += Rule.build(example)
	}

	/**
	 * 解析并执行指令
	 */
	operator fun invoke(command: String) {
		val sentence = command.split().cleanup()
		if (sentence.size == 1) return
		val result = parse(sentence)
		when (result.what) {
			Success, Failure -> println(result.info)
			Error            -> {
				print("invalid command: ")
				sentence.dropLast(1).forEachIndexed { i, it ->
					print(
						if (i == result.where)
							"> ${it.text} < "
						else
							"${it.text} "
					)
				}
				println()
			}
			Incomplete       -> {
				print("incomplete command: ")
				sentence.dropLast(1).forEach { print("${it.text} ") }
				println("...")
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
