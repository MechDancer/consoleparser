package org.mechdancer.console.parser

import org.mechdancer.console.parser.TokenType.*
import org.mechdancer.console.parser.TokenType.Number

typealias Sentence = List<Token>          //句子
typealias Function = (Sentence) -> Result //执行器

//解析正则
private infix fun String.match(token: TokenType) =
	token.pattens.any { it.matchEntire(this) != null }

//类别判定
private fun determine(text: String): TokenType =
	when {
		text match Integer -> Integer
		text match Number  -> Number
		text match Note    -> Note
		text match Final   -> Final
		text match Key     ->
			when (text.substring(1 until text.length - 1)) {
				"int"  -> Integer
				"num"  -> Number
				"word" -> Word
				"sign" -> Sign
				else   -> Key
			}
		text.length == 1 && text match Sign
		                   -> Sign
		else               -> Word
	}

/** 拆分 */
fun String.split() =
	trim().split(Regex("\\s+"))
		.map { Token(it, determine(it)) }

/** 清理 */
fun Sentence.cleanup() =
	asSequence()
		.filter { it.type != Note }
		.takeWhile { it.type != Final }
		.toMutableList()
		.apply { add(Token("", Final)) }
		.toList()
