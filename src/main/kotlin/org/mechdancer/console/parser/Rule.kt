package org.mechdancer.console.parser

import org.mechdancer.console.core.Token
import kotlin.math.min

/** 句子是词的列表 */
typealias Sentence = List<Token<*>>

/** 操作基于指令 */
typealias Action = Sentence.() -> Pair<Boolean, Any?>

/** 规则是擦除了内容的句子 */
typealias Rule = Sentence

/** 库是规则到操作的映射 */
typealias Library = Map<Rule, Action>

/** 匹配操作获取到指令与各自的匹配结果，包括长度、是否成功和对应操作 */
typealias Matchers = Map<Rule, Matcher>

/** 内核操作不但参考指令，还能获取指令与全指令库匹配的结果 */
typealias CoreAction = (Sentence, Matchers) -> Pair<Boolean, Any?>

/** 内核指令库是内核指令到内核操作的映射 */
typealias CoreLibrary = Map<Rule, CoreAction>

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
	buildString {
		append("\"")
		this@ruleView.forEach { append("$it ") }
		deleteCharAt(lastIndex)
		append("\"")
	}

/**
 * 小工具
 */
val Rule.tip get() = "you mean ${this.ruleView()}?"
