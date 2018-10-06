package org.mechdancer.console.core

/**
 * 累积扫描器
 */
abstract class TokenScanner<T, R> {
	//单元缓存
	protected val buffer = mutableListOf<T>()

	/**
	 * 匹配长度
	 */
	val size get() = buffer.size

	/**
	 * 能否继续匹配
	 */
	private var access = true

	/**
	 * 重新匹配
	 */
	fun reset(char: T? = null) {
		buffer.clear()
		access = true
		char?.let(::offer)
	}

	/**
	 * 判断能否匹配
	 */
	abstract fun check(char: T): TokenMatchResult

	/**
	 * 尝试匹配一个单字
	 * @return 是否匹配这个字
	 */
	fun offer(char: T) =
		if (access) {
			val (current, next) = check(char)
			if (current) buffer += char
			access = next
			current
		} else false

	/**
	 * 构造标签
	 */
	abstract fun build(erase: Boolean = false): R?
}
