package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenMatchResult.Rejected
import org.mechdancer.console.core.TokenType.Word
import org.mechdancer.console.core.depends

class StringScanner : CharScanner() {
	override fun check(char: Char) =
		if (char == '\n' || buffer.isEmpty() && char != '\"')
			Rejected
		else
			depends(buffer.count { it == '\"' } < 2)

	override fun build(erase: Boolean) =
		text?.substring(1, buffer.lastIndex)
			?.let { Token(Word, it.takeUnless { erase }) }
}
