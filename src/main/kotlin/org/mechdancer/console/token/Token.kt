package org.mechdancer.console.token

import org.mechdancer.console.token.TokenType.*
import org.mechdancer.console.token.TokenType.Number

/**
 * 一个词定义为其类别与含义
 * 当只需判断类别时含义记为空
 * @param type 词性
 * @param data 词义
 */
data class Token<T>(val type: TokenType, val data: T) {
	/** 按类型显示 */
	override fun toString() = (data as? String) ?: data.toString()

	/** 判断另一词是否与此例匹配 */
	infix fun match(actual: Token<*>) =
		when (type) {
			Number -> actual.type == Number
			Sign   -> actual.type == Sign && (toString().isBlank() || actual.toString() == toString())
			Word   -> actual.type == Word && (toString().isBlank() || actual.toString() == toString())
			Note   -> throw IllegalArgumentException("note appeared in rule")
			Key    -> true
		}

	/** 判断另一词是否不与此例匹配 */
	infix fun notMatch(actual: Token<*>) = !match(actual)
}
