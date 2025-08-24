package dev.cypdashuhn.rooster.ui.demo.ui

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.Slots
import org.bukkit.Material

object TestInterface : NoContextInterface("test") {
    override fun getInterfaceItems(): List<InterfaceItem<EmptyContext>> {
        return listOf(
            InterfaceItem(slots = Slots(4), itemStack = createItem(Material.DIAMOND), action = { it.click.player.sendMessage("test") }),
        )
    }
}