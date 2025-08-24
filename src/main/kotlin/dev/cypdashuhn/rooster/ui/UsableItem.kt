package dev.cypdashuhn.rooster.ui

import dev.cypdashuhn.rooster.util.ClickType
import dev.cypdashuhn.rooster.util.MouseClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import kotlin.collections.any
import kotlin.collections.toList

private fun eventHasClicks(
    clickStates: Array<out ClickType>,
    mouseClickType: MouseClickType,
    isShift: Boolean
): Boolean {
    return clickStates.any {
        it.isShift == null || it.isShift == isShift &&
                it.mouseClickType == null || it.mouseClickType == mouseClickType
    }
}

fun PlayerInteractEvent.hasClicks(vararg clickStates: ClickType): Boolean {
    val clickType =
        if (this.action.isRightClick) MouseClickType.RIGHT else if (this.action.isLeftClick) MouseClickType.LEFT else MouseClickType.MIDDLE
    return eventHasClicks(clickStates, clickType, this.player.isSneaking)
}

fun InventoryClickEvent.hasClicks(vararg clickStates: ClickType): Boolean {
    val clickType =
        if (this.isRightClick) MouseClickType.RIGHT else if (this.isLeftClick) MouseClickType.LEFT else MouseClickType.MIDDLE
    return eventHasClicks(clickStates, clickType, this.isShiftClick)
}

class UsableItem() {
    lateinit var condition: (PlayerInteractEvent) -> Boolean
    lateinit var clickEffect: (PlayerInteractEvent) -> Unit
    private var itemGenerator: (() -> ItemStack)? = null
    val item
        get() = itemGenerator!!()
    lateinit var subEffects: List<ItemEffect>

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        clickEffect: (PlayerInteractEvent) -> Unit,
        itemGenerator: (() -> ItemStack),
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = clickEffect
        this.itemGenerator = itemGenerator
        this.subEffects = subEffects.toList()
    }

    constructor(
        itemStack: ItemStack,
        clickEffect: (PlayerInteractEvent) -> Unit,
        vararg subEffects: ItemEffect
    ) : this() {
        condition = { event -> event.item == itemStack }
        itemGenerator = { itemStack }
        this.clickEffect = clickEffect
        this.subEffects = subEffects.toList()
    }

    constructor(
        itemStack: ItemStack,
        vararg subEffects: ItemEffect
    ) : this() {
        condition = { event -> event.item == itemStack }
        itemGenerator = { itemStack }
        this.clickEffect = {}
        this.subEffects = subEffects.toList()
    }
}

class ItemEffect() {
    lateinit var condition: (PlayerInteractEvent) -> Boolean
    lateinit var clickEffect: (PlayerInteractEvent) -> Unit
    lateinit var subEffects: List<ItemEffect>

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        clickEffect: (PlayerInteractEvent) -> Unit,
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = clickEffect
        this.subEffects = subEffects.toList()
    }

    constructor(
        condition: (PlayerInteractEvent) -> Boolean,
        vararg subEffects: ItemEffect
    ) : this() {
        this.condition = condition
        this.clickEffect = { }
        this.subEffects = subEffects.toList()
    }
}