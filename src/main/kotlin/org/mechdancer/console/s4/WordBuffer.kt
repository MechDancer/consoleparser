package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Word

class WordBuffer : CharBuffer() {
	override fun check(char: Char) =
		depends(char.isJavaIdentifierStart() || buffer.isNotEmpty() && char.isJavaIdentifierPart())

	override fun build() =
		text?.let { Token(Word, it) }
}
