package dev.cypdashuhn.rooster.ui

import kotlin.collections.associateWith
import kotlin.collections.filter
import kotlin.collections.flatMap
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.plus
import kotlin.collections.toList
import kotlin.collections.toTypedArray

object RoosterOptions {
    object Localization {
        const val DEFAULT_STRING = "Message not found"
    }

    enum class Warnings {
        ALL,
        INTERFACE(ALL),
        INTERFACE_PAGES(INTERFACE),
        INTERFACE_PAGES_EMPTY({
            "Interface pages are empty!"
        }, INTERFACE_PAGES),
        INTERFACE_PAGES_OVERLAP({
            val map = it as Map<Int, Int>
            val string = map.map { "${it.key} (${it.value}x)" }.joinToString { ", " }
            "Following Interface Pages defined multiple Times: $string"
        }, INTERFACE_PAGES),
        INTERFACE_PAGES_SKIPPED_FIRST({
            "First Page was skipped. If you didn't knew, Page-Index starts at 0."
        }, INTERFACE_PAGES),
        LOCALIZATION(ALL),
        LOCALIZATION_MISSING_LOCALE({
            "Missing Locale: ${(it as Pair<*, *>).first} for Language: ${(it).second}"
        }, LOCALIZATION),
        LOCALIZATION_MISSING_LOCALE_FILE({
            "Missing Locale-File: $it"
        }, LOCALIZATION),

        ;

        internal var defaultValue: Boolean = true
        internal var warningMethod: (Any) -> String = { "" }
        private var parents: List<Warnings> = listOf()

        constructor(vararg parents: Warnings) {
            this.parents = parents.toList()
        }

        constructor(value: Boolean, vararg parents: Warnings) {
            this.defaultValue = value
            this.parents = parents.toList()
        }

        constructor(value: Boolean, warningMethod: (Any) -> String, vararg parents: Warnings) {
            this.defaultValue = value
            this.warningMethod = warningMethod
            this.parents = parents.toList()
        }

        constructor(warningMethod: (Any) -> String, vararg parents: Warnings) {
            this.warningMethod = warningMethod
            this.parents = parents.toList()
        }

        fun getChildren(): List<Warnings> {
            val directChildren = entries.filter { it.parents.contains(this) }
            return directChildren + directChildren.flatMap { it.getChildren() }
        }

        internal fun warn(obj: Any = -1) {
            if (RoosterSettings.getWarningOption(this)) {
                RoosterUICore.logger.warning("${warningMethod(obj)} | #Warning.${this.name}#")
            }
        }

        fun disable() {
            RoosterSettings.setWarningOption(this, false)
        }

        fun enable() {
            RoosterSettings.setWarningOption(this, true)
        }
    }
}

object RoosterSettings {
    private val warnings: MutableMap<RoosterOptions.Warnings, Boolean> = mutableMapOf()

    init {
        warnings.putAll(RoosterOptions.Warnings.entries.toTypedArray().associateWith { it.defaultValue })
    }

    fun setWarningOption(warningOption: RoosterOptions.Warnings, value: Boolean) {
        warnings.putAll(warningOption.getChildren().associateWith { value })
    }

    fun getWarningOption(warningOption: RoosterOptions.Warnings): Boolean {
        return warnings[warningOption] ?: warningOption.defaultValue
    }
}