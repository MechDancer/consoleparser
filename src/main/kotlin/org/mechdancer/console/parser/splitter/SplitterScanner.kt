package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Sign

/**
 * 扫描仅用于分隔的特殊符号
 */
class SplitterScanner : CharScanner() {
	override fun check(char: Char) = depends(buffer.isEmpty() && char in signSet)
	override fun build(erase: Boolean) = text?.let { Token(Sign, it) }
	val signSet = setOf('`', '(', ')', '[', '{', '}', ']', '\\', ';', ',')
}
