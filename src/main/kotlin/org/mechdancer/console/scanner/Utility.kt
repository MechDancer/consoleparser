package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.parser.Sentence

private fun scan(
	string: String,
	scanners: Set<CharScanner>,
	erase: Boolean
): Sentence {
	val sentence = mutableListOf<Token<*>>()
	fun summary(char: Char?) {
		scanners
			.maxBy { it.size }
			?.takeIf { it !is NoteScanner }
			?.build(erase)
			?.let { sentence.add(it) }
		scanners.forEach { it.reset(char) }
	}

	for (char in string.trim())
		if (scanners.map { it.offer(char) }.none { it })
			summary(char)
	summary(null)
	return sentence
}

infix fun String.splitBy(scanners: Set<CharScanner>) =
	scan(this, scanners, false)

infix fun String.eraseBy(scanners: Set<CharScanner>) =
	scan(this, scanners, true)

val defaultSet = setOf(
	NumberScanner(),
	WordScanner(),
	StringScanner(),
	SignScanner(),
	KeyScanner(),
	NoteScanner(),
	SplitterScanner()
)
