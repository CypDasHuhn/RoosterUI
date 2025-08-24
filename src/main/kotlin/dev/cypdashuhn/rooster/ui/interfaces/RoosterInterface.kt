package dev.cypdashuhn.rooster.ui.interfaces

import dev.cypdashuhn.rooster.ui.RoosterUI.interfaceContextProvider
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

open class RoosterInterfaceOptions<T : Context>() {
    // Click behaviour
    val cancelEvent: (ClickInfo<T>) -> Boolean = { true }
    val ignorePlayerInventory: Boolean = true
    val ignoreEmptySlots: Boolean = true

    // Inventory-Creator
    val inventorySize: Int = 9*6
    val inventoryTitle: String? = null
}

/**
 * An Instance of Interface is a model of a UI component. It's main
 * ingredient is [getInterfaceItems], which get resolved dynamically. The
 * field [interfaceName] is the key connected to the particular Interface.
 */
abstract class RoosterInterface<T : Context>{
    open lateinit var interfaceName: String
        internal set
    open lateinit var contextClass: KClass<T>
        internal set
    val options: RoosterInterfaceOptions<T>

    constructor(
        interfaceName: String,
        contextClass: KClass<T>,
        settingsBuilder: (RoosterInterfaceOptions<T>) -> Unit = { }
    ) {
       this.interfaceName = interfaceName
       this.contextClass = contextClass
       options = RoosterInterfaceOptions<T>().also { settingsBuilder(it) }
    }

    constructor(
        interfaceName: String,
        contextClass: KClass<T>,
        settings: RoosterInterfaceOptions<T>
    ) {
        this.interfaceName = interfaceName
        this.contextClass = contextClass
        this.options = settings
    }

    val items by lazy { getInterfaceItems() }


    open fun getInventory(player: Player, context: T): Inventory {
        return Bukkit.createInventory(
            player,
            options.inventorySize,
            Component.text(options.inventoryTitle ?: interfaceName)
        )
    }

    abstract fun getInterfaceItems(): List<InterfaceItem<T>>
    internal fun forEachVisibleItem(info: InterfaceInfo<T>, action: (InterfaceItem<T>) -> Unit) {
        items
            .filter { it.check(info) }
            .sortedBy { it.priority(info) }
            .forEach(action)
    }
    abstract fun defaultContext(player: Player): T
    open fun onClose(player: Player, context: T, event: InventoryCloseEvent) { }

    fun openInventory(player: Player): Inventory {
        return openInventory(player, getCurrentContext(player) ?: defaultContext(player))
    }
    fun openInventory(player: Player, context: T): Inventory {
        return InterfaceManager.openTargetInterface(player, this, context)
    }

    fun getContext(player: Player): T {
        return interfaceContextProvider.getContext(player, this) ?: defaultContext(player)
    }

    fun getCurrentContext(player: Player): T? {
        return interfaceContextProvider.getContext(player, this)
    }

    fun item(): InterfaceItem<T> = InterfaceItem(contextClass)

    private val currentInventorySize: MutableMap<Player, Int?> = mutableMapOf()
    private var groupedMapBySize: MutableMap<Int, Map<Slot, List<InterfaceItem<T>>>?> = mutableMapOf()

    internal fun groupedItems(player: Player, context: T): Map<Slot, List<InterfaceItem<T>>> {
        val inventorySize = currentInventorySize[player] ?: getInventory(player, context).size
        val grouping = groupedMapBySize[inventorySize]

        return if (grouping != null) grouping else {
            val map: MutableMap<Slot, MutableList<InterfaceItem<T>>> = mutableMapOf()
            val allSlots = (0..<inventorySize).toList().toTypedArray()
            items.forEach { item ->
                val slots = if (item.slots?.all ?: false) allSlots else item.slots?.slots
                slots?.forEach { slot ->
                    map.getOrPut(slot) { mutableListOf() }.add(item)
                }
            }

            groupedMapBySize[inventorySize] = map
            map
        }
    }
}