package dev.cypdashuhn.rooster.ui.demo

import dev.cypdashuhn.rooster.ui.RoosterUI
import org.bukkit.plugin.java.JavaPlugin

class DemoPlugin : JavaPlugin() {
    override fun onEnable() {
        RoosterUI.init(this)
    }
}