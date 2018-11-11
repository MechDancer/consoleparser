package org.mechdancer.console.parser

import org.mechdancer.console.parser.Parser.*
import org.mechdancer.console.token.Token
import org.mechdancer.console.token.Token.Type.*
import org.mechdancer.console.token.Token.Type.Number
import java.io.ByteArrayOutputStream
import java.io.PrintStream

/** 句子是词的列表 */
typealias Sentence = List<Token<*>>

/** 操作基于指令 */
typealias Action = Sentence.() -> Any?

/** 取出句子中所有数字 */
val Sentence.numbers
	get() = filter { it.type == Number }.map { it.data as Double }

/** 取出句子中所有符号 */
val Sentence.signs
	get() = filter { it.type == Sign }.map { it.data as String }

/** 取出句子中所有单词 */
val Sentence.words
	get() = filter { it.type == Word }.map { it.data as String }

/** 取出句子中所有关键字 */
val Sentence.keys
	get() = filter { it.type == Key }.map { it.data as String }

/** 构造解析器 */
fun buildParser(block: Parser.() -> Unit) =
	Parser().apply(block)

/** 组织反馈信息 */
fun feedback(result: Map.Entry<Sentence, *>): Pair<Boolean, *> {
	val (sentence, data) = result
	return when (data) {
		!is Throwable           -> true to data
		is CannotMatchException -> false to buildString {
			append("invalid command: ")
			sentence.forEachIndexed { i, it ->
				append(if (i == data.where) "> $it < " else "$it ")
			}
			appendln()
			append("you mean \"${data.rule.joinToString(" ")}\"?")
		}
		is IncompleteException  -> false to buildString {
			append("incomplete command: ")
			append(sentence.joinToString(" "))
			appendln(" ...")
			append("you mean \"${data.rule.joinToString(" ")}\"?")
		}
		is ParseException       -> false to data.message
		else                    -> false to buildString {
			appendln("${data.javaClass.simpleName} thrown during running")
			ByteArrayOutputStream()
				.apply { data.printStackTrace(PrintStream(this)) }
				.toByteArray()
				.let { String(it) }
				.let(this::append)
		}
	}
}

/** 显示指令反馈 */
fun display(feedback: Pair<Boolean, Any?>) =
	(if (feedback.first) System.out else System.err)
		.println(feedback.second)