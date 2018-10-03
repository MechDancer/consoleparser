package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Note
import org.mechdancer.console.s4.Matcher.Accepted
import org.mechdancer.console.s4.Matcher.Rejected

class NoteBuffer : CharBuffer() {
	override fun check(char: Char) =
		when (buffer.size) {
			0    -> depends(char == '(')
			1    -> depends(char == '-')
			2, 3 -> depends(char != '\n')
			else -> when (char) {
				'\n' -> Rejected
				')'  -> nextDepends(buffer.last() != '-')
				else -> Accepted
			}
		}

	override fun build() =
		text?.let { it.substring(2, if (it.endsWith("-)")) it.length - 2 else it.length) }
			?.trim()
			?.let { Token(Note, it) }
}
