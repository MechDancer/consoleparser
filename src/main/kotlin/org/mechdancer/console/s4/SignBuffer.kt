package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Sign

class SignBuffer : CharBuffer() {
	override fun check(char: Char) =
		depends(char in signSet)

	override fun build() =
		text?.let { Token(Sign, it) }

	private companion object {
		val signSet = setOf(
			'~', '`', '!', '@',
			'#', '$', '%',
			'^', '&', '*',
			'(', ')', '-',
			'_', '=', '+',
			'[', '{', '}', ']',
			'|', '\\', '/',
			';', ':', '<', '>',
			',', '.', '?'
		)
	}
}
