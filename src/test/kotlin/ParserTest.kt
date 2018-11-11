import org.mechdancer.console.parser.*

fun main(args: Array<String>) {
	var flag = true
	val parser = buildParser {
		this["hello"] = { "hello" }
		this["hello world"] = { "hello master" }
		this["hello computer"] = { "hello commander" }
		this["hi"] = { "hi" }
		this["@num / @num"] = { numbers.first() / numbers.last() }
		this["print @word"] = { words[1] }
		this["A=@num"] = { numbers.first() }
		this["B=@num"] = { numbers.first() }
		this["exit"] = {
			flag = false
			"bye~"
		}
	}

	while (flag) readLine()
		?.let(parser::invoke)
		?.map(::feedback)
		?.forEach(::display)
}
