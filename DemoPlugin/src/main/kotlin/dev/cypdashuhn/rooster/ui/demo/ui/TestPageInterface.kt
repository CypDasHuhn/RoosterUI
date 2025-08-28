package dev.cypdashuhn.rooster.ui.demo.ui

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface
import org.bukkit.Material
import org.bukkit.entity.Player

object TestPageInterface : PageInterface<PageInterface.PageContext>("test-page", PageContext::class) {
    val test = item().atSlot(4).displayAs(createItem(Material.DIAMOND)).onClick {
        click.player.sendMessage("test")
    }

    override fun getPages(): List<Page<PageContext>> {
        return pages {
            page(0) {
                add(test)
            }
        }
    }

    override fun defaultContext(player: Player): PageContext {
        return PageContext(0)
    }
}