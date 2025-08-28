package dev.cypdashuhn.rooster.ui.interfaces

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

typealias Slot = Int

data class InterfaceInfo<T : Context>(
    val slot: Slot,
    val context: T,
    val player: Player
)

data class ClickInfo<T : Context>(
    val click: Click,
    val context: T,
    val event: InventoryClickEvent,
    val clickedInterface: RoosterInterface<T>
)

data class Click(
    var event: InventoryClickEvent,
    var player: Player,
    var item: ItemStack?,
    var material: Material?,
    var slot: Int
) {
    @Suppress("unused")
    val isEmpty = lazy { item != null }
}

/**
 * A class meant to be overwritten to implement the specific needs
 * four your interface. Basically some sort of value that is being
 * saved in between clicks, to save the current state of the interface.
 */
open class Context

class ContextManager<T : Context> {
    val clazz: KClass<T>
    val defaultContext: (Player) -> T

    constructor(clazz: KClass<T>, defaultContext: (Player) -> T) {
        this.clazz = clazz
        this.defaultContext = defaultContext
    }
}

fun <T : Context> KClass<T>.toManager(defaultContext: (Player) -> T) = ContextManager(this, defaultContext)
val defaultContextManager = Context::class.toManager { Context() }
val emptyContext = object : Context() {}
