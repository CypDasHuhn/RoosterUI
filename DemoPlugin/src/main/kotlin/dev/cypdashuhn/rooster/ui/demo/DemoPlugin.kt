package dev.cypdashuhn.rooster.ui.demo

import dev.cypdashuhn.rooster.ui.RoosterUI
import dev.cypdashuhn.rooster.ui.demo.commands.demo
import dev.cypdashuhn.rooster.ui.demo.ui.TestInterface
import dev.cypdashuhn.rooster.ui.demo.ui.TestPageInterface
import dev.cypdashuhn.rooster.ui.demo.ui.TestScrollInterface
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import org.bukkit.plugin.java.JavaPlugin

class DemoPlugin : JavaPlugin() {
    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false)) // Load with verbose output

        demo()
    }

    override fun onEnable() {
        CommandAPI.onEnable()

        RoosterUI.init(this, listOf(TestInterface, TestPageInterface, TestScrollInterface))
    }

    override fun onDisable() {
        CommandAPI.onDisable()
    }
}