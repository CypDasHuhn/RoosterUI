package dev.cypdashuhn.rooster.util

fun appendNumber(name: String): String {
    val regex = "\\d+$".toRegex()
    return if (regex.containsMatchIn(name)) {
        val number = regex.find(name)?.value?.toIntOrNull() ?: 0
        name.replace(regex, (number + 1).toString())
    } else {
        "${name}2"
    }
}

fun nextName(name: String, list: List<String>): String {
    return when {
        name in list -> nextName(appendNumber(name), list)
        else -> name
    }
}