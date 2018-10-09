package org.mechdancer.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.Sign
import org.mechdancer.console.core.TokenType.WordSplitter

class SplitterScanner : CharScanner {
	private var type: Char? = null
		set(value) {
			if (value == null || field.value < value.value)
				field = value
		}

	override var remain = 0
		private set

	override fun offer(e: Char) {
		if (remain-- < 0) return
		val (remain, type) = when (e) {
			in spaceSet -> +0 to e
			in signSet  ->
				if (type !in signSet)
					+0 to e
				else
					-1 to type
			else        -> -1 to null
		}
		this.remain = remain
		this.type = type
	}

	override fun reset(e: Char?) {
		remain = 0
		type = null
	}

	override fun build(erase: Boolean) =
		type?.takeIf { remain <= 0 }
			?.let { Token(if (it in spaceSet) WordSplitter else Sign, it) }

	private companion object {
		val spaceSet = setOf(' ', '\t')
		val signSet = setOf(
			'`', '\'', '\"',
			'(', ')', '[', ']', '{', '}',
			';', ',', '\n')

		val Char?.value
			get() =
				when (this) {
					null        -> +0
					in spaceSet -> +1
					in signSet  -> +2
					else        -> -1
				}
	}
}
