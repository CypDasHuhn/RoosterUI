package dev.cypdashuhn.rooster.localization

import dev.cypdashuhn.rooster.localization.provider.Language
import dev.cypdashuhn.rooster.ui.RoosterOptions
import dev.cypdashuhn.rooster.ui.RoosterUI.cache
import dev.cypdashuhn.rooster.ui.RoosterUI.localeProvider
import net.kyori.adventure.text.TextComponent
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.concurrent.TimeUnit

object Localization {
    fun getLocalizedMessage(
        language: Language?,
        messageKey: String,
        vararg replacements: Pair<String, String?>
    ): TextComponent {
        val language = language ?: localeProvider.getGlobalLanguage()

        var message = cache.get("$language-$messageKey", null, {
            val resourcePath = "/locales/${language.lowercase()}.json"
            val inputStream = javaClass.getResourceAsStream(resourcePath)
                ?: return@get RoosterOptions.Localization.DEFAULT_STRING.also {
                    RoosterOptions.Warnings.LOCALIZATION_MISSING_LOCALE.warn(resourcePath)
                }

            val localization = LocaleFileParser.parseLocalization(inputStream)

            val message = LocaleFileParser.getValueFromNestedMap(localization, messageKey)
            if (message != null) return@get message

            val roosterResourcePath = "/roosterLocales/${language.lowercase()}.json"
            val roosterInputStream = javaClass.getResourceAsStream(roosterResourcePath)
                ?: throw IllegalStateException("Rooster should've crashed")

            val roosterLocalization = LocaleFileParser.parseLocalization(roosterInputStream)
            val roosterMessage = LocaleFileParser.getValueFromNestedMap(roosterLocalization, messageKey)

            if (roosterMessage != null) return@get roosterMessage

            RoosterOptions.Warnings.LOCALIZATION_MISSING_LOCALE.warn(messageKey to language)
            return@get RoosterOptions.Localization.DEFAULT_STRING
        }, 60, TimeUnit.MINUTES)

        for ((key, value) in replacements) {
            message = message.replace("\${$key}", value ?: "")
        }

        return minimessage(message)
    }
}

fun t(messageKey: String, language: Language?, vararg replacements: Pair<String, String?>): TextComponent {
    return Localization.getLocalizedMessage(language, messageKey, *replacements)
}

fun t(messageKey: String, player: Player, vararg replacements: Pair<String, String?>): TextComponent {
    return Localization.getLocalizedMessage(localeProvider.getLanguage(player), messageKey, *replacements)
}

fun CommandSender.tSend(messageKey: String, vararg replacements: Pair<String, String?>) {
    this.sendMessage(t(messageKey, this.language(), *replacements))
}

fun CommandSender.tString(messageKey: String, vararg replacements: Pair<String, String?>): String {
    return t(messageKey, this.language(), *replacements).content()
}

fun CommandSender.language(): Language {
    return if (this is Player) localeProvider.getLanguage(this)
    else localeProvider.getGlobalLanguage()
}

class Locale(var language: Language?) {
    private val actualLocale: Language by lazy { language ?: localeProvider.getGlobalLanguage() }
    fun t(messageKey: String, vararg replacements: Pair<String, String?>): TextComponent {
        return Localization.getLocalizedMessage(actualLocale, messageKey, *replacements)
    }

    fun tSend(sender: CommandSender, messageKey: String, vararg replacements: Pair<String, String?>) {
        sender.sendMessage(t(messageKey, *replacements))
    }
}

fun t(messageKey: String, vararg replacements: Pair<String, String>): String {
    return "<t>$messageKey<rp>${replacements.joinToString("<next>") { "<key>${it.first}<value>${it.second}" }}"
}

fun transformMessage(message: String, language: Language?): String {
    return when {
        message.startsWith("!<t>") -> message.drop(1)
        message.startsWith("<t>") -> {
            val (key, replacements) = decryptTranslatableMessage(message)
            t(key, language, *replacements).content()
        }

        else -> message
    }
}

fun decryptTranslatableMessage(message: String): Pair<String, Array<Pair<String, String>>> {
    val (key, rest) = message.split("<rp>", limit = 2)
    val replacements = if (rest.isNotEmpty()) rest.split("<next>").map {
        val (replacementKey, replacementValue) = it.split("<value>")
        replacementKey.drop("<key>".length) to replacementValue
    } else listOf()
    return key.drop(3) to replacements.toTypedArray()
}

fun translateLanguage(message: String, language: Language?, vararg replacements: Pair<String, String>): String {
    return t(message, language, *replacements).content()
}

fun main() {
    val language = "en_us"

    val message = t("rooster.language.changed", language, "language" to "en_us")

    println(message.content())
}