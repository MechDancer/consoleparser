package org.mechdancer.console.core

import org.mechdancer.console.core.TokenType.*
import org.mechdancer.console.core.TokenType.Number

/**
 * 一个词定义为其类别与含义
 * 当只需判断类别时含义记为空
 * @param type 词性
 * @param data 词义
 */
data class Token<T>(val type: TokenType, val data: T? = null) {
	/** 转字符 */
	val text get() = (data as? String) ?: data?.toString()

	/** 按类型显示 */
	override fun toString() =
		text ?: when (type) {
			Integer      -> "[int]"
			Number       -> "[num]"
			Sign         -> "[sign]"
			Word         -> "[word]"
			Note         -> ""
			Key          -> "[key]"
			WordSplitter -> " "
			LineSplitter -> "\n"
		}

	/** 判断另一词是否与此例匹配 */
	infix fun match(actual: Token<*>) =
		when (type) {
			Integer      -> actual.type == Integer
			Number       -> actual.type == Integer || actual.type == Number
			Sign         -> actual.type == Sign && (null == text || actual.text == text)
			Word         -> actual.type == Word && (null == text || actual.text == text)
			Note         -> throw IllegalArgumentException("note appeared in rule")
			Key          -> true
			WordSplitter -> actual.type == WordSplitter
			LineSplitter -> actual.type == LineSplitter
		}

	/** 判断另一词是否不与此例匹配 */
	infix fun notMatch(actual: Token<*>) = !match(actual)
}
