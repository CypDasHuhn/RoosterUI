package dev.cypdashuhn.rooster.localization.core

import dev.cypdashuhn.rooster.common.RoosterSettings
import dev.cypdashuhn.rooster.common.WarningScaffold

enum class LocalizationWarnings(
    override var warningMethod: (Any) -> String = { "" },
    override var parents: List<WarningScaffold> = emptyList(),
    override var defaultValue: Boolean = true
) : WarningScaffold {
    ALL,
    LOCALIZATION_MISSING_LOCALE({
        "Missing Locale: ${(it as Pair<*, *>).first} for Language: ${(it).second}"
    }, ALL),
    LOCALIZATION_MISSING_LOCALE_FILE({
        "Missing Locale-File: $it"
    }, ALL);

    constructor(vararg parents: LocalizationWarnings) : this(warningMethod = { "" }, parents = parents.toList())
    constructor(defaultValue: Boolean, vararg parents: LocalizationWarnings) : this(warningMethod = { "" }, defaultValue = defaultValue, parents = parents.toList())
    constructor(method: (Any) -> String, vararg parents: LocalizationWarnings) : this(warningMethod = method, parents = parents.toList())
    constructor(defaultValue: Boolean, method: (Any) -> String, vararg parents: LocalizationWarnings) : this(warningMethod = method, defaultValue = defaultValue, parents = parents.toList())

    override fun disable() = LocalizationSettings.setWarningOption(this, false)
    override fun enable() = LocalizationSettings.setWarningOption(this, true)
    internal fun warn(obj: Any = -1) = warnScaffold(this.name, RoosterLocalization.logger, LocalizationSettings, obj)
}

object LocalizationSettings : RoosterSettings<LocalizationWarnings>(LocalizationWarnings.entries) {
    val DEFAULT_STRING = "Message not found"
}