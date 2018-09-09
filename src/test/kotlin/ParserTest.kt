import org.mechdancer.console.parser.Parser
import org.mechdancer.console.parser.Result

fun main(args: Array<String>) {
	val parser = Parser()
	parser += "#1: hello world"
	parser.library[1] = {
		Result("hello commander")
	}
	while (true) {
		readLine()?.let(parser::invoke)
	}
}
