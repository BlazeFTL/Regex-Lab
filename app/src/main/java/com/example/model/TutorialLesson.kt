package com.example.model

enum class TutorialLevel(val displayName: String, val badgeColorHex: Long) {
    EASY("Easy (Beginner)", 0xFF10B981),        // Emerald
    MEDIUM("Medium (Intermediate)", 0xFF3B82F6),  // Blue
    HARD("Hard (Advanced)", 0xFF8B5CF6),        // Purple
    EXTREME("Extremely Difficult", 0xFFEF4444)  // Red/Rose
}

data class TutorialLesson(
    val id: String,
    val level: TutorialLevel,
    val title: String,
    val concept: String,
    val detailedGuide: String,
    val sampleText: String,
    val targetGoalDescription: String,
    val defaultFlags: String = "g",
    val initialPattern: String = "",
    val solutionPattern: String,
    val hints: List<String>,
    // Function to check if pattern satisfies lesson criteria
    val validate: (userPattern: String, sampleText: String) -> Boolean
)

object TutorialData {
    val lessons = listOf(
        // EASY / BEGINNER
        TutorialLesson(
            id = "lesson_01",
            level = TutorialLevel.EASY,
            title = "1. Literal Character Matching",
            concept = "Matching Exact Text Strings",
            detailedGuide = "In Regular Expressions, plain letters and words match themselves. When you type 'cat', regex looks for the exact sequence 'c', 'a', 't'.",
            sampleText = "The cat sat on the mat with another cat.",
            targetGoalDescription = "Match every occurrence of the word 'cat' in the sentence.",
            initialPattern = "",
            solutionPattern = "cat",
            hints = listOf(
                "Regex matches characters literally. Type the 3-letter animal name.",
                "Simply type 'cat' into the regex field!"
            ),
            validate = { pattern, text ->
                pattern.trim() == "cat" || (pattern.trim().isNotEmpty() && runCatching {
                    val regex = Regex(pattern.trim(), RegexOption.IGNORE_CASE)
                    val matches = regex.findAll(text).map { it.value }.toList()
                    matches.size == 2 && matches.all { it.equals("cat", ignoreCase = true) }
                }.getOrDefault(false))
            }
        ),
        TutorialLesson(
            id = "lesson_02",
            level = TutorialLevel.EASY,
            title = "2. Digits & Numbers (\\d)",
            concept = "Matching Any Single Digit",
            detailedGuide = "The special shorthand character \\d stands for 'digit' and matches any single number from 0 to 9. Adding + means 'one or more digits'.",
            sampleText = "Order #4092 placed on 2026-07-28 costing $99.",
            targetGoalDescription = "Match all number sequences (4092, 2026, 07, 28, 99).",
            initialPattern = "",
            solutionPattern = "\\d+",
            hints = listOf(
                "Use \\d to match a single digit.",
                "Combine \\d with the + quantifier to match multi-digit numbers: \\d+"
            ),
            validate = { pattern, text ->
                pattern.trim() == "\\d+" || (pattern.isNotEmpty() && runCatching {
                    val matches = Regex(pattern.trim()).findAll(text).map { it.value }.toList()
                    matches == listOf("4092", "2026", "07", "28", "99")
                }.getOrDefault(false))
            }
        ),
        TutorialLesson(
            id = "lesson_03",
            level = TutorialLevel.EASY,
            title = "3. Word Characters & Quantifiers (\\w+)",
            concept = "Extracting Words",
            detailedGuide = "\\w matches any word character (letters a-z, A-Z, numbers 0-9, and underscore _). Combining \\w+",
            sampleText = "hello_world 123 @regex #tag",
            targetGoalDescription = "Match all valid word identifiers ('hello_world', '123', 'regex', 'tag') without symbols.",
            initialPattern = "",
            solutionPattern = "\\w+",
            hints = listOf(
                "\\w matches a single alphanumeric character.",
                "Append + to match one or more contiguous word characters: \\w+"
            ),
            validate = { pattern, text ->
                runCatching {
                    val matches = Regex(pattern.trim()).findAll(text).map { it.value }.toList()
                    matches == listOf("hello_world", "123", "regex", "tag")
                }.getOrDefault(false)
            }
        ),

        // MEDIUM / INTERMEDIATE
        TutorialLesson(
            id = "lesson_04",
            level = TutorialLevel.MEDIUM,
            title = "4. Character Classes & Ranges ([a-z])",
            concept = "Custom Character Sets",
            detailedGuide = "Square brackets [...] allow you to define a set or range of characters to match. For instance, [aeiou] matches any vowel, and [0-9] matches digits.",
            sampleText = "Code: A1 B9 C3 X7 Z0",
            targetGoalDescription = "Match code pairs starting with capital letter A-C followed by a single digit 1-9 (A1, B9, C3).",
            initialPattern = "",
            solutionPattern = "[A-C][1-9]",
            hints = listOf(
                "Use range [A-C] for the first character.",
                "Follow it with range [1-9] for the second digit: [A-C][1-9]"
            ),
            validate = { pattern, text ->
                runCatching {
                    val matches = Regex(pattern.trim()).findAll(text).map { it.value }.toList()
                    matches == listOf("A1", "B9", "C3")
                }.getOrDefault(false)
            }
        ),
        TutorialLesson(
            id = "lesson_05",
            level = TutorialLevel.MEDIUM,
            title = "5. Anchors (^ and $)",
            concept = "Line & Boundary Anchors",
            detailedGuide = "^ matches the start of a line or string, while $ matches the end. They do not consume characters; they assert position.",
            sampleText = "LOG: Error 404\nINFO: Success 200\nLOG: Critical 500",
            defaultFlags = "gm",
            targetGoalDescription = "Match only lines that begin with 'LOG:' using multiline mode.",
            initialPattern = "",
            solutionPattern = "^LOG:.*",
            hints = listOf(
                "Start your pattern with ^ to anchor to line beginning.",
                "Combine ^ with literal 'LOG:' followed by .* to capture the rest of the log line: ^LOG:.*"
            ),
            validate = { pattern, text ->
                runCatching {
                    val matches = Regex(pattern.trim(), setOf(RegexOption.MULTILINE)).findAll(text).map { it.value }.toList()
                    matches.size == 2 && matches.all { it.startsWith("LOG:") }
                }.getOrDefault(false)
            }
        ),
        TutorialLesson(
            id = "lesson_06",
            level = TutorialLevel.MEDIUM,
            title = "6. Word Boundaries (\\b)",
            concept = "Exact Whole Word Extraction",
            detailedGuide = "\\b represents a word boundary position between a word character (\\w) and a non-word character (\\W or space).",
            sampleText = "the cat in the cathedral catching a catnap with cat",
            targetGoalDescription = "Match ONLY the standalone word 'cat' (size 3) and not inside 'cathedral', 'catching', or 'catnap'.",
            initialPattern = "",
            solutionPattern = "\\bcat\\b",
            hints = listOf(
                "Wrapping a word with \\b ensures no letters precede or follow it.",
                "Try \\bcat\\b"
            ),
            validate = { pattern, text ->
                runCatching {
                    val matches = Regex(pattern.trim()).findAll(text).map { it.value }.toList()
                    matches == listOf("cat", "cat")
                }.getOrDefault(false)
            }
        ),

        // HARD / ADVANCED
        TutorialLesson(
            id = "lesson_07",
            level = TutorialLevel.HARD,
            title = "7. Capturing Groups & Extraction",
            concept = "Parentheses (...) for Grouping",
            detailedGuide = "Parentheses () create capturing groups. This lets you extract parts of a match, like area code vs phone number or key vs value pairs.",
            sampleText = "User: Alice (ID: 1001), User: Bob (ID: 2045)",
            targetGoalDescription = "Match user ID entries where Group 1 is the name (Alice/Bob) and Group 2 is the ID digits (1001/2045).",
            initialPattern = "",
            solutionPattern = "User:\\s*(\\w+)\\s*\\(ID:\\s*(\\d+)\\)",
            hints = listOf(
                "Match 'User: ' then group 1 '(\\w+)' then ' (ID: ' then group 2 '(\\d+)'",
                "Don't forget to escape parentheses with \\( and \\) when matching literal parens!"
            ),
            validate = { pattern, text ->
                runCatching {
                    val regex = Regex(pattern.trim())
                    val matchResults = regex.findAll(text).toList()
                    matchResults.size == 2 &&
                            matchResults[0].groupValues[1] == "Alice" && matchResults[0].groupValues[2] == "1001" &&
                            matchResults[1].groupValues[1] == "Bob" && matchResults[1].groupValues[2] == "2045"
                }.getOrDefault(false)
            }
        ),
        TutorialLesson(
            id = "lesson_08",
            level = TutorialLevel.HARD,
            title = "8. Non-Capturing Groups & Alternation",
            concept = "Grouping Without Capturing (?:...)",
            detailedGuide = "(?:...) groups expressions for applying quantifiers or alternation (|) WITHOUT saving the group in memory.",
            sampleText = "Visit http://a.com or https://b.org or ftp://c.net",
            targetGoalDescription = "Match web protocols (http or https only) without capturing them into a group.",
            initialPattern = "",
            solutionPattern = "(?:http|https)://\\w+\\.\\w+",
            hints = listOf(
                "Use non-capturing group (?:http|https)",
                "Follow with :// and domain name: (?:https?):\\/\\/\\w+\\.\\w+"
            ),
            validate = { pattern, text ->
                runCatching {
                    val matches = Regex(pattern.trim()).findAll(text).map { it.value }.toList()
                    matches.size == 2 && matches.all { it.startsWith("http") }
                }.getOrDefault(false)
            }
        ),

        // EXTREMELY DIFFICULT / MASTER
        TutorialLesson(
            id = "lesson_09",
            level = TutorialLevel.EXTREME,
            title = "9. Lookahead Assertions (?=...) & (?!...)",
            concept = "Zero-Width Positive & Negative Lookaheads",
            detailedGuide = "Lookaheads assert conditions ahead without moving the regex matching cursor. Positive lookahead (?=...) verifies X follows; Negative lookahead (?!...) verifies X does NOT follow.",
            sampleText = "100$ 50EUR 200$ 75USD 30GBP",
            targetGoalDescription = "Match all number amounts that are specifically followed by the '$' symbol, without including the '$' in the match itself.",
            initialPattern = "",
            solutionPattern = "\\d+(?=\\$)",
            hints = listOf(
                "Start with \\d+ to match numbers.",
                "Add positive lookahead (?=\\$) to ensure dollar sign is right ahead without including it in match result."
            ),
            validate = { pattern, text ->
                runCatching {
                    val matches = Regex(pattern.trim()).findAll(text).map { it.value }.toList()
                    matches == listOf("100", "200")
                }.getOrDefault(false)
            }
        ),
        TutorialLesson(
            id = "lesson_10",
            level = TutorialLevel.EXTREME,
            title = "10. Lookbehind Assertions (?<=...) & Password Validation",
            concept = "Lookbehinds & Multi-rule Validation",
            detailedGuide = "Lookbehinds look backward from the current position. Lookarounds combined with ^ and $ allow enforcing complex constraints like password rules in a single regex!",
            sampleText = "P@ssword123 admin pass123 SECURE_99",
            targetGoalDescription = "Match passwords that have: at least 8 chars, at least one uppercase letter, at least one digit, and at least one special symbol (@ or _).",
            initialPattern = "",
            solutionPattern = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@_]).{8,}$",
            defaultFlags = "m",
            hints = listOf(
                "Combine multiple lookaheads anchored at ^: ^(?=.*[A-Z])(?=.*\\d)(?=.*[@_])",
                "End with minimum length quantifier .{8,}$"
            ),
            validate = { pattern, text ->
                runCatching {
                    val matches = Regex(pattern.trim(), setOf(RegexOption.MULTILINE)).findAll(text).map { it.value }.toList()
                    matches == listOf("P@ssword123", "SECURE_99")
                }.getOrDefault(false)
            }
        )
    )
}
