import org.mechdancer.console.parser.Parser
import org.mechdancer.console.parser.integers
import org.mechdancer.console.parser.numbers
import org.mechdancer.console.parser.words

fun main(args: Array<String>) {
	val parser = Parser().apply {
		this["hello"] = { true to "hello" }
		this["hello world"] = { true to "hello master" }
		this["hello computer"] = { true to "hello commander" }
		this["hi"] = { true to "hi" }
		this["{num} + {num}"] = { true to numbers.sum() }
		this["print {word}"] = { true to words.first() }
		this["print {int}"] = { true to integers.first() }
	}

	while (true) readLine()?.let(parser::invoke)
}
