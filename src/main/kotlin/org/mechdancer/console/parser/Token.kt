package org.mechdancer.console.parser

/**
 * 词法分类
 */
data class Token(val text: String, val type: TokenType) {
	override fun toString() = "\"$text\": ${type.name}"
}
