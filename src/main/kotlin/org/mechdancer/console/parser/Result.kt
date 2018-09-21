package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.Failure
import org.mechdancer.console.parser.Result.State.Success

/**
 * 指令执行结果
 */
data class Result(
	val what: State,
	val info: String = "",
	val where: Int = 0
) {
	//指令执行结果
	constructor(pair: Pair<Boolean, String>)
		: this(if (pair.first) Success else Failure, pair.second)

	/** 是否成功 */
	val positive get() = what == Success

	/**
	 * 指令执行状态
	 */
	enum class State {
		Success,   //执行成功
		Failure,   //执行异常
		Error,     //无法匹配
		Incomplete //指令不全
	}
}
