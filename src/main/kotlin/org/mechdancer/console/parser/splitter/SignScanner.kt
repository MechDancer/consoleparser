package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Sign

/**
 * 符号扫描
 * 支持各种连续符号，因此有独立含义的符号需要与其他符号隔开
 */
class SignScanner : CharScanner() {
	override fun check(char: Char) = depends(char in signSet)
	override fun build() = text?.let { Token(Sign, it) }

	private companion object {
		val signSet = setOf(
			'!', '?', '@', '#', '$',
			'+', '-', '*', '/', '%', '^',
			'&', '|', '~', '_', '=',
			':', '<', '>', '.'
		)
	}
}
