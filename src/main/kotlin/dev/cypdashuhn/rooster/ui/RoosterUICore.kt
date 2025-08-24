package dev.cypdashuhn.rooster.ui

import com.google.common.cache.CacheBuilder
import dev.cypdashuhn.rooster.ui.context.InterfaceContextProvider
import dev.cypdashuhn.rooster.ui.context.YmlInterfaceContextProvider
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

object RoosterUICore {
    lateinit var plugin: JavaPlugin
    val pluginFolder: String by lazy { plugin.dataFolder.absolutePath }
    var interfaceContextProvider: InterfaceContextProvider = YmlInterfaceContextProvider()
    val interfaces = listOf<RoosterInterface<*>>()
    val cache = RoosterCache<String, Any>(
        CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
    )
    internal val logger: Logger = Logger.getLogger("RoosterUI")
}