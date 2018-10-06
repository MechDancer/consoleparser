package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenMatchResult.Accepted
import org.mechdancer.console.core.TokenMatchResult.Rejected
import org.mechdancer.console.core.TokenType.Note
import org.mechdancer.console.core.depends
import org.mechdancer.console.core.nextDepends

/**
 * 注释扫描
 * 语法：(-expression-)
 */
class NoteScanner : CharScanner() {
	override fun check(char: Char) =
	//开始于(*，结束于*)，不能包含换行
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

	override fun build(erase:Boolean) =
		text?.takeUnless { erase }
			?.let { it.substring(2, if (it.endsWith("-)")) it.length - 2 else it.length) }
			?.trim()
			?.let { Token(Note, it) }
}
