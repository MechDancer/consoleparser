package org.mechdancer.console.scanner

import org.mechdancer.console.parser.Sentence
import org.mechdancer.console.token.Token
import org.mechdancer.console.token.Token.Type
import org.mechdancer.console.token.Token.Type.*
import org.mechdancer.console.token.Token.Type.Number

private infix fun <T> Iterable<Scanner<T>>.offer(char: T) = forEach { it(char) }
private fun <T> Iterable<Scanner<T>>.reset() = forEach(Scanner<T>::reset)
private fun <T> Iterable<Scanner<T>>.summary() = filter(Scanner<T>::complete).maxBy(Scanner<T>::length)

private fun Char.toDigit() = when (this) {
    in '0'..'9' -> this - '0'
    in 'a'..'f' -> this - 'a'
    in 'A'..'F' -> this - 'A'
    else        -> throw IllegalArgumentException("$this is not a digit")
}

private fun String.toDigit(order: Int) =
    fold(.0 to 1.0) { state, it ->
        if (state.second == 1.0)
            if (it == '.') state.first to state.second / order
            else state.first * order + it.toDigit() to 1.0
        else
            state.first + it.toDigit() * state.second to state.second / order
    }.first

private fun String.buildNumber() =
    if (length == 1 || get(1).isDigit()) toDouble()
    else when (get(1)) {
        '.'  -> toDouble()
        'b'  -> drop(2).toDigit(2)
        'x'  -> drop(2).toDigit(16)
        else -> throw RuntimeException()
    }

private fun String.buildKey() =
    if (this[1] == '{') drop(2).dropLast(1).trim() else drop(1)

private infix fun <T> Map<Scanner<T>, Type>.build(text: String) =
    when (val type = keys.summary()
        ?.takeIf { it.length == text.length }
        ?.let(this::get)
        ?: throw RuntimeException("illegal token: $text")
        ) {
        Number -> Token(type, text.buildNumber())
        Sign   -> Token(type, text)
        Word   -> Token(type, text)
        Note   -> Token(type, text.trim())
        Key    -> text.buildKey().let {
            when (it) {
                "num"  -> Token(Number, "[num]")
                "word" -> Token(Word, "[word]")
                "sign" -> Token(Sign, "[sign]")
                else   -> Token(Key, "[key]")
            }
        }
    }

infix fun String.scanBy(pairs: Map<Scanner<Char>, Type>): Sentence {
    val scanners = pairs.keys
    val sentence = mutableListOf<Token<*>>()
    var m = 0 // 当前单词开始位置
    var p = 0 // 当前扫描位置

    while (p < length) {
        scanners offer this[p++]
        if (scanners.any { m + it.length == p }) continue
        if (m < p - 1) sentence += pairs build substring(m, --p)
        m = p
        scanners.reset()
    }
    if (m < p) sentence += pairs build substring(m, p)

    return sentence
}

fun scan(string: String) = string scanBy Type.values().associate { Scanner[it]!! to it }
