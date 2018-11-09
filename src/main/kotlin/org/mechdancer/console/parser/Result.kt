package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.Status.*

/**
 * 指令执行结果
 * @param what 匹配结果
 *             [0, +∞) -> 匹配失败时的位置
 *             -1      -> 执行成功
 *             -2      -> 执行失败
 *             -3      -> 指令不全
 */
data class Result(
	val what: Int,
	val data: Any?
) {
	//指令执行结果
	constructor(pair: Pair<Boolean, Any?>)
		: this(if (pair.first) -1 else -2, pair.second)

	/** 是否成功 */
	val positive get() = what == -1

	/** 状态类别 */
	val status
		get() = when (what) {
			-1   -> Success
			-2   -> Failure
			-3   -> Incomplete
			else -> Error
		}

	/**
	 * 指令执行状态
	 */
	enum class Status {
		Success,   //执行成功
		Failure,   //执行异常
		Error,     //无法匹配
		Incomplete //指令不全
	}
}
