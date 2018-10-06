package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Integer
import org.mechdancer.console.parser.TokenType.Number

/**
 * 数字扫描
 */
class NumberScanner : CharScanner() {
	override fun build(erase: Boolean) =
		if ('.' in buffer)
			text?.toDoubleOrNull()?.let { Token(Number, it.takeUnless { erase }) }
		else
			when (buffer.getOrNull(1)) {
				'b'  -> text?.substring(2)?.toIntOrNull(2)
				'x'  -> text?.substring(2)?.toIntOrNull(16)
				else -> text?.toIntOrNull(10)
			}?.let { Token(Integer, it.takeUnless { erase }) }

	override fun check(char: Char): TokenMatchResult {
		val case = char.toLowerCase()
		val mode = buffer == startList && case in modeSet
		val modeChar by lazy { buffer.getOrNull(1) }
		val bin by lazy { modeChar == 'b' && case in binSet }
		val int by lazy { modeChar != 'b' && case in intSet }
		val hex by lazy { modeChar == 'x' && case in hexSet }
		val decimal by lazy { modeChar !in modeSet && '.' !in buffer && case == '.' }
		return depends(mode || int || bin || hex || decimal)
	}

	private companion object {
		val startList = listOf('0')
		val modeSet = setOf('b', 'x')
		val binSet = setOf('0', '1')
		val intSet = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
		val hexSet = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
	}
}
