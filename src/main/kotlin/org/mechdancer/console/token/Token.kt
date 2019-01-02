package org.mechdancer.console.token

/**
 * 一个词定义为其类别与含义
 * 当只需判断类别时含义记为空
 * @param type 词性
 * @param data 词义
 */
data class Token<T>(val type: Type, val data: T) {
    /** 按类型显示 */
    override fun toString() =
        (data as? String)?.takeIf(String::isNotBlank) ?: "[$type]" //?: data.toString()

    /** 判断另一词是否与此例匹配 */
    infix fun match(actual: Token<*>) =
        when (type) {
            Type.Number -> actual.type == Type.Number
            Type.Sign   -> actual.type == Type.Sign && ((data as String).isBlank() || actual.toString() == data)
            Type.Word   -> actual.type == Type.Word && ((data as String).isBlank() || actual.toString() == data)
            Type.Note   -> throw IllegalArgumentException("note appeared in rule")
            Type.Key    -> true
        }

    /** 判断另一词是否不与此例匹配 */
    infix fun notMatch(actual: Token<*>) = !match(actual)

    /**
     * 标签类别
     */
    enum class Type {
        Number,
        Sign,
        Word,
        Note,
        Key
    }
}
