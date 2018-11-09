package org.mechdancer.console.scanner

import org.mechdancer.console.token.Token
import org.mechdancer.console.token.TokenType.Word

class WordScanner : CharScanner {
	private val buffer = StringBuilder()

	override var remain = 1
		private set

	override fun offer(e: Char) {
		if (remain-- < 0) return
		remain =
			e.takeIf { it.isLetter() || it == '_' || buffer.isNotEmpty() && it.isLetterOrDigit() }
				?.also { buffer.append(it) }
				?.let { 0 }
			?: -1
	}

	override fun reset(e: Char?) {
		remain = 1
		buffer.setLength(0)
		e?.let(::offer)
	}

	override fun build() =
		buffer
			.takeIf { remain <= 0 && buffer.isNotBlank() }
			?.let { Token(Word, it.toString()) }
}
