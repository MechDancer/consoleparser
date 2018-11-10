package org.mechdancer.console.newScanner

class SignScanner(
	private val extensionSet: Set<String> = defaultExtensionSet
) : Scanner {
	private var state = 0
	override val length get() = state.takeIf { it >= 0 } ?: -(state + 1)
	override val complete get() = length > 0

	private val buffer = CharArray(extensionSet.map(String::length).max() ?: 1)

	override fun invoke(char: Char) {
		if (state < 0) return
		if (char !in signSet || state == buffer.size) {
			state = -state - 1
			return
		}
		buffer[state] = char
		val current = String(buffer.copyOfRange(0, ++state))
		if (state > 1 && extensionSet.none { it.startsWith(current) })
			state = -state
	}

	override fun reset() {
		state = 0
	}

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
