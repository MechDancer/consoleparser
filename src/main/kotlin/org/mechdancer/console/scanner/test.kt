package org.mechdancer.console.scanner

fun main(args: Array<String>) {
	val scanner = SignScanner()
	for (c in "<<=")
		scanner.offer(c)
	println(scanner.build())
}
