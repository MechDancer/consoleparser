package org.mechdancer.scanner

import org.mechdancer.console.core.Token
import org.mechdancer.console.core.TokenType.Sign

class SignScanner(
	private val extensionSet: Set<String> = defaultExtensionSet
) : CharScanner {
	private val buffer =
		CharArray(extensionSet.map { it.length }.max() ?: 1) { ' ' }

	override var remain = 0
		private set

	override fun offer(e: Char) {
		if (remain-- < 0) return
		remain = buffer
			.takeIf { e in signSet }
			?.indexOfFirst { it == ' ' }
			?.takeUnless { it < 0 }
			?.let { i ->
				i.takeIf { it == 0 }
					?.also { buffer[i] = e }
					?: extensionSet
						.filter { it.startsWith(String(buffer.clone().apply { set(i, e) }).trim()) }
						.takeIf { it.isNotEmpty() }
						?.also { buffer[i] = e }
						?.let { set -> (set.map { it.length }.min()!!) - i - 1 }
			}
			?: -1
	}

	override fun reset(e: Char?) {
		remain = 1
		buffer.fill(' ')
		e?.let(::offer)
	}

	override fun build(erase: Boolean) =
		buffer
			.takeIf { remain <= 0 }
			?.let { String(it) }
			?.trim()
			?.takeIf { it.isNotBlank() }
			?.let { Token(Sign, it) }

	companion object {
		private val signSet = setOf(
			'!', '?', '@', '#', '$',
			'+', '-', '*', '/', '%', '^',
			'&', '|', '~', '_', '=',
			':', '<', '>', '.',

			'`', '\'', '\"',
			'(', ')', '[', ']', '{', '}',
			';', ',', '\n')

		val defaultExtensionSet =
			setOf(
				"++", "--", "**", "&&", "||",
				"<<", ">>", "->", "<-",
				"==", "!=", "===", "=/=",
				"=>", "<=", "<=>",
				"..", "...", "?.", "!!",
				"<>", ":="
			)
	}
}
