package org.mechdancer.console.parser

import org.mechdancer.console.parser.Result.State.*

/**
 * 指令执行结果
 */
data class Result(
	val what: State,
	val where: Int = 0,
	val info: String = ""
) {
	//指令执行结果
	constructor(info: String, success: Boolean = true)
		: this(if (success) Success else Failure, 0, info)

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
