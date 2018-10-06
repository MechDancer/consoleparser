import org.junit.Assert
import org.junit.Test
import org.mechdancer.console.parser.Token
import org.mechdancer.console.parser.TokenType.*
import org.mechdancer.console.parser.TokenType.Number
import org.mechdancer.console.parser.splitter.defaultSet
import org.mechdancer.console.parser.splitter.splitBy

class TestS4 {
	@Test
	fun testS4() {
		val source = """
			adfg
			(* int *)
			12345.76
			+-67890 0xff  "hello world"(- this is a note
		""".trimIndent()
		Assert.assertEquals(listOf(
			Token(Word, "adfg"),
			Token<Unit>(Integer),
			Token(Number, 12345.76),
			Token(Sign, "+-"),
			Token(Integer, 67890),
			Token(Integer, 255),
			Token(Word, "hello world")
		), source splitBy defaultSet)
	}
}
