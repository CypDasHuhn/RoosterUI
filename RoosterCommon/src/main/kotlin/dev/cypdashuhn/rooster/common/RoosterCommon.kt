package dev.cypdashuhn.rooster.common

import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

object RoosterCommon {
    var logger: Logger = Logger.getLogger("RoosterCommon")
    private lateinit var pluginInst: JavaPlugin
    val plugin: JavaPlugin
        get() {
            if (!::pluginInst.isInitialized) {
                throw UninitializedPropertyAccessException("You need to call RoosterCommon.init() before using RoosterCommon")
            }
            return pluginInst
        }

    fun init(plugin: JavaPlugin) {
        this.pluginInst = plugin
    }
}