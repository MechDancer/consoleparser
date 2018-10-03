package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.*
import org.mechdancer.console.parser.TokenType.Number
import org.mechdancer.console.s4.Matcher.Accepted
import org.mechdancer.console.s4.Matcher.Rejected

class KeyBuffer : CharBuffer() {
	private val key
		get() =
			text?.takeIf { it.endsWith("*)") }
				?.let { it.substring(2, it.length - 2) }
				?.trim()

	override fun check(char: Char) =
		when (buffer.size) {
			0    -> depends(char == '(')
			1    -> depends(char == '*')
			2, 3 -> depends(char != '\n')
			else -> when (char) {
				'\n' -> Rejected
				')'  -> nextDepends(buffer.last() != '*')
				else -> Accepted
			}
		}

	override fun build() =
		when (key?.toLowerCase()) {
			null   -> null
			"int"  -> Token<Unit>(Integer)
			"num"  -> Token(Number)
			"word" -> Token(Word)
			"sign" -> Token(Sign)
			else   -> Token(Key)
		}
}
