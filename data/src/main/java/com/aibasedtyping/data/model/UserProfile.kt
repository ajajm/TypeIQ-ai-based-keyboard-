package com.aibasedtyping.data.model

data class UserProfile(
    val languages: List<Language> = listOf(Language.ENGLISH),
    val typingStyle: TypingStyle = TypingStyle.MIXED,
    val usage: Usage = Usage.BOTH,
    val profession: Profession = Profession.OTHER,
    val sentencePredictionEnabled: Boolean = true,
    val modelSize: ModelSize = ModelSize.SMALL,
    val onboardingCompleted: Boolean = false
)

enum class Language(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    HINDI("Hindi", "hi"),
    HINGLISH("Hinglish", "hi-en")
}

enum class TypingStyle(val displayName: String) {
    FORMAL("Formal — full sentences, proper grammar"),
    CASUAL("Casual — abbreviations, slang, emojis"),
    MIXED("Mixed — depends on context")
}

enum class Usage(val displayName: String) {
    WORK("Work & Productivity"),
    SOCIAL("Social & Chat"),
    BOTH("Both equally")
}

enum class Profession(val displayName: String) {
    TECH("Tech / Engineering"),
    MEDICAL("Medical / Healthcare"),
    LEGAL("Legal"),
    FINANCE("Finance / Business"),
    EDUCATION("Education / Teaching"),
    STUDENT("Student"),
    CREATIVE("Creative / Design"),
    OTHER("Other")
}

enum class ModelSize(val displayName: String, val description: String, val sizeBytes: Long) {
    SMALL("Minimal (~100 MB)", "Fastest, good word predictions", 100L * 1024 * 1024),
    MEDIUM("Balanced (~250 MB)", "Better completions", 250L * 1024 * 1024),
    LARGE("Best quality (~700 MB)", "Best sentence predictions", 700L * 1024 * 1024)
}
