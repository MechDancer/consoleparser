import org.junit.Assert
import org.junit.Test
import org.mechdancer.console.newScanner.Scanner
import org.mechdancer.console.token.TokenType.*

class TestNewScanner {
	// （匹配长度 - 是否完整） - 测试用例
	private infix fun List<Pair<Pair<Int, Boolean>, String>>.test(scanner: Scanner) {
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
}