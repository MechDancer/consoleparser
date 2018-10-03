package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Word
import org.mechdancer.console.s4.Matcher.Rejected

class StringBuffer : CharBuffer() {
	override fun check(char: Char) =
		Rejected.takeIf { char == '\n' || buffer.isEmpty() && char != '\"' }
			?: depends(buffer.count { it == '\"' } < 2)

	override fun build() =
		text?.substring(1, buffer.lastIndex)?.let { Token(Word, it) }
}
