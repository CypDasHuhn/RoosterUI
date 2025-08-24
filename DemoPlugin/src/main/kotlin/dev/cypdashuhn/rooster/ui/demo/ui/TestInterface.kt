package dev.cypdashuhn.rooster.ui.demo.ui

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Material

object TestInterface : NoContextInterface("test") {
    override fun getInterfaceItems(): List<InterfaceItem<EmptyContext>> {
        return listOf(
            item().atSlot(4).displayAs(createItem(Material.DIAMOND)).onClick {
                click.player.sendMessage("test")
            }
        )
    }
}