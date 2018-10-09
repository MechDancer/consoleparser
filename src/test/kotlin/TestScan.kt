import org.mechdancer.console.scanner.defaultScanners
import org.mechdancer.console.scanner.scanBy

fun main(args: Array<String>) {
	("3.15*3" scanBy defaultScanners)
		.forEach {
			println("$it: ${it.type}")
		}

	("@num    *   @num" scanBy defaultScanners)
		.forEach {
			println("$it: ${it.type}")
		}

	("""
		3 + 5
		4 - 7
		3.5 *-1.7
		(@num + @int)*100.0
		hello 123 !!
		+-*/
		//this is a note
		/* ? */ this is not note
	""".trimIndent() scanBy defaultScanners)
		.forEach {
			println("$it: ${it.type}")
		}
}
