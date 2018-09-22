package org.mechdancer.console.parser

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
	for (i in sentence.indices)
		if (this[i] notMatch sentence[i]) return i
	return sentence.size
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
