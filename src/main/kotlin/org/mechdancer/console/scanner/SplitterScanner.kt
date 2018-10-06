package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.Sign
import org.mechdancer.console.core.depends

/**
 * 扫描仅用于分隔的特殊符号
 */
class SplitterScanner : CharScanner() {
	override fun check(char: Char) = depends(buffer.isEmpty() && char in signSet)
	override fun build(erase: Boolean) = text?.let { Token(Sign, it) }

	private companion object {
		val signSet = setOf('`', '(', ')', '[', '{', '}', ']', '\\', ';', ',')
	}
}
