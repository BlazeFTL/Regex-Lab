package com.example.model

data class PrebuiltPattern(
    val id: String,
    val category: String,
    val title: String,
    val pattern: String,
    val flags: String = "gi",
    val description: String,
    val sampleText: String,
    val replacePattern: String = ""
)

object PrebuiltPatternLibrary {
    val items = listOf(
        PrebuiltPattern(
            id = "pb_email",
            category = "Web & Tech",
            title = "Email Address",
            pattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            flags = "gi",
            description = "Standard email address validation matching name@domain.com format.",
            sampleText = "Contact support@example.com or admin.user+tag@sub.company.org for help. Invalid: user@, @domain.com."
        ),
        PrebuiltPattern(
            id = "pb_url",
            category = "Web & Tech",
            title = "URL / Web Link",
            pattern = "https?://[a-zA-Z0-9.-]+(?:\\.[a-zA-Z]{2,})+(?::\\d+)?(?:/[^\\s]*)?",
            flags = "gi",
            description = "HTTP and HTTPS URLs with domain, optional port, and path.",
            sampleText = "Check https://ai.studio/build and http://localhost:8080/api/v1/data for details."
        ),
        PrebuiltPattern(
            id = "pb_ipv4",
            category = "Web & Tech",
            title = "IPv4 Address",
            pattern = "\\b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b",
            flags = "g",
            description = "Validates IP addresses in range 0.0.0.0 to 255.255.255.255.",
            sampleText = "Server 192.168.1.1 and gateway 10.0.0.254 connected. Invalid: 999.1.1.1."
        ),
        PrebuiltPattern(
            id = "pb_phone",
            category = "Validation",
            title = "US / Int Phone Number",
            pattern = "(?:\\+?\\d{1,3}[- .]?)?\\(?\\d{3}\\)?[- .]?\\d{3}[- .]?\\d{4}",
            flags = "g",
            description = "Matches international or national phone numbers with optional country code and dashes.",
            sampleText = "Call +1 (555) 019-2831 or 800-555-1234 or +44 20 7946 0912."
        ),
        PrebuiltPattern(
            id = "pb_hexcolor",
            category = "Design & Code",
            title = "Hex Color Code",
            pattern = "#(?:[0-9a-fA-F]{3}){1,2}\\b",
            flags = "g",
            description = "3-digit (#FFF) or 6-digit (#0D9488) CSS hexadecimal color codes.",
            sampleText = "Palette: primary #0D9488, accent #4F46E5, white #fff and dark #0F172A."
        ),
        PrebuiltPattern(
            id = "pb_date_iso",
            category = "Validation",
            title = "ISO Date (YYYY-MM-DD)",
            pattern = "\\b\\d{4}-(?:0[1-9]|1[0-2])-(?:0[1-9]|[12]\\d|3[01])\\b",
            flags = "g",
            description = "Valid ISO 8601 formatted date strings.",
            sampleText = "Release date: 2026-07-28, updated on 2025-12-31. Invalid: 2026-13-45."
        ),
        PrebuiltPattern(
            id = "pb_html_tag",
            category = "Design & Code",
            title = "HTML / XML Tag",
            pattern = "</?[a-zA-Z][a-zA-Z0-9]*\\b[^>]*>",
            flags = "g",
            description = "Matches open, self-closing, and closing HTML tags.",
            sampleText = "<div class=\"container\"><h1 id=\"main\">Hello World</h1><img src=\"pic.png\" /></div>"
        ),
        PrebuiltPattern(
            id = "pb_uuid",
            category = "Web & Tech",
            title = "UUID v4",
            pattern = "\\b[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\\b",
            flags = "g",
            description = "Standard 36-character Universally Unique Identifier.",
            sampleText = "Session token: 123e4567-e89b-12d3-a456-426614174000 created."
        ),
        PrebuiltPattern(
            id = "pb_hashtag",
            category = "Social",
            title = "Hashtags & Mentions",
            pattern = "[#@][a-zA-Z0-9_]+",
            flags = "g",
            description = "Extract social media hashtags (#regex) and user mentions (@developer).",
            sampleText = "Excited to code #RegexLab with @GoogleAIStudio! #AndroidDev #Compose"
        ),
        PrebuiltPattern(
            id = "pb_password",
            category = "Validation",
            title = "Strong Password Rule",
            pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            flags = "m",
            description = "Enforces min 8 chars with 1 uppercase, 1 lowercase, 1 digit, and 1 special char.",
            sampleText = "P@ssword123\nweakpass\nNO_SPECIAL1\nValidPass!9"
        )
    )
}
