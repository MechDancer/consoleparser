package org.mechdancer.console.parser

import org.mechdancer.console.parser.TokenType.*
import java.lang.StringBuilder
import java.util.regex.Pattern

/**
 * 规则
 * 定义为一个编号和一个示例
 */
class Rule(val id: Int, val example: Sentence) {
	/**
	 * 维数
	 * 即示例的长度
	 */
	val dim get() = example.size

	/**
	 * 匹配
	 * @param sentence 句子
	 * @return 匹配长度
	 */
	operator fun get(sentence: Sentence): Int {
		for (i in sentence.indices) {
			when (example[i].type) {
				Word, Sign             ->
					example[i] != sentence[i]
				Key                    ->
					sentence[i].type == Key || sentence[i].type == Final
				Integer, Number, Final ->
					sentence[i].type != example[i].type
				Note                   ->
					throw IllegalArgumentException("note appear in a sentence")
			}.let { if (it) return i }
		}
		return sentence.size
	}

	override fun toString(): String {
		val builder = StringBuilder("#$id:")
		example.forEach { builder.append(" ${it.type.name}") }
		return builder.toString()
	}

	companion object {
		private val patten = Pattern.compile("(^#(\\d+):\\s*([\\s|\\S]+)$)")

		/**
		 * 生成器
		 * 从示例句构造规则
		 */
		fun build(example: String): Rule {
			val m = patten.matcher(example)
			if (!m.find() || m.groupCount() != 3) //[整体][id][body]
				throw IllegalArgumentException("failed to build a rule by example: $example")
			return Rule(m.group(2).toInt(), m.group(3).split().cleanup())
		}
	}
}
