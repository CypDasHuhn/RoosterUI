package dev.cypdashuhn.rooster.ui.demo.commands

import dev.cypdashuhn.rooster.ui.demo.ui.TestInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.entity.Player

fun demo() {
    CommandAPICommand("test-interface")
        .executes(CommandExecutor { sender, _ ->
            TestInterface.openInventory(sender as Player, NoContextInterface.EmptyContext())
        })
        .register()
}