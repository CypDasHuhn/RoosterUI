package dev.cypdashuhn.rooster.ui.items

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
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
    internal var staticPriority: Int? = null

    internal var displayItem: (InterfaceInfo<T>.() -> ItemStack) = { createItem(Material.BARRIER) }

    internal var onClick: (ClickInfo<T>.() -> Unit)? = null
    private var contextModifier: (ClickInfo<T>.() -> Unit)? = null
    internal val onClickMerged: (ClickInfo<T>.() -> Unit)
        get() = {
            onClick?.invoke(this)
            contextModifier?.invoke(this)

        }

    internal fun check(info: InterfaceInfo<T>): Boolean {
        return slots.targetsSlot(info.slot) && condition.flattend(info)
    }

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
        this.staticPriority = null
    }

    fun priority(priority: Int): InterfaceItem<T> = copy {
        this.priority = { priority }
        this.staticPriority = priority
    }

    /** Does something when the item is clicked */
    fun onClick(action: ClickInfo<T>.() -> Unit): InterfaceItem<T> = copy { this.onClick = action }

    /** Modifies the context of the click info when the item is clicked, and opens the inventory with the modified context by default */
    fun modifyContext(openInventory: Boolean = true, action: ClickInfo<T>.() -> Unit): InterfaceItem<T> = copy {
        if (!openInventory) this.contextModifier = action
        else this.contextModifier = {
            action()
            clickedInterface.openInventory(click.player, context)
        }
    }

    fun displayAs(itemStackCreator: InterfaceInfo<T>.() -> ItemStack) =
        copy { this.displayItem = itemStackCreator }

    fun displayAs(itemStack: ItemStack): InterfaceItem<T> = copy { this.displayItem = { itemStack } }

    fun copy(modifyingBlock: InterfaceItem<T>.() -> Unit): InterfaceItem<T> {
        val copy = InterfaceItem(contextClass).also {
            it.condition = condition.copy()
            it.displayItem = displayItem
            it.onClick = onClick
            it.priority = priority
            it.staticPriority = staticPriority
            it.slots = slots
        }
        copy.modifyingBlock()
        return copy
    }
}