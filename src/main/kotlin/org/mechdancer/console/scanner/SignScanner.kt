package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.Sign
import org.mechdancer.console.core.depends

/**
 * 符号扫描
 * 支持各种连续符号，因此有独立含义的符号需要与其他符号隔开
 */
class SignScanner : CharScanner() {
	override fun check(char: Char) = depends(char in signSet)
	override fun build(erase: Boolean) = text?.let { Token(Sign, it) }

	private companion object {
		val signSet = setOf(
			'!', '?', '@', '#', '$',
			'+', '-', '*', '/', '%', '^',
			'&', '|', '~', '_', '=',
			':', '<', '>', '.'
		)
	}
}
