package com.chifunt.chromaticharptabs.data.util

internal fun normalizeTagsInput(value: String): String {
    return value
        .lowercase()
        .trim()
        .replace(",", " ")
        .replace(Regex("\\s+"), " ")
}

internal fun parseTags(value: String): List<String> {
    if (value.isBlank()) {
        return emptyList()
    }
    return value
        .lowercase()
        .trim()
        .replace(",", " ")
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }
        .distinct()
}
