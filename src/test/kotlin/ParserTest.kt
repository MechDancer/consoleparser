import org.mechdancer.console.parser.Parser

fun main(args: Array<String>) {
	val parser = Parser().apply {
		this["hello world"] = { true to "hello commander" }
		this["hi"] = { true to "hi" }
	}
	while (true) readLine()?.let(parser::invoke)
}
