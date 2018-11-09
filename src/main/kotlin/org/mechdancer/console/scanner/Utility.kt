package org.mechdancer.console.scanner

import org.mechdancer.console.parser.Sentence
import org.mechdancer.console.token.Token

infix fun String.scanBy(scanners: Set<CharScanner>): Sentence {
	val sentence = mutableListOf<Token<*>>()
	fun summary(char: Char?) {
		scanners
			.filter { it.remain <= 0 }
			.asSequence()
			.associate { it to it.build() }
			.filterValues { it != null }
			.maxBy { it.key.remain }
			?.takeIf { it.key !is NoteScanner }
			?.value
			?.let { sentence += it }
		scanners.forEach { it.reset(char) }
	}

	for (char in trim()) {
		scanners.forEach { it.offer(char) }
		if (scanners.all { it.remain < 0 })
			summary(char)
	}
	summary(null)
	return sentence
}

val defaultScanners = setOf(
	NumberScanner(),
	WordScanner(),
	SignScanner(),
	KeyScanner(),
	NoteScanner()
)
