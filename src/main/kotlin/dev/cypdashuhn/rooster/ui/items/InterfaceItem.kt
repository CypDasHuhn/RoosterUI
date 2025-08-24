package dev.cypdashuhn.rooster.ui.items

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.common.util.createItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

class InterfaceItem<T : Context> {
    constructor(clazz: KClass<T>) {
        this.contextClass = clazz
        this.condition = ConditionMap(clazz)
    }

    private val contextClass: KClass<T>

    internal var slots: Slots? = null
    internal var condition: ConditionMap<T>
    internal var priority: (InterfaceInfo<T>.() -> Int) = { -1 }

    internal var displayItem: (InterfaceInfo<T>.() -> ItemStack) = { createItem(Material.BARRIER) }

    internal var onClick: (ClickInfo<T>.() -> Unit)? = null
    private var contextModifier: (ClickInfo<T>.() -> Unit)? = null
    internal val onClickMerged: (ClickInfo<T>.() -> Unit)
        get() = {
            onClick?.invoke(this)
            contextModifier?.invoke(this)
            this.clickedInterface.openInventory(click.player, this.context)
        }

    internal fun check(info: InterfaceInfo<T>) = (slots != null && slots.targetsSlot(info.slot)) || condition.flattend(info)

    fun usedWhen(
        conditionKey: String = ConditionMap.ANONYMOUS_KEY,
        condition: InterfaceInfo<T>.() -> Boolean
    ) = copy {
        this.condition.set(condition, conditionKey)
    }

    fun atSlot(slot: Int) = copy {
        this.slots = Slots(slot)
    }

    fun atSlots(vararg slots: Int) = atSlots(slots.toList())

    fun atSlots(slots: List<Int>) = copy {
        this.slots = Slots(slots)
    }

    fun forAllSlots() = copy { this.slots = Slots.all() }

    fun resetConditions(excludingConditionKeys: List<String>) = copy {
        this.condition.resetConditions(excludingConditionKeys)
    }

    fun priority(
        priority: InterfaceInfo<T>.() -> Int,
    ): InterfaceItem<T> = copy {
        this.priority = { it: InterfaceInfo<T> -> priority(it) }
    }

    fun priority(priority: Int): InterfaceItem<T> = copy {
        this.priority = { it: InterfaceInfo<T> -> priority }
    }

    /** Does something when the item is clicked */
    fun onClick(action: ClickInfo<T>.() -> Unit): InterfaceItem<T> = copy { this.onClick = action }
    /** Modifies the context of the click info when the item is clicked, and opens the inventory with the modified context */
    fun modifyContext(action: ClickInfo<T>.() -> Unit): InterfaceItem<T> = copy { this.contextModifier = action }

    fun displayAs(itemStackCreator: InterfaceInfo<T>.() -> ItemStack) =
        copy { this.displayItem = itemStackCreator }

    fun displayAs(itemStack: ItemStack): InterfaceItem<T> = copy { this.displayItem = { itemStack } }

    fun copy(modifyingBlock: InterfaceItem<T>.() -> Unit): InterfaceItem<T> {
        val copy = InterfaceItem<T>(contextClass).also {
            it.condition = condition.copy()
            it.displayItem = displayItem
            it.onClick = onClick
            it.priority = priority
        }
        copy.modifyingBlock()
        return copy
    }

    class ItemsForSlot<T : Context>(
        val items: List<InterfaceItem<T>>,
        val contextClass: Class<T>
    ) {
        var dynamicPriorityItems: List<InterfaceItem<T>> = listOf()
        var staticPriorityItems: List<InterfaceItem<T>> = listOf()
        var staticPriorityItemsSorted: List<Pair<InterfaceItem<T>, Int>>? = null

        var get: (InterfaceInfo<T>) -> InterfaceItem<T>? = { info ->
            staticPriorityItemsSorted = staticPriorityItems
                .map { it to it.priority(info) }
                .sortedByDescending { it.second }

            get = { info: InterfaceInfo<T> ->
                val entry = staticPriorityItemsSorted!!.firstOrNull { (item, _) -> item.condition.flattend(info) }
                val condition =
                    if (entry == null) { it: InterfaceItem<T> -> true }
                    else { it: InterfaceItem<T> -> it.priority(info) > entry.second }
                val other = dynamicPriorityItems.firstOrNull { it.condition.flattend(info) && condition(it) }
                other ?: entry?.first
            }

            get(info)
        }
    }
}


fun main() {

    InterfaceItem(NoContextInterface.EmptyContext::class)
        .atSlot(1)
        .displayAs { createItem(Material.COMPASS) }
        .onClick { }
}
