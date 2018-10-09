package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.*
import org.mechdancer.console.core.TokenType.Number

/**
 * 关键字扫描
 * 语法：(*expresion*)
 */
class KeyScanner : CharScanner {
	override var remain = 1
		private set

	private val buffer = StringBuilder()

	override fun offer(e: Char) {
		if (remain-- < 0) return
		remain = when {
			buffer.isEmpty()       -> when (e) {
				'@'  -> +1
				else -> -1
			}
			buffer.length == 1     -> when {
				e == '{'                  -> +1
				e.isJavaIdentifierStart() -> +0
				else                      -> -1
			}
			buffer.take(2) == "@{" ->
				if (buffer.last() == '}') -1
				else when (e) {
					'}'  -> 0
					else -> 1
				}
			else                   -> when {
				e.isJavaIdentifierPart() -> +0
				else                     -> -1
			}
		}.also { if (it >= 0) buffer.append(e) }
	}

	override fun reset(e: Char?) {
		remain = 1
		buffer.setLength(0)
		e?.let(::offer)
	}

	override fun build() =
		buffer
			.takeIf { remain <= 0 && buffer.isNotBlank() }
			?.run {
				if (take(2) != "@{") drop(1)
				else dropLast(1).drop(2)
			}
			?.toString()
			?.let { key ->
				when (key.toLowerCase()) {
					"int"  -> Integer
					"num"  -> Number
					"sign" -> Sign
					"word" -> Word
					else   -> Key
				}
			}
			?.let { Token<Unit>(it) }
}
