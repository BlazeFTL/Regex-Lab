package com.example.model

enum class RegexFlag(
    val code: Char,
    val flagName: String,
    val description: String,
    val option: RegexOption?
) {
    GLOBAL('g', "Global", "Find all matches rather than stopping after first match", null),
    CASE_INSENSITIVE('i', "Case Insensitive", "Ignore case when matching letters", RegexOption.IGNORE_CASE),
    MULTILINE('m', "Multiline", "^ and $ match start/end of line, not entire string", RegexOption.MULTILINE),
    SINGLELINE('s', "Single Line (DotAll)", ". matches any character including newline", RegexOption.DOT_MATCHES_ALL)
}
