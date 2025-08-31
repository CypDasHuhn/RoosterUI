package dev.cypdashuhn.rooster.common

import java.util.logging.Logger

interface WarningScaffold {
    var defaultValue: Boolean
    var warningMethod: (Any) -> String
    var parents: List<WarningScaffold>

    fun getChildren(entries: List<WarningScaffold>): List<WarningScaffold> {
        val directChildren = entries.filter { it.parents.contains(this) }
        return directChildren + directChildren.flatMap { it.getChildren(entries) }
    }

    fun warnScaffold(name: String, logger: Logger, setting: RoosterSettings<*>, obj: Any = -1) {
        if (setting.unsafeGetWarningOption(this)) {
            logger.warning("${warningMethod(obj)} | #Warning.${name}#")
        }
    }

    fun disable()

    fun enable()
}

abstract class RoosterSettings<T : WarningScaffold>(val warningList: List<T>) {
    private val warnings: MutableMap<WarningScaffold, Boolean> = mutableMapOf()

    fun setWarningOption(warningOption: T, value: Boolean) {
        warnings.putAll(warningOption.getChildren(warningList).associateWith { value })
    }

    fun getWarningOption(warningOption: T): Boolean {
        return warnings[warningOption] ?: warningOption.defaultValue
    }

    internal fun unsafeGetWarningOption(warningOption: WarningScaffold): Boolean {
        return warnings[warningOption as T] ?: warningOption.defaultValue
    }
}