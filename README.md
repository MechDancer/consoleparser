# 控制台指令解析器

## 使用说明

### 步骤一 构造规则和动作

```kotlin
var flag = true
val parser = buildParser {
		this["hello"] = { true to "hello" }
		this["hello world"] = { true to "hello master" }
		this["hello computer"] = { true to "hello commander" }
		this["hi"] = { true to "hi" }
		this["(* num *) + (* num *)"] = { true to numbers.sum() }
		this["print (* word *)"] = { true to words[1] }
		this["print (* int *)"] = { true to integers.first() }
		this["quit"] = {
			flag = false
			true to "bye~"
		}
	}
```

如以上代码所示，请使用 `buildParser` 函数构造解析器。此函数将构造并打开一个空白的解析器，并接受一个操作解析器的代码块。

要向解析器添加 *规则-动作* 对，请使用 `Parser.set(rule, action)` 操作符。在已打开解析器的代码块中写作 `this[rule] = action`。其中，`rule` 是规则的示例字符串，可使用 `(* int *)` `(* num *)` `(* word *)` `(* sign *)` 来匹配任意的整数、有理数、单词和符号。

* 注意，用负号标注的负数会被视作一个符号和一个数字，因此，要匹配负数，请声明为 `(* sign *)(* int *)` 或 `(* sign *)(* num*)` ，并在使用时添加 `+` 以匹配正数。

动作代码块内打开了用户输入的句子分词后的结果，使用 `this[index]` 即可按序号索引。代码块需要返回一个 `Pair<Boolean, Any?>` 。前者用于指示操作是否成功，后者是将在控制台显示的信息，不必是 `String`，因为后台会调用一次 `toString()`。代码块中可以获取外部变量的引用。

若有两个相同的规则，解析器的构造将发生异常。`:` 引起的指令被定义为内部指令，因此定义这样的指令将在运行时导致错误。

### 步骤二 解析指令

```kotlin
while (flag) readLine()?.let(parser::invoke)
```

解析指令很简单。通过引用调用 `Parser.invoke(String)` 即可，其中没有什么运行时的限制或约定。输入 `:help` 可打印解析器已知的所有规则，也可以用 `:help` 引起一部分指令以查看与之匹配的规则。

多行的字符串输入会被视作脚本。指令会在换行处进行分割，并逐条进行解释和执行。因此，定义包含换行的规则尽管是合法的，但永远无法被匹配。