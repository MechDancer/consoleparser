package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Sentence
import org.mechdancer.console.parser.Token

infix fun String.splitBy(buffers: Set<CharScanner>): Sentence {
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
	return sentence
}

val defaultSet = setOf(
	NumberScanner(),
	WordScanner(),
	StringScanner(),
	SignScanner(),
	KeyScanner(),
	NoteScanner(),
	SplitterScanner()
)
