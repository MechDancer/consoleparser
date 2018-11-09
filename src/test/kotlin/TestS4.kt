import org.junit.Assert
import org.junit.Test
import org.mechdancer.console.scanner.defaultScanners
import org.mechdancer.console.scanner.scanBy
import org.mechdancer.console.token.Token
import org.mechdancer.console.token.TokenType.*
import org.mechdancer.console.token.TokenType.Number

class TestS4 {
	@Test
	fun testS4() {
		val source = """
			0 x
			adfg
			@int
			12345.76
			+-67890 0xff  //this is a note
		""".trimIndent()
		val result = source scanBy defaultScanners
			.also { it.forEach(::println) }
		Assert.assertEquals(listOf(
			Token(Integer, 0),
			Token(Word, "x"),
			Token(Word, "adfg"),
			Token<Unit>(Integer),
			Token(Number, 12345.76),
			Token(Sign, "+"),
			Token(Integer, -67890),
			Token(Integer, 255)
		), result)
	}
}
