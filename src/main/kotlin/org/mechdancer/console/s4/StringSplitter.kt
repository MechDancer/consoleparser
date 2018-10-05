package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token

infix fun String.splitBy(buffers: Set<CharBuffer>) = {
	val sentence = mutableListOf<Token<*>>()
	fun summary(char: Char? = null) {
		buffers
			.maxBy { it.size }
			?.build()
			?.let { sentence.add(it) }
		buffers.forEach { it.reset(char) }
	}

	for (char in this)
		if (buffers.map { it.offer(char) }.none { it })
			summary(char)
	summary()
	sentence
}

val defaultSet = setOf(
	NumberBuffer(),
	WordBuffer(),
	StringBuffer(),
	SignBuffer(),
	KeyBuffer(),
	NoteBuffer()
)
