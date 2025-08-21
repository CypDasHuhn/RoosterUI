package dev.cypdashuhn.rooster.util

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