package org.mechdancer.scanner

/**
 * 标记扫描器
 */
interface TokenScanner<T, R> {
	/**
	 * 向后继续匹配的最少单元数
	 * -1 : 匹配已经失败
	 *  0 : 随时可以结束
	 * +n : 仍需匹配的最少单元数
	 */
	val remain: Int

	/**
	 * 尝试匹配一个单字
	 */
	fun offer(e: T)

	/**
	 * 重新匹配
	 * 清除当前缓存，可同时存入一个新的单元
	 */
	fun reset(e: T? = null)

	/**
	 * 构造标签
	 * @param erase 擦除具体内容以构造规则
	 */
	fun build(erase: Boolean = false): R?
}
