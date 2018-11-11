package org.mechdancer.console.parser

import org.mechdancer.console.scanner.scan
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
		},
		scan(":do") to { user: Sentence ->
			val matchers = userLibrary match user
			matchers.successOrNull()?.let(userLibrary::get)?.swallow { it(user) }
			?: matchers cannotMatch user
			?: ParseException("unknown command \"${user.joinToString(" ")}\"")
		}
	)

	/**
	 * 指令分析
	 * 区分内核指令和用户指令
	 * @param sentence 已分词的句子
	 * @return 内核部分 - 用户部分
	 */
	private fun analyze(sentence: Sentence) =
		if (sentence.firstOrNull()?.toString() == ":")
			sentence.take(2) to sentence.drop(2)
		else
			listOf<Token<*>>() to sentence

	/**
	 * 添加规则和动作
	 * @param example 规则范例文本
	 * @param action  对应动作
	 */
	operator fun set(example: String, action: Action) {
		scan(example)
			// 有效
			.takeIf(Sentence::isNotEmpty)
			// 不重复
			?.takeIf { rule -> userLibrary.none { equal(it.key.size, it.key[rule], rule.size) } }
			// 存储
			?.let { userLibrary[it] = action }
		?: throw RuntimeException("rule \"$example\" is meaningless or already exist")
	}

	// 解析并执行指令
	private fun parse(sentence: Sentence): Any? {
		//指令分析
		val (core, user) = analyze(sentence)
		//用户指令匹配
		return if (core.isNotEmpty()) {
			val matchers = coreLibrary match core
			matchers.successOrNull()?.let(coreLibrary::get)?.swallow { it(user) }
			?: matchers cannotMatch sentence
			?: ParseException("unknown core command \"${core.joinToString(" ")}\"")
		} else {
			val matchers = userLibrary match user
			matchers.successOrNull()?.let(userLibrary::get)?.swallow { it(user) }
			?: matchers cannotMatch sentence
			?: ParseException("unknown command \"${user.joinToString(" ")}\"")
		}
	}

	/**
	 * 解析并执行指令
	 * @param script 待解析脚本，用换行分句
	 * @return 分句分词的指令及其执行结果
	 */
	operator fun invoke(script: String) =
		script.reader()
			.readLines()
			.map(::scan)
			.filterNot(Sentence::isEmpty)
			.map { it to parse(it) }

	/**
	 * 匹配结果
	 * @param length  匹配长度
	 * @param success 是否匹配成功
	 */
	private data class Matcher(val length: Int, val success: Boolean)

	/**
	 * 无法匹配
	 * @param rule  最优匹配规则
	 * @param where 匹配到的最长长度
	 */
	class CannotMatchException(val rule: Sentence, val where: Int) : IllegalArgumentException()

	/**
	 * 指令不全
	 * @param rule 最优匹配规则
	 */
	class IncompleteException(val rule: Sentence) : IllegalArgumentException()

	/**
	 * 解析时异常
	 * @param msg 异常信息
	 */
	class ParseException(msg: String) : IllegalArgumentException(msg)

	private companion object {
		// 吞下异常
		@JvmStatic
		fun <T> T.swallow(block: (T) -> Any?) =
			try {
				block(this)
			} catch (e: Throwable) {
				e
			}

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
