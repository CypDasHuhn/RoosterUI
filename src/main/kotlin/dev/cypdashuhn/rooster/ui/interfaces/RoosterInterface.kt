package dev.cypdashuhn.rooster.ui.interfaces

import dev.cypdashuhn.rooster.ui.RoosterUI.interfaceContextProvider
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.targetsNullableSlot
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass


inline fun <T : Context, reified E : RoosterInterface.RoosterInterfaceOptions<T>> options(
    block: E.() -> Unit
): E {
    val instance = E::class.java.getDeclaredConstructor().newInstance()
    instance.block()
    return instance
}

/**
 * An Instance of Interface is a model of a UI component. It's main
 * ingredient is [getInterfaceItems], which get resolved dynamically. The
 * field [interfaceName] is the key connected to the particular Interface.
 */
abstract class RoosterInterface<T : Context>(
    open val interfaceName: String,
    open val contextClass: KClass<T>,
    val options: RoosterInterfaceOptions<T> = options { }
) {
    open class RoosterInterfaceOptions<T : Context>() {
        // Click behaviour
        var cancelEvent: (ClickInfo<T>) -> Boolean = { true }
        var ignorePlayerInventory: Boolean = true
        var ignoreEmptySlots: Boolean = true

        // Inventory-Creator
        var inventorySize: Int = 9 * 6
        var inventoryTitle: String? = null
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
    abstract fun defaultContext(player: Player): T

    open fun onClose(player: Player, context: T, event: InventoryCloseEvent) {}

    internal fun forEachVisibleItem(info: InterfaceInfo<T>, action: (InterfaceItem<T>) -> Unit) {
        items
            .filter { it.check(info) }
            .sortedBy { it.priority(info) }
            .forEach(action)
    }

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

    fun item(): InterfaceItem<T> {
        //TODO: Link to docs
        requireNotNull(contextClass) { "If you see this error, you used nested Interface Instances without while initializing items inside the parent directly, not lazily. Fore more, visit: -" }
        return InterfaceItem(contextClass)
    }

    private val currentInventorySize: MutableMap<Player, Int?> = mutableMapOf()
    private var groupedMapBySize: MutableMap<Int, Map<Slot, List<InterfaceItem<T>>>?> = mutableMapOf()

    internal fun groupedItems(player: Player): Map<Slot, List<InterfaceItem<T>>> {
        val maxSlot = items
            .mapNotNull { it.slots }
            .flatMap { it.slots.toList() }
            .maxOrNull() ?: return emptyMap()

        val map = mutableMapOf<Slot, List<InterfaceItem<T>>>()
        for (i in 0..maxSlot) {
            map += i to items.filter { it.slots.targetsNullableSlot(i) }
        }

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