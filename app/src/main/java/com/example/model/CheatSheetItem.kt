package com.example.model

enum class CheatCategory(val title: String, val description: String) {
    CHARACTERS("Character Classes", "Single characters, wildcards, and character sets"),
    ANCHORS("Anchors & Boundaries", "Position markers without consuming characters"),
    QUANTIFIERS("Quantifiers", "Specify how many times a character/group must match"),
    GROUPS("Groups & Ranges", "Combine, capture, and branch expressions"),
    LOOKAROUNDS("Lookarounds", "Zero-width assertions for matching before or after"),
    SPECIAL("Special Characters", "Escapes, whitespace, and control characters"),
    SUBSTITUTION("Substitution", "Replacement operators and backreferences")
}

data class CheatSheetItem(
    val id: String,
    val category: CheatCategory,
    val token: String,
    val title: String,
    val description: String,
    val example: String,
    val testString: String
)

object CheatSheetData {
    val items = listOf(
        // Character Classes
        CheatSheetItem(
            id = "cc_dot",
            category = CheatCategory.CHARACTERS,
            token = ".",
            title = "Any Character",
            description = "Matches any single character except newline (unless 's' flag set).",
            example = "c.t matches 'cat', 'cut', 'c9t'",
            testString = "cat cut c9t c#t"
        ),
        CheatSheetItem(
            id = "cc_digit",
            category = CheatCategory.CHARACTERS,
            token = "\\d",
            title = "Digit",
            description = "Matches any digit character [0-9].",
            example = "\\d+ matches '123' in 'Order #123'",
            testString = "Order #123 items total 99"
        ),
        CheatSheetItem(
            id = "cc_nondigit",
            category = CheatCategory.CHARACTERS,
            token = "\\D",
            title = "Non-digit",
            description = "Matches any character that is not a digit.",
            example = "\\D+ matches 'Item ' in 'Item 45'",
            testString = "Item 45"
        ),
        CheatSheetItem(
            id = "cc_word",
            category = CheatCategory.CHARACTERS,
            token = "\\w",
            title = "Word Character",
            description = "Matches alphanumeric characters and underscore [a-zA-Z0-9_].",
            example = "\\w+ matches 'user_123'",
            testString = "user_123 @home!"
        ),
        CheatSheetItem(
            id = "cc_nonword",
            category = CheatCategory.CHARACTERS,
            token = "\\W",
            title = "Non-word Character",
            description = "Matches any non-alphanumeric character.",
            example = "\\W matches '@', '!'",
            testString = "user@domain.com!"
        ),
        CheatSheetItem(
            id = "cc_space",
            category = CheatCategory.CHARACTERS,
            token = "\\s",
            title = "Whitespace",
            description = "Matches spaces, tabs, line breaks, and form feeds.",
            example = "hello\\sworld matches 'hello world'",
            testString = "hello world\tline2"
        ),
        CheatSheetItem(
            id = "cc_nonspace",
            category = CheatCategory.CHARACTERS,
            token = "\\S",
            title = "Non-whitespace",
            description = "Matches any non-whitespace character.",
            example = "\\S+ matches words",
            testString = "Regex is awesome"
        ),

        // Anchors
        CheatSheetItem(
            id = "a_start",
            category = CheatCategory.ANCHORS,
            token = "^",
            title = "Start of String/Line",
            description = "Matches position at the beginning of input or line.",
            example = "^Hello matches 'Hello' at start",
            testString = "Hello world\nHello again"
        ),
        CheatSheetItem(
            id = "a_end",
            category = CheatCategory.ANCHORS,
            token = "$",
            title = "End of String/Line",
            description = "Matches position at the end of input or line.",
            example = "end$ matches 'end' at termination",
            testString = "This is the end"
        ),
        CheatSheetItem(
            id = "a_boundary",
            category = CheatCategory.ANCHORS,
            token = "\\b",
            title = "Word Boundary",
            description = "Matches position where a word character is adjacent to a non-word char.",
            example = "\\bcat\\b matches 'cat' but not 'catch'",
            testString = "The cat in the catch phrase"
        ),
        CheatSheetItem(
            id = "a_nonboundary",
            category = CheatCategory.ANCHORS,
            token = "\\B",
            title = "Non-word Boundary",
            description = "Matches any position that is NOT a word boundary.",
            example = "\\Bcat matches 'cat' in 'scatter'",
            testString = "scatter cat"
        ),

        // Quantifiers
        CheatSheetItem(
            id = "q_star",
            category = CheatCategory.QUANTIFIERS,
            token = "*",
            title = "0 or More",
            description = "Matches preceding element zero or more times.",
            example = "go*d matches 'gd', 'god', 'good', 'goood'",
            testString = "gd god good goood"
        ),
        CheatSheetItem(
            id = "q_plus",
            category = CheatCategory.QUANTIFIERS,
            token = "+",
            title = "1 or More",
            description = "Matches preceding element one or more times.",
            example = "a+ matches 'a', 'aa', 'aaa'",
            testString = "a aa aaa b"
        ),
        CheatSheetItem(
            id = "q_opt",
            category = CheatCategory.QUANTIFIERS,
            token = "?",
            title = "0 or 1 (Optional)",
            description = "Matches preceding element zero or one time.",
            example = "colou?r matches 'color' and 'colour'",
            testString = "color and colour"
        ),
        CheatSheetItem(
            id = "q_exact",
            category = CheatCategory.QUANTIFIERS,
            token = "{n}",
            title = "Exactly N Times",
            description = "Matches preceding element exactly n times.",
            example = "\\d{3} matches 3 digits",
            testString = "Code: 12345 or 987"
        ),
        CheatSheetItem(
            id = "q_range",
            category = CheatCategory.QUANTIFIERS,
            token = "{n,m}",
            title = "Between N and M Times",
            description = "Matches preceding element between n and m times inclusive.",
            example = "\\w{3,5} matches 3 to 5 letter words",
            testString = "hi cat elephant developer"
        ),
        CheatSheetItem(
            id = "q_lazy",
            category = CheatCategory.QUANTIFIERS,
            token = "*?",
            title = "Lazy / Non-greedy",
            description = "Matches as few characters as possible.",
            example = "<.*?> matches '<p>' in '<p>text</p>'",
            testString = "<p>First</p><div>Second</div>"
        ),

        // Groups & Ranges
        CheatSheetItem(
            id = "g_capturing",
            category = CheatCategory.GROUPS,
            token = "(...)",
            title = "Capturing Group",
            description = "Groups multiple tokens and creates a capture group for extraction.",
            example = "(\\d{3})-(\\d{4}) captures area and number",
            testString = "Call 555-0199"
        ),
        CheatSheetItem(
            id = "g_noncapturing",
            category = CheatCategory.GROUPS,
            token = "(?:...)",
            title = "Non-capturing Group",
            description = "Groups tokens without creating a capture group.",
            example = "(?:http|https) matches protocol without saving group",
            testString = "http://site.com and https://secure.org"
        ),
        CheatSheetItem(
            id = "g_named",
            category = CheatCategory.GROUPS,
            token = "(?<name>...)",
            title = "Named Capturing Group",
            description = "Captures matched substring under a specific name key.",
            example = "(?<year>\\d{4})-(?<month>\\d{2})",
            testString = "2026-07-28"
        ),
        CheatSheetItem(
            id = "g_set",
            category = CheatCategory.GROUPS,
            token = "[abc]",
            title = "Character Set",
            description = "Matches any single character inside the square brackets.",
            example = "[aeiou] matches any vowel",
            testString = "quick brown fox"
        ),
        CheatSheetItem(
            id = "g_negatedset",
            category = CheatCategory.GROUPS,
            token = "[^abc]",
            title = "Negated Set",
            description = "Matches any character NOT listed inside the square brackets.",
            example = "[^0-9] matches non-digits",
            testString = "Age: 25 years"
        ),
        CheatSheetItem(
            id = "g_or",
            category = CheatCategory.GROUPS,
            token = "|",
            title = "Alternation (OR)",
            description = "Acts like a logical OR operator.",
            example = "cat|dog matches 'cat' or 'dog'",
            testString = "I love cat and dog"
        ),

        // Lookarounds
        CheatSheetItem(
            id = "la_pos_head",
            category = CheatCategory.LOOKAROUNDS,
            token = "(?=...)",
            title = "Positive Lookahead",
            description = "Asserts that the preceding element is followed by expression X without consuming.",
            example = "\\d+(?=\\$) matches digits followed by $",
            testString = "Price: 100$ or 50€"
        ),
        CheatSheetItem(
            id = "la_neg_head",
            category = CheatCategory.LOOKAROUNDS,
            token = "(?!...)",
            title = "Negative Lookahead",
            description = "Asserts that preceding element is NOT followed by expression X.",
            example = "\\d+(?!\\$) matches numbers not followed by $",
            testString = "100$ or 50€ or 200"
        ),
        CheatSheetItem(
            id = "la_pos_behind",
            category = CheatCategory.LOOKAROUNDS,
            token = "(?<=...)",
            title = "Positive Lookbehind",
            description = "Asserts that current position is preceded by expression X.",
            example = "(?<=\\$)\\d+ matches digits preceded by $",
            testString = "Total $500 or €300"
        ),
        CheatSheetItem(
            id = "la_neg_behind",
            category = CheatCategory.LOOKAROUNDS,
            token = "(?<!...)",
            title = "Negative Lookbehind",
            description = "Asserts that current position is NOT preceded by expression X.",
            example = "(?<!\\$)\\d+ matches digits not preceded by $",
            testString = "$500 and 300"
        ),

        // Special Characters & Escapes
        CheatSheetItem(
            id = "sp_escape",
            category = CheatCategory.SPECIAL,
            token = "\\",
            title = "Escape Character",
            description = "Escapes a special regex metacharacter so it is treated literally.",
            example = "\\. matches literal period",
            testString = "domain.com vs domaincom"
        ),
        CheatSheetItem(
            id = "sp_tab",
            category = CheatCategory.SPECIAL,
            token = "\\t",
            title = "Tab",
            description = "Matches tab character (ASCII 9).",
            example = "Col1\\tCol2",
            testString = "Name\tAge\tCity"
        ),
        CheatSheetItem(
            id = "sp_newline",
            category = CheatCategory.SPECIAL,
            token = "\\n",
            title = "Newline",
            description = "Matches linefeed character (ASCII 10).",
            example = "line1\\nline2",
            testString = "Line 1\nLine 2"
        ),

        // Substitution
        CheatSheetItem(
            id = "sub_match",
            category = CheatCategory.SUBSTITUTION,
            token = "$0",
            title = "Entire Match",
            description = "Inserts the entire matched string during replacement.",
            example = "Replace \\d+ with '[$0]' -> '[123]'",
            testString = "Item 123"
        ),
        CheatSheetItem(
            id = "sub_group1",
            category = CheatCategory.SUBSTITUTION,
            token = "$1, $2...",
            title = "Group Backreference",
            description = "Inserts text captured by Nth parenthesized group.",
            example = "(\\w+), (\\w+) replaced with '$2 $1'",
            testString = "Smith, John"
        )
    )
}
