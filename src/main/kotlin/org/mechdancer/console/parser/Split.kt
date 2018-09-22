package org.mechdancer.console.parser

import org.mechdancer.console.parser.TokenType.*
import org.mechdancer.console.parser.TokenType.Number

//解析正则
private infix fun String.match(token: TokenType) =
	token.pattens.any { it.matchEntire(this) != null }

//类别判定
private fun determine(text: String, erase: Boolean): Token<*>? =
	when {
		text match Integer -> Token(Integer, text.takeUnless { erase }?.toInt())
		text match Number  -> Token(Number, text.takeUnless { erase }?.toDouble())
		text match Note    -> if (!erase) Token(Note, text) else null
		text match Final   -> Token(Final, text.takeUnless { erase })
		text match Key     ->
			when (text.substring(1 until text.length - 1)) {
				"int"  -> Token<Unit>(Integer)
				"num"  -> Token(Number)
				"word" -> Token(Word)
				"sign" -> Token(Sign)
				else   -> Token(Key)
			}
		text.length == 1 && text match Sign
		                   -> Token(Sign, text)
		else               -> Token(Word, text)
	}

private fun Sequence<Token<*>>.changeTail() =
	takeWhile { it.type != Final }
		.toMutableList()
		.apply { add(Token<Unit>(Final)) }
		.toList()

/** 拆分 */
fun String.split(erase: Boolean) =
	trim().split(Regex("\\s+")).mapNotNull { determine(it, erase) }

/** 擦除 */
fun String.erase() =
	split(true)
		.asSequence()
		.changeTail()

/** 整理 */
fun String.cleanup() =
	split(false)
		.asSequence()
		.filter { it.type != Note }
		.changeTail()
