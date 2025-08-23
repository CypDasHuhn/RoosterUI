package dev.cypdashuhn.rooster.common

import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

object RoosterCommon {
    var logger: Logger = Logger.getLogger("RoosterCommon")
    lateinit var plugin: JavaPlugin

    fun init(plugin: JavaPlugin) {
        this.plugin = plugin
    }
}