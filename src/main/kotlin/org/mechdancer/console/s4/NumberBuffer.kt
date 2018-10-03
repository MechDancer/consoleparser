package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.Integer
import org.mechdancer.console.parser.TokenType.Number

class NumberBuffer : CharBuffer() {
	override fun build() =
		if ('.' in buffer)
			text?.toDoubleOrNull()?.let { Token(Number, it) }
		else
			when (buffer.getOrNull(1)) {
				'b'  -> text?.substring(2)?.toIntOrNull(2)
				'x'  -> text?.substring(2)?.toIntOrNull(16)
				else -> text?.toIntOrNull(10)
			}?.let { Token(Integer, it) }

	override fun check(char: Char): Matcher {
		val case = char.toLowerCase()
		val modeChar by lazy { buffer.getOrNull(1) }
		val mode by lazy { buffer == listOf('0') && case in modeSet }
		val bin by lazy { modeChar == 'b' && case in binSet }
		val int by lazy { modeChar != 'b' && case in intSet }
		val hex by lazy { modeChar == 'x' && case in hexSet }
		val decimal by lazy { modeChar !in modeSet && '.' !in buffer && case == '.' }
		return depends(mode || bin || hex || int || decimal)
	}

	private companion object {
		val modeSet = setOf('b', 'x')
		val binSet = setOf('0', '1')
		val intSet = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
		val hexSet = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
	}
}
