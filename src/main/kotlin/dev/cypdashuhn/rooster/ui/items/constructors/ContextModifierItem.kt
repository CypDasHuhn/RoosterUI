package dev.cypdashuhn.rooster.ui.items.constructors

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.Slots
import org.bukkit.inventory.ItemStack

open class ContextModifierItem<T : Context> : InterfaceItem<T> {
    protected companion object {
        fun <T : Context> contextModifierAction(
            contextModifier: (ClickInfo<T>) -> T,
            furtherAction: (ClickInfo<T>) -> Unit
        ): (ClickInfo<T>) -> Unit {
            return {
                furtherAction(it)
                val context = contextModifier(it)
                it.clickedInterface.openInventory(it.click.player, context)
            }
        }
    }

    fun changeContextModifierAction(
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { }
    ): ContextModifierItem<T> {
        return ContextModifierItem(
            this.conditionMap,
            this.itemStackCreator,
            contextModifier,
            furtherAction,
            this.priority
        )
    }

    constructor(
        conditionMap: Map<String, (InterfaceInfo<T>) -> Boolean>,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: ((InterfaceInfo<T>) -> Int)? = null
    ) : super(conditionMap, itemStackCreator, contextModifierAction(contextModifier, furtherAction), priority)

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: ((InterfaceInfo<T>) -> Int)? = null
    ) : super(
        condition = condition,
        itemStackCreator = itemStackCreator,
        action = contextModifierAction(contextModifier, furtherAction),
        priority = priority
    )

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: ((InterfaceInfo<T>) -> Int)? = null
    ) : super(
        condition = condition,
        itemStack = itemStack,
        action = contextModifierAction(contextModifier, furtherAction),
        priority = priority
    )

    constructor(
        slots: Slots,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: ((InterfaceInfo<T>) -> Int)? = null
    ) : super(
        slots = slots,
        condition = condition,
        itemStackCreator = itemStackCreator,
        action = contextModifierAction(contextModifier, furtherAction),
        priority = priority
    )

    constructor(
        slots: Slots,
        condition: (InterfaceInfo<T>) -> Boolean = { true },
        itemStack: ItemStack,
        contextModifier: (ClickInfo<T>) -> T,
        furtherAction: (ClickInfo<T>) -> Unit = { },
        priority: ((InterfaceInfo<T>) -> Int)? = null
    ) : super(
        slots = slots,
        condition = condition,
        itemStack = itemStack,
        action = contextModifierAction(contextModifier, furtherAction),
        priority = priority
    )
}