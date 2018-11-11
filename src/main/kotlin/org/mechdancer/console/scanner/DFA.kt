package org.mechdancer.console.scanner

import kotlin.math.abs

/**
 * 确定性自动机扫描器
 * @param table  状态转移表（*行序号从 1 开始*，*0 表示错误状态*）
 * @param ending 合法结束状态
 * @param map    字符到转移表列序号的映射关系（*-1 表示无效字符*）
 */
class DFA<T>(
	private val table: List<List<Int>>,
	private val ending: Set<Int>,
	private val map: (T) -> Int
) : Scanner<T> {
	// 当前状态（序号）
	// 正数表示正在匹配
	// 负数表示匹配失败前的最后状态，即能匹配的部分的结束状态
	private var state = 1

	override var length: Int = 0; private set
	override val complete get() = abs(state) in ending

	override operator fun invoke(char: T) {
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

	override fun reset() {
		state = 1
		length = 0
	}
}