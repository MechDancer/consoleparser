package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Sentence
import org.mechdancer.console.parser.Token

private fun split(
	string: String,
	buffers: Set<CharScanner>,
	erase: Boolean
): Sentence {
	val sentence = mutableListOf<Token<*>>()
	fun summary(char: Char?) {
		buffers
			.maxBy { it.size }
			?.takeIf { it !is NoteScanner }
			?.build(erase)
			?.let { sentence.add(it) }
		buffers.forEach { it.reset(char) }
	}

	for (char in string.trim())
		if (buffers.map { it.offer(char) }.none { it })
			summary(char)
	summary(null)
	return sentence
}

infix fun String.splitBy(buffers: Set<CharScanner>) =
	split(this, buffers, false)

infix fun String.eraseBy(buffers: Set<CharScanner>) =
	split(this, buffers, true)

val defaultSet = setOf(
	NumberScanner(),
	WordScanner(),
	StringScanner(),
	SignScanner(),
	KeyScanner(),
	NoteScanner(),
	SplitterScanner()
)
