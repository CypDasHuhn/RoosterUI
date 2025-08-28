package dev.cypdashuhn.rooster.ui.demo.ui

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Material
import org.bukkit.entity.Player

object TestInterface : RoosterInterface<PageInterface.PageContext>("test", PageInterface.PageContext::class) {
    val test = item().atSlot(4).displayAs(createItem(Material.DIAMOND)).onClick {
        click.player.sendMessage("test")
    }.atSlot(4)

    override fun getInterfaceItems(): List<InterfaceItem<PageInterface.PageContext>> {
        return listOf(
            test
        )
    }

    override fun defaultContext(player: Player): PageInterface.PageContext {
        return PageInterface.PageContext(0)
    }
}