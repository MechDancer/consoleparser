package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token

class StringSplitter(vararg buffer: CharBuffer) {
	private val buffers = buffer.toSet()
	private fun MutableList<Token<*>>.summary(char: Char? = null) {
		buffers
			.maxBy { it.size }
			?.build()
			?.let { add(it) }
		buffers.forEach { it.reset(char) }
	}

	operator fun invoke(source: String): List<Token<*>> {
		val sentence = mutableListOf<Token<*>>()
		for (char in source)
			if (buffers.map { it.offer(char) }.none { it })
				sentence.summary(char)
		sentence.summary()
		return sentence
	}

	companion object {
		val default by lazy {
			StringSplitter(
				NumberBuffer(),
				WordBuffer(),
				StringBuffer(),
				SignBuffer(),
				KeyBuffer(),
				NoteBuffer()
			)
		}
	}
}
