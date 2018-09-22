package org.mechdancer.console.parser

private val dec = "(^[-+]?\\d+$)".toRegex()
private val hex = "(^[-+]?0x[a-fA-F0-9]+$)".toRegex()
private val num = "(^[-+]?\\d*\\.\\d+$)".toRegex()
private val sign = "(^[^a-zA-Z]+$)".toRegex()
private val note = "(^\\[-\\D+-]$)".toRegex()
private val finalNote = "(^\\[-\\D+$)".toRegex()
private val key = "(^\\{\\D+}$)".toRegex()

/**
 * 标签类别
 * @param pattens 匹配模式
 */
enum class TokenType(vararg val pattens: Regex) {
	Integer(dec, hex),
	Number(num),
	Sign(sign),
	Word,
	Final(finalNote),
	Note(note),
	Key(key)
}
