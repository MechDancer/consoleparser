package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.*
import org.mechdancer.console.parser.TokenType.Number
import org.mechdancer.console.parser.splitter.TokenMatchResult.Accepted
import org.mechdancer.console.parser.splitter.TokenMatchResult.Rejected

/**
 * 关键字扫描
 * 语法：(*expresion*)
 */
class KeyScanner : CharScanner() {
	//关键内容
	private val key
		get() =
			text?.takeIf { it.endsWith("*)") }
				?.let { it.substring(2, it.length - 2) }
				?.trim()

	override fun check(char: Char) =
	//开始于(*，结束于*)，不能包含换行
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

	override fun build(erase: Boolean) =
	//翻译类别关键字
		when (key?.toLowerCase()) {
			null   -> null
			"int"  -> Token<Unit>(Integer)
			"num"  -> Token(Number)
			"word" -> Token(Word)
			"sign" -> Token(Sign)
			else   -> Token(Key)
		}
}
