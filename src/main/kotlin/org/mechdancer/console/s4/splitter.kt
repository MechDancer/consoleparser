package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token

object DefaultParser {
	private val buffers = setOf(
		NumberBuffer(),
		WordBuffer(),
		StringBuffer(),
		SignBuffer(),
		KeyBuffer(),
		NoteBuffer()
	)
	private val sentence = mutableListOf<Token<*>>()

	private fun summary() =
		buffers
			.maxBy { it.size }
			?.build()
			?.let { sentence += it }

	operator fun invoke(source: String): List<Token<*>> {
		sentence.clear()
		for (char in source)
			if (buffers.map { it.offer(char) }.none { it }) {
				summary()
				buffers.forEach { it.reset(char) }
			}
		summary()
		return sentence.toList()
	}
}

fun main(args: Array<String>) {
	val source = "adfg (* int *)  12345.76  +-67890 0xff  \"hello world\" (- this is a note "
	DefaultParser(source).forEach { println("${it.type} : $it") }
}
