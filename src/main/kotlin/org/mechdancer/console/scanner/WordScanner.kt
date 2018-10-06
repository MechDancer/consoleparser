package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.Word
import org.mechdancer.console.core.depends

class WordScanner : CharScanner() {
	override fun check(char: Char) =
		depends(char.isJavaIdentifierStart() || buffer.isNotEmpty() && char.isJavaIdentifierPart())

	override fun build(erase: Boolean) =
		text?.let { Token(Word, it) }
}
