package org.mechdancer.console.parser

import kotlin.math.min

typealias Sentence = List<Token<*>>
typealias Action = Sentence.() -> Pair<Boolean, Any?>
typealias Rule = Sentence
typealias Library = Map<Rule, Action>

/**
 * 维数
 * 即示例的长度
 */
val Rule.dim get() = size

/**
 * 匹配
 * @param sentence 句子
 * @return 匹配长度
 */
operator fun Rule.get(sentence: Sentence): Int {
	val length = min(lastIndex, sentence.lastIndex)
	for (i in 0..length)
		if (this[i] notMatch sentence[i]) return i
	return length + 1
}

/**
 * 以规则形式展示
 */
fun Rule.ruleView() =
	StringBuilder("\"").apply {
		this@ruleView.forEach { append("$it ") }
		deleteCharAt(lastIndex)
		append("\"")
	}.toString()

/**
 * 小工具
 */
val Rule.tip get() = "you mean ${this.ruleView()}?"
