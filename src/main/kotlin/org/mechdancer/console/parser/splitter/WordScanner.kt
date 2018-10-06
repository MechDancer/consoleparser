package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Word

class WordScanner : CharScanner() {
	override fun check(char: Char) =
		depends(char.isJavaIdentifierStart() || buffer.isNotEmpty() && char.isJavaIdentifierPart())

	override fun build(erase: Boolean) =
		text?.let { Token(Word, it) }
}
