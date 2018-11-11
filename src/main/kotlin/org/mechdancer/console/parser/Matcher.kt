package org.mechdancer.console.parser

/**
 * 匹配结果
 * @param length  匹配长度
 * @param success 是否匹配成功
 */
data class Matcher(
	val length: Int,
	val success: Boolean
)
