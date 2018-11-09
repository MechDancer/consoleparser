package org.mechdancer.console.scanner

import org.mechdancer.console.token.Token
import org.mechdancer.console.token.TokenType
import org.mechdancer.console.token.TokenType.Integer

/**
 * 数字扫描器
 */
class NumberScanner : CharScanner {
	//内部状态
	private var state = default

	override var remain = 1
		private set

	override fun offer(e: Char) {
		if (remain-- < 0) return
		val (remain, state) = offer(e, state)
		this.state = if (remain < this.remain) default else state
		this.remain = remain
	}

	override fun reset(e: Char?) {
		state = default
		remain = 1
		e?.let(::offer)
	}

	override fun build() =
		state
			.takeIf { (remain == 0 || remain == -1) && !it.signUnknown() }
			?.number
			?.let { num ->
				(num as? Int)
					?.let { Token(Integer, it * state.sign) }
					?: (num as? Double)
						?.let { Token(TokenType.Number, it * state.sign) }
			}

	private data class State(
		//符号
		val sign: Byte,
		//进制
		val format: Int,
		//基数
		val base: Number,
		//数字
		val number: Number
	) {
		// 判断状态：定义符号前
		fun signUnknown() = sign == byte_0

		// 判断状态：定义进制前
		fun formatUnknown() = format == -1

		// 判断状态：确定进制前
		fun baseUnknown() = base == 0

		// 当前状态下的有效字符集
		val set
			get() = when (format) {
				-1, 0 -> intSet
				2     -> binSet
				10    -> intSet
				16    -> hexSet
				else  -> throw RuntimeException()
			}

		// 添加小数点
		fun dot() = copy(base = base.toDouble() / format)

		// 插入字符(先判定是否可插入)
		fun insert(char: Char): State {
			val value = when (char) {
				in intSet -> char.toInt() - char_0
				in hexSet -> char.toInt() - char_a + 10
				else      -> throw RuntimeException()
			}
			return when (base) {
				is Int    -> copy(number = number.toInt() * format + value)
				is Double -> copy(number = number.toDouble() + base * value, base = base / format)
				else      -> throw RuntimeException()
			}
		}

		companion object {
			private const val byte_0 = 0.toByte()
			private const val char_0 = '0'.toInt()
			private const val char_a = 'a'.toInt()

			private val binSet = setOf('0', '1')
			private val intSet = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
			private val hexSet = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
		}
	}

	private companion object {
		val default = State(0, -1, 0, 0)
		val failed = -1 to default

		@JvmStatic
		tailrec fun offer(e: Char, state: State): Pair<Int, State> =
			when {
				state.signUnknown()   -> when (e) {
					'+'               -> +1 to state.copy(sign = +1)
					'-'               -> +1 to state.copy(sign = -1)
					'.', in state.set -> offer(e, state.copy(sign = +1))
					else              -> -1 to state
				}
				state.formatUnknown() -> when (e) {
					'0'               -> +0 to state.copy(format = 0)
					'.', in state.set -> offer(e, state.copy(format = 10))
					else              -> -1 to state
				}
				state.baseUnknown()   -> when (e) {
					'b'               -> +1 to state.copy(format = 2, base = 1)
					'x'               -> +1 to state.copy(format = 16, base = 1)
					'.', in state.set -> offer(e, state.copy(format = 10, base = 1))
					else              -> -1 to state
				}
				else                  -> when (e) {
					'.'          -> if (state.base !is Double) +1 to state.dot() else failed
					in state.set -> +0 to state.insert(e)
					else         -> -1 to state
				}
			}
	}
}
