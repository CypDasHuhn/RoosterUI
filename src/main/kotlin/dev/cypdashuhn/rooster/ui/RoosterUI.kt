package dev.cypdashuhn.rooster.ui

import com.google.common.cache.CacheBuilder
import dev.cypdashuhn.rooster.caching.RoosterCache
import dev.cypdashuhn.rooster.localization.provider.LocaleProvider
import dev.cypdashuhn.rooster.localization.provider.YmlLocaleProvider
import dev.cypdashuhn.rooster.ui.context.InterfaceContextProvider
import dev.cypdashuhn.rooster.ui.context.YmlInterfaceContextProvider
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.plugin.java.JavaPlugin
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.text.set

object RoosterUI {
    var plugin: JavaPlugin? = null
    val pluginFolder by lazy { plugin!!.dataFolder }
    var logger = Logger.getLogger("Rooster")
    val interfaces: MutableList<RoosterInterface<*>> = mutableListOf()

    val cache = RoosterCache<String, Any>(
        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
    )

    internal val localeProvider by RoosterServices.delegate<LocaleProvider>()
    internal val interfaceContextProvider by RoosterServices.delegate<InterfaceContextProvider>()

    fun initServices() {
        RoosterServices.set<LocaleProvider>(YmlLocaleProvider(mapOf("en_US" to Locale.ENGLISH), "en_US"))
        RoosterServices.set<InterfaceContextProvider>(YmlInterfaceContextProvider())
    }
}