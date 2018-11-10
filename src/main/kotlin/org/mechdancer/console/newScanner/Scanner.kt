package org.mechdancer.console.newScanner

import org.mechdancer.console.token.TokenType
import org.mechdancer.console.token.TokenType.*
import kotlin.math.abs

/**
 * 确定性自动机扫描器
 * @param table  状态转移表（*行序号从 1 开始*，*0 表示错误状态*）
 * @param ending 合法结束状态
 * @param map    字符到转移表列序号的映射关系（*-1 表示无效字符*）
 */
class Scanner(
	private val table: List<List<Int>>,
	private val ending: Set<Int>,
	private val map: (Char) -> Int
) {
	// 当前状态（序号）
	// 正数表示正在匹配
	// 负数表示匹配失败前的最后状态，即能匹配的部分的结束状态
	private var state = 1

	/**
	 * 最长匹配长度
	 */
	var length: Int = 0; private set

	/**
	 * 最长匹配长度内字符匹配是否完整
	 */
	val complete get() = abs(state) in ending

	/**
	 * 匹配一个字符
	 * @param char 将进行匹配的字符
	 */
	operator fun invoke(char: Char) {
		if (state > 0) {
			state = map(char)
				// 是一个有意义的字符
				.takeIf { it >= 0 }
				// 查找转移表
				?.let { table[state - 1][it] }
				// 不导致错误状态
				?.takeIf { it != 0 }
				// 匹配长度增加
				?.also { ++length }
				// 否则标记匹配结束
				?: -state
		}
	}

	/**
	 * 重置内部状态
	 */
	fun reset() {
		state = 1
		length = 0
	}

	companion object {
		@JvmStatic
		operator fun get(type: TokenType) =
			parameters[type]?.let {
				val (table, ending, map) = it
				Scanner(table, ending, map)
			}

		@JvmStatic
		private fun Char.isD() = isLetter() || this == '_'

		@JvmStatic
		private fun Char.isd() = isDigit()

		private val parameters = mapOf(
			Note to Triple(// 0  1  2    //
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
			Word to Triple(// 0  1    //
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
			Key to Triple(//  0  1  2  3  4  5    //
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
			)
		)
	}
}