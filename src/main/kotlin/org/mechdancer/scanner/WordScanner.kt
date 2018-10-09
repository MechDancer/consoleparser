package org.mechdancer.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.Word

class WordScanner : CharScanner {
	private val buffer = mutableListOf<Char>()

	override var remain = 1
		private set

	override fun offer(e: Char) {
		if (remain-- < 0) return
		remain =
			e.takeIf { it.isJavaIdentifierStart() || buffer.isNotEmpty() && it.isJavaIdentifierPart() }
				?.also { buffer += it }
				?.let { 0 }
			?: -1
	}

	override fun reset(e: Char?) {
		remain = 1
		buffer.clear()
		e?.let(::offer)
	}

	override fun build(erase: Boolean) =
		buffer
			.takeIf { remain <= 0 }
			?.let { Token(Word, it.joinToString("")) }
}
