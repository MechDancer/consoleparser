package org.mechdancer.console.parser

class CannotMatchException(val rule: Rule, val where: Int) : IllegalArgumentException()
class IncompleteException(val rule: Rule) : IllegalArgumentException()
class ParseException(msg: String) : IllegalArgumentException(msg)