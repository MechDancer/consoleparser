package org.mechdancer.console.parser

import org.mechdancer.console.token.Token
import kotlin.math.min

/** 句子是词的列表 */
typealias Sentence = List<Token<*>>

/** 操作基于指令 */
typealias Action = Sentence.() -> Any?

/** 规则是句子 */
typealias Rule = Sentence

/** 库是规则到操作的映射 */
typealias Library = Map<Rule, Action>

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
