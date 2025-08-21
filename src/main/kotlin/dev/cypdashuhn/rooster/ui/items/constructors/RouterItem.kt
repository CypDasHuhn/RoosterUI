package dev.cypdashuhn.rooster.ui.items.constructors

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.items.Condition
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.ItemStackCreator
import org.bukkit.inventory.ItemStack

open class RouterItem<T : Context, K : Context> : InterfaceItem<T> {
    protected companion object {
        fun <T : Context, K : Context> routerAction(
            furtherAction: (ClickInfo<T>) -> Unit,
            context: ((ClickInfo<T>) -> K?)?,
            targetInterface: RoosterInterface<K>
        ): (ClickInfo<T>) -> Unit {
            return { clickInfo ->
                furtherAction(clickInfo)
                var usedContext: K? = context?.let { it(clickInfo) }
                if (usedContext == null) usedContext = targetInterface.getContext(clickInfo.click.player)
                targetInterface.openInventory(clickInfo.click.player, usedContext)
            }

        }
    }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStackCreator: (InterfaceInfo<T>) -> ItemStack,
        targetInterface: RoosterInterface<K>,
        context: ((ClickInfo<T>) -> K?)? = null,
        furtherAction: (ClickInfo<T>) -> Unit = {},
    ) : super(
        condition = condition,
        itemStackCreator = itemStackCreator,
        action = routerAction(furtherAction, context, targetInterface)
    )

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean,
        itemStack: ItemStack,
        targetInterface: RoosterInterface<K>,
        context: ((ClickInfo<T>) -> K?)? = null,
        furtherAction: (ClickInfo<T>) -> Unit = {},
    ) : super(
        condition = condition,
        itemStack = itemStack,
        action = routerAction(furtherAction, context, targetInterface)
    )

    constructor(
        condition: Condition<T>,
        itemStack: ItemStackCreator<T>,
        targetInterface: RoosterInterface<K>,
        context: ((ClickInfo<T>) -> K?)? = null,
        furtherAction: (ClickInfo<T>) -> Unit = {},
    ) : super(
        condition = condition(),
        itemStackCreator = itemStack(),
        action = routerAction(furtherAction, context, targetInterface)
    )
}

