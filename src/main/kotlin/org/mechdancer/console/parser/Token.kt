package org.mechdancer.console.parser

import org.mechdancer.console.parser.TokenType.*
import org.mechdancer.console.parser.TokenType.Number

/**
 * 一个词定义为其类别与含义
 * 当只需判断类别时含义记为空
 */
data class Token<T>(val type: TokenType, val data: T? = null) {
	val text get() = (data as? String) ?: data?.toString()

	override fun toString() =
		text ?: when (type) {
			Integer -> "[int]"
			Number  -> "[num]"
			Sign    -> "[sign]"
			Word    -> "[word]"
			Final   -> ";"
			Note    -> ""
			Key     -> "[key]"
		}

	/** 判断另一词是否与此例匹配 */
	infix fun match(actual: Token<*>) =
		when (type) {
			Integer -> actual.type == Integer
			Number  -> actual.type == Integer || actual.type == Number
			Sign    -> actual.type == Sign && (null == text || actual.text == text)
			Word    -> actual.type == Word && (null == text || actual.text == text)
			Final   -> actual.type == Final
			Note    -> throw IllegalArgumentException("note appear in a example")
			Key     -> true
		}

	/** 判断另一词是否不与此例匹配 */
	infix fun notMatch(actual: Token<*>) = !match(actual)
}
