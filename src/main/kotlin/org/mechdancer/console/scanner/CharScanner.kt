package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenScanner

/**
 * 以字符为单位的扫描器
 */
abstract class CharScanner : TokenScanner<Char, Token<*>>() {
	protected val text get() = buffer.joinToString("").takeIf { it.isNotBlank() }
}
