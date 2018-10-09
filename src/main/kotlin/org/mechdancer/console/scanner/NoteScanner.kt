package org.mechdancer.console.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.Note

/**
 * 注释扫描
 * 语法：/* expression */
 */
class NoteScanner : CharScanner {
	override var remain = 1
		private set

	private val buffer = StringBuilder()

	override fun offer(e: Char) {
		if (remain-- < 0) return
		//开始于/*，结束于*/，不能包含换行
		remain = when {
			buffer.isEmpty()       -> when (e) {
				'/'  -> 1
				else -> -1
			}
			buffer.length == 1     -> when (e) {
				'/'  -> 0
				'*'  -> 2
				else -> -1
			}
			buffer.take(2) == "/*" ->
				if (buffer.takeLast(2) == "*/") -1
				else when (e) {
					'*'  -> 1
					'/'  -> if (buffer.last() == '*') 0 else 2
					else -> 2
				}
			else                   -> when (e) {
				'\n' -> -1
				else -> 0
			}
		}.also { if (it >= 0) buffer.append(e) }
	}

	override fun reset(e: Char?) {
		remain = 1
		buffer.setLength(0)
		e?.let(::offer)
	}

	override fun build() =
		buffer
			.takeIf { remain <= 0 }
			?.toString()
			?.let { Token(Note, it) }
}
