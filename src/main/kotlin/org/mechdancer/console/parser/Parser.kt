package org.mechdancer.console.parser

import org.mechdancer.console.newScanner.scan
import org.mechdancer.console.token.Token
import kotlin.math.min

/**
 * 语义分析和执行器
 */
class Parser {
	//用户指令集
	private val userLibrary = mutableMapOf<Sentence, Action>()
	//内部指令集
	private val coreLibrary = mapOf<Sentence, Action>(
		scan(":help") to { user: Sentence ->
			buildString {
				((userLibrary match user)
					 .filter { it.value.length == user.size }
					 .takeIf { it.isNotEmpty() }
				 ?: userLibrary)
					.keys
					.forEach { appendln(it.joinToString(" ")) }
			}
		}
	)

	/** 指令分析 */
	private fun analyze(sentence: Sentence) =
		if (sentence.firstOrNull()?.toString() == ":")
			sentence.take(2) to sentence.drop(2)
		else
			listOf<Token<*>>() to sentence

	/** 添加规则和动作 */
	operator fun set(example: String, action: Action) {
		scan(example)
			// 有效
			.takeIf(Sentence::isNotEmpty)
			// 不重复
			?.takeIf { rule -> userLibrary.none { equal(it.key.size, it.key[rule], rule.size) } }
			// 存储
			?.let { userLibrary[it] = action }
		?: throw RuntimeException("rule \"$example\" already exist")
	}

	fun <T> T.letCatching(block: (T) -> Any?) =
		let {
			try {
				block(it)
			} catch (e: Throwable) {
				e
			}
		}

	// 解析并执行指令
	private fun parse(sentence: Sentence): Any? {
		//指令分析
		val (core, user) = analyze(sentence)
		//用户指令匹配
		return if (core.isNotEmpty()) {
			val cMatchers = coreLibrary match core
			cMatchers.successOrNull()?.let(coreLibrary::get)?.letCatching { it(user) }
			?: cMatchers cannotMatch sentence
			?: ParseException("unknown core command \"${core.joinToString(" ")}\"")
		} else {
			val uMatchers = userLibrary match user
			uMatchers.successOrNull()?.let(userLibrary::get)?.letCatching { it(user) }
			?: uMatchers cannotMatch sentence
			?: ParseException("unknown command \"${user.joinToString(" ")}\"")
		}
	}

	/** 解析并执行指令 */
	operator fun invoke(script: String): Map<Sentence, Any?> =
		script.reader()
			.readLines()
			.map(::scan)
			.filterNot(Sentence::isEmpty)
			.associate { it to parse(it) }

	/**
	 * 匹配结果
	 * @param length  匹配长度
	 * @param success 是否匹配成功
	 */
	private data class Matcher(
		val length: Int,
		val success: Boolean
	)

	class CannotMatchException(val rule: Sentence, val where: Int) : IllegalArgumentException()
	class IncompleteException(val rule: Sentence) : IllegalArgumentException()
	class ParseException(msg: String) : IllegalArgumentException(msg)

	private companion object {
		// 判断多个元素相等
		@JvmStatic
		fun <T> equal(vararg list: T) = list.distinct().size == 1

		// 匹配单个规则
		@JvmStatic
		operator fun Sentence.get(sentence: Sentence): Int {
			val length = min(lastIndex, sentence.lastIndex)
			for (i in 0..length)
				if (this[i] notMatch sentence[i]) return i
			return length + 1
		}

		// 匹配规则集
		@JvmStatic
		infix fun Map<Sentence, Action>.match(sentence: Sentence) =
			mapValues {
				val rule = it.key
				val length = rule[sentence]
				Matcher(length, equal(sentence.size, rule.size, length))
			}

		// 成功匹配的规则或空
		@JvmStatic
		fun Map<Sentence, Matcher>.successOrNull() =
			filterValues(Matcher::success)
				.keys
				.singleOrNull()

		// 从最优匹配信息产生匹配异常
		@JvmStatic
		infix fun Map<Sentence, Matcher>.cannotMatch(sentence: Sentence) =
			maxBy { it.value.length }
				?.takeIf { it.value.length > 0 }
				?.let {
					val (rule, matcher) = it
					if (sentence.size == matcher.length && rule.size > sentence.size)
						IncompleteException(rule)
					else
						CannotMatchException(rule, matcher.length)
				}
	}
}
