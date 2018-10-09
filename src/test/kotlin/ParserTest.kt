import org.mechdancer.console.parser.buildParser
import org.mechdancer.console.parser.integers
import org.mechdancer.console.parser.numbers
import org.mechdancer.console.parser.words

fun main(args: Array<String>) {
	var flag = true
	val parser = buildParser {
		this["hello"] = { true to "hello" }
		this["hello world"] = { true to "hello master" }
		this["hello computer"] = { true to "hello commander" }
		this["hi"] = { true to "hi" }
		this["@num + @num"] = { true to numbers.first() * numbers.last() }
		this["print @word"] = { true to words[1] }
		this["print @int"] = { true to integers.first() }
		this["save @{how are you}"] = { true to integers.first() }
		this["quit"] = {
			flag = false
			true to "bye~"
		}
	}

	while (flag)
		readLine()
			?.let(parser::invoke)
			?.forEach {
				val (success, info) = it
				(if (success) System.out else System.err).println(info)
			}
}
