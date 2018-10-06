package org.mechdancer.console.parser.splitter

import org.mechdancer.console.parser.Token

/**
 * 以字符为单位的扫描器
 */
abstract class CharScanner : TokenScanner<Char, Token<*>>() {
	protected val text get() = buffer.joinToString("").takeIf { it.isNotBlank() }
}
