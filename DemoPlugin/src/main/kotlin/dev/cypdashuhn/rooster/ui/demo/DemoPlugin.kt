package dev.cypdashuhn.rooster.ui.demo

import dev.cypdashuhn.rooster.ui.RoosterUI
import dev.cypdashuhn.rooster.ui.demo.commands.demo
import dev.cypdashuhn.rooster.ui.demo.ui.TestInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class DemoPlugin : JavaPlugin() {
    override fun onLoad() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(this).verboseOutput(false)) // Load with verbose output

        demo()
    }
    override fun onEnable() {
        CommandAPI.onEnable()

        RoosterUI.init(this, listOf(TestInterface))
    }

    override fun onDisable() {
        CommandAPI.onDisable()
    }
}