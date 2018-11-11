import org.junit.Assert
import org.junit.Test
import org.mechdancer.console.newScanner.Scanner
import org.mechdancer.console.token.TokenType.*
import org.mechdancer.console.token.TokenType.Number

class TestNewScanner {
	// （匹配长度 - 是否完整） - 测试用例
	private infix fun List<Pair<Pair<Int, Boolean>, String>>.test(scanner: Scanner<Char>) {
		for ((target, test) in this) {
			for (char in test) scanner(char)
			Assert.assertEquals(target, scanner.length to scanner.complete)
			scanner.reset()
		}
	}

	@Test
	fun testNote() {
		listOf(
			0 to false to "t/t",
			0 to false to "123",
			7 to true to "//123*/",
			4 to true to "/**/",
			3 to false to "/*/",
			4 to true to "/**/*") test Scanner[Note]!!
	}

	@Test
	fun testWord() {
		listOf(
			4 to true to "word",
			0 to false to ":help",
			0 to false to "123",
			4 to true to "a123*/",
			1 to true to "a + b",
			2 to true to "_b",
			0 to false to "      ") test Scanner[Word]!!
	}

	@Test
	fun testKey() {
		listOf(
			9 to true to "@asdf1234",
			1 to false to "@1324",
			0 to false to "1223",
			1 to false to "@     }",
			1 to false to "@",
			5 to false to "@{asd",
			8 to true to "@{a s d}") test Scanner[Key]!!
	}

	@Test
	fun testNumber() {
		listOf(
			5 to true to "12345",
			0 to false to "@1324",
			5 to true to "01223",
			5 to false to "0122.",
			4 to true to "0123_",
			6 to true to "0b1001",
			6 to true to "0xad0f",
			6 to true to "3.1415",
			6 to true to "0x.222",
			6 to true to "0.2333",
			3 to true to ".23.33") test Scanner[Number]!!
	}

	@Test
	fun testSign() {
		listOf(
			3 to true to "===",
			1 to true to "!",
			0 to false to "123",
			1 to true to "()",
			2 to true to "<-",
			3 to true to "....") test Scanner[Sign]!!
	}
}