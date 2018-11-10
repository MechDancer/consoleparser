package org.mechdancer.console.newScanner

import org.mechdancer.console.token.TokenType

interface Scanner {
	/**
	 * 最长匹配长度
	 */
	val length: Int

	/**
	 * 最长匹配长度内字符匹配是否完整
	 */
	val complete: Boolean

	/**
	 * 匹配一个字符
	 * @param char 将进行匹配的字符
	 */
	operator fun invoke(char: Char)

	/**
	 * 重置内部状态
	 */
	fun reset()

	companion object {
		private fun buildDFA(
			parameter: Triple<List<List<Int>>, Set<Int>, (Char) -> Int>
		): DFA {
			val (table, ending, map) = parameter
			return DFA(table, ending, map)
		}

		@JvmStatic
		operator fun get(type: TokenType): Scanner? =
			when (type) {
				TokenType.Number  -> buildDFA(parameters[type]!!)
				TokenType.Sign    -> SignScanner()
				TokenType.Word    -> buildDFA(parameters[type]!!)
				TokenType.Note    -> buildDFA(parameters[type]!!)
				TokenType.Key     -> buildDFA(parameters[type]!!)
				TokenType.Integer -> null
			}

		private fun Char.isD() = isLetter() || this == '_'
		private fun Char.isd() = isDigit()

		private val parameters = mapOf(
			TokenType.Note to Triple(// /  *  e    //
				listOf(listOf(2, 0, 0),  // 1 -> ε
				       listOf(7, 3, 0),  // 2 -> /
				       listOf(6, 4, 6),  // 3 -> /*
				       listOf(5, 4, 6),  // 4 -> /* ... *
				       listOf(0, 0, 0),  // 5 -> /* ... */
				       listOf(6, 4, 6),  // 6 -> /* ...
				       listOf(7, 7, 7)), // 7 -> // ...
				setOf(5, 7),
				{ c: Char ->
					when (c) {
						'/'  -> 0
						'*'  -> 1
						else -> 2
					}
				}
			),
			TokenType.Word to Triple(// D  d    //
				listOf(listOf(2, 0),  // 1
				       listOf(2, 2)), // 2
				setOf(2),
				{ c: Char ->
					when {
						c.isD() -> 0
						c.isd() -> 1
						else    -> -1
					}
				}
			),
			TokenType.Key to Triple(//  @  D  d  {  }  e    //
				listOf(listOf(2, 0, 0, 0, 0, 0),  // 1 -> ε
				       listOf(0, 3, 0, 4, 0, 0),  // 2 -> @
				       listOf(0, 3, 3, 0, 0, 0),  // 3 -> @ ...
				       listOf(5, 5, 5, 5, 0, 5),  // 4 -> @{
				       listOf(5, 5, 5, 5, 6, 5),  // 5 -> @{ ...
				       listOf(0, 0, 0, 0, 0, 0)), // 6 -> @{ ... }
				setOf(3, 6),
				{ c: Char ->
					when {
						c == '@' -> 0
						c.isD()  -> 1
						c.isd()  -> 2
						c == '{' -> 3
						c == '}' -> 4
						else     -> 5
					}
				}
			),
			TokenType.Number to Triple(//0   1   d   b   h  x   .    //
				listOf(listOf(+2, 11, 11, +0, +0, 0, 12),  // 1  ->
				       listOf(11, 11, 11, +3, +0, 7, 12),  // 2  ->
				       listOf(+4, +4, +0, +0, +0, 0, +5),  // 3  ->
				       listOf(+4, +4, +0, +0, +0, 0, +5),  // 4  ->
				       listOf(+6, +6, +0, +0, +0, 0, +0),  // 5  ->
				       listOf(+6, +6, +0, +0, +0, 0, +0),  // 6  ->
				       listOf(+8, +8, +8, +8, +8, 0, +9),  // 7  ->
				       listOf(+8, +8, +8, +8, +8, 0, +9),  // 8  ->
				       listOf(10, 10, 10, 10, 10, 0, +0),  // 9  ->
				       listOf(10, 10, 10, 10, 10, 0, +0),  // 10 ->
				       listOf(11, 11, 11, +0, +0, 0, 12),  // 11 ->
				       listOf(13, 13, 13, +0, +0, 0, +0),  // 12 ->
				       listOf(13, 13, 13, +0, +0, 0, +0)), // 13 ->
				setOf(4, 6, 8, 10, 11, 13),
				{ c: Char ->
					when (c.toLowerCase()) {
						'0'         -> 0  // 0
						'1'         -> 1  // 1
						in '0'..'9' -> 2  // d
						'b'         -> 3  // b
						in 'a'..'f' -> 4  // h
						'x'         -> 5  // x
						'.'         -> 6  // .
						else        -> -1 // e
					}
				}
			)
		)
	}
}