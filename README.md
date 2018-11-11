# 控制台指令解析器

[![Download](https://api.bintray.com/packages/mechdancer/maven/consoleparser/images/download.svg)](https://bintray.com/mechdancer/maven/consoleparser/_latestVersion)
[![Build Status](https://travis-ci.com/MechDancer/consoleparser.svg?branch=master)](https://travis-ci.com/MechDancer/consoleparser)

## 使用说明

### 步骤一 构造规则和动作

```kotlin
var flag = true
	val parser = buildParser {
		this["hello"] = { "hello" }
		this["hello world"] = { "hello master" }
		this["hello computer"] = { "hello commander" }
		this["hi"] = { "hi" }
		this["@num / @num"] = { numbers.first() / numbers.last() }
		this["print @word"] = { words[1] }
		this["A=@num"] = { numbers.first() }
		this["exit"] = {
			flag = false
			"bye~"
		}
	}
```

如以上代码所示，请使用 `buildParser` 函数构造解析器。此函数将构造并打开一个空白的解析器，并接受一个操作解析器的代码块。

要向解析器添加 *规则-动作* 对，请使用 `Parser.set(rule, action)` 操作符。在已打开解析器的代码块中写作 `this[rule] = action`。其中，`rule` 是规则的示例字符串，可使用 `@num` `@word` `@sign` 来匹配任意的数字、单词和符号。

* 注意，用负号标注的负数会被视作一个符号和一个数字，因此，要匹配负数，请声明为 `@sign @num`，并在使用时添加 `+` 以匹配正数。

动作代码块内打开了用户输入的句子分词后的结果，使用 `this[index]` 即可按序号索引。同时可使用 `numbers`、`words`、`signs` 和 `keys` 索引所有数字、单词、符号或关键字。代码块接受任意的返回值类型，并将 `Throwable` 类型解释为运行时异常。运行时抛出的任何异常也会被解析器捕获以作为运行结果。代码块中可以获取外部变量的引用。

若有两个相同的规则，解析器的构造将发生异常。`:` 引起的指令被定义为内部指令，因此定义这样的指令将在运行时导致错误。

### 步骤二 解析指令

```kotlin
while (flag) readLine()?.let(parser::invoke)
```

解析指令很简单。通过引用调用 `Parser.invoke(String)` 即可，其中没有什么运行时的限制或约定。输入 `:help` 可打印解析器已知的所有规则，也可以用 `:help` 引起一部分指令以查看与之匹配的规则。

多行的字符串输入会被视作脚本。指令会在换行处进行分割，并逐条进行解释和执行。因此，定义包含换行的规则尽管是合法的，但永远无法被匹配。

### 步骤三 获取或显示结果

```kotlin
result?.map(::feedback)?.forEach(::display)
```

`Parser::invoke` 返回一个列表，其中包含已分词的指令和结果，可以直接使用。若不需要处理运行时异常，可使用内置的 `feedback` 函数进行预处理。`feedback` 函数会将所有运行时异常的信息进行格式化，转换为字符串，对于非异常的结果的不做处理。如果只想在命令行显示结果，可以再调用 `display` 函数将结果通过标准输出流或标准错误流打印到控制台。

## 开始使用

* Gradle
* Maven
* Bintray

您需要将其添加至  [仓库和依赖](https://docs.gradle.org/current/userguide/declaring_dependencies.html) 中。

### Gradle

```groovy
repositories {
    jcenter()
}
dependencies {
    compile 'org.mechdancer:consoleparser:+'
}
```

### Maven

```xml
<repositories>
   <repository>
     <id>jcenter</id>
     <name>JCenter</name>
     <url>https://jcenter.bintray.com/</url>
   </repository>
</repositories>

<dependency>
  <groupId>org.mechdancer</groupId>
  <artifactId>consoleparser</artifactId>
  <version>LATEST</version>
  <type>pom</type>
</dependency>
```

### Bintray

您总可以从 bintray 直接下载 jar： [![Download](https://api.bintray.com/packages/mechdancer/maven/consoleparser/images/download.svg)](https://bintray.com/mechdancer/maven/consoleparser/_latestVersion)

## 示例

要查看示例，请访问 [这里](src/test/kotlin/ParserTest.kt)。