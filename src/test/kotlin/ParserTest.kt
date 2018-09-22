import org.mechdancer.console.parser.Parser
import org.mechdancer.console.parser.numbers

fun main(args: Array<String>) {
	val parser = Parser().apply {
		this["hello world"] = { true to "hello commander" }
		this["hi"] = { true to "hi" }
		this["{num} + {num}"] = { true to numbers.sum() }
	}
	while (true) readLine()?.let(parser::invoke)
}
