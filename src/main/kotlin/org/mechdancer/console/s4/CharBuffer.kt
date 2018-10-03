package org.mechdancer.console.s4

import org.mechdancer.console.parser.Token

abstract class CharBuffer : TokenBuffer<Char, Token<*>>() {
	protected val text get() = buffer.joinToString("").takeIf { it.isNotBlank() }
}
