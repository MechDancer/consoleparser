import org.mechdancer.console.parser.Parser
import org.mechdancer.console.parser.Result

fun main(args: Array<String>) {
	val parser = Parser().apply {
		this["hello world"] = { Result("hello commander") }
		this["hi"] = { Result("hi") }
	}
	while (true) readLine()?.let(parser::invoke)
}
