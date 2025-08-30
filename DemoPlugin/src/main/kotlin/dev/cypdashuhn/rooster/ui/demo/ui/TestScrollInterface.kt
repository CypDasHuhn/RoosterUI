package dev.cypdashuhn.rooster.ui.demo.ui

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.common.util.toComponent
import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.ContextHandler
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content.ScrollContext
import dev.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content.ScrollInterface
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Entry(
    val name: String,
    val material: Material
)

object TestScrollInterface :
    ScrollInterface<ScrollContext, Entry>("test-scroll"),
    ContextHandler<ScrollContext> by ScrollContext.defaultHandler {
    val list = listOf(
        Entry("Robert", Material.DIAMOND),
        Entry("Peter", Material.BAKED_POTATO),
        Entry("John", Material.BREAD),
        Entry("Jane", Material.BEETROOT),
        Entry("Stephan", Material.BAMBOO_BLOCK)
    )

    override fun contentDisplay(
        data: Entry,
        context: ScrollContext
    ): InterfaceInfo<ScrollContext>.() -> ItemStack {
        return { createItem(data.material, data.name.toComponent()) }
    }

    override fun contentClick(
        data: Entry,
        context: ScrollContext
    ): ClickInfo<ScrollContext>.() -> Unit {
        return { click.player.sendMessage(data.name) }
    }

    override fun contentProvider(
        id: Int,
        context: ScrollContext
    ): Entry? {
        return list.getOrNull(id)
    }

    override fun defaultContext(player: Player): ScrollContext {
        return ScrollContext()
    }
}