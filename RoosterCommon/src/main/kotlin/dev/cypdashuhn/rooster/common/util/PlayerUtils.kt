package dev.cypdashuhn.rooster.common.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.translation.Argument
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.entity.minecart.CommandMinecart
import org.bukkit.inventory.ItemStack

fun Player.uuid() = this.uniqueId.toString()

fun CommandSender.uniqueKey(): String {
    return when (this) {
        is Player -> this.uniqueId.toString()
        is ConsoleCommandSender -> "console"
        is BlockCommandSender -> "${this.block.location.toVector()}"
        else -> "unknown-${this::class.simpleName}"
    }
}

fun CommandSender.location(): Location? {
    return when (this) {
        is Player -> this.location
        is BlockCommandSender -> this.block.location
        is CommandMinecart -> this.location
        else -> null
    }
}

fun CommandSender.isPlayer() = this is Player
fun CommandSender.isCommandBlock() = this is BlockCommandSender
fun CommandSender.isMinecart() = this is CommandMinecart
fun CommandSender.isConsole() = this is ConsoleCommandSender

fun Player.giveItem(
    itemStack: ItemStack,
    inventoryFullFallback: (Player) -> Unit = { dropItemAtPlayer(it, itemStack) }
) {
    if (inventory.none { it.isEmpty }) {
        inventoryFullFallback(this)
        return
    }

    val index = inventory.indexOfFirst { it.isEmpty }
    inventory.setItem(index, itemStack)
}

fun dropItemAtPlayer(player: Player, itemStack: ItemStack) {
    TODO("Drop")
}

fun t(translationKey: String, vararg pair: Pair<String, String>) = Component.translatable(
    translationKey,
    *pair.map { Argument.component(it.first, it.second.toComponent()) }.toTypedArray()
)

fun CommandSender.tSend(translationKey: String, vararg value: Pair<String, String>) =
    this.sendMessage(t(translationKey, *value))

fun wrap(sender: CommandSender, errorMessage: String, vararg value: Pair<String, String>, block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.printStackTrace()
        sender.sendMessage(Component.translatable(errorMessage))
    }
}