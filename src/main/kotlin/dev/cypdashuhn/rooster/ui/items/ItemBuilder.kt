package dev.cypdashuhn.rooster.ui.items

import dev.cypdashuhn.rooster.caching.InterfaceChachableLambda
import dev.cypdashuhn.rooster.caching.InterfaceDependency
import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.util.createItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

class ItemBuilder<T : Context> {
    constructor(clazz: KClass<T>) {
        this.contextClass = clazz
        this.condition = ConditionMap(clazz)
    }

    private val contextClass: KClass<T>

    private var slots: Array<Int>? = null
    private var condition: ConditionMap<T>
    private var priority = InterfaceChachableLambda<T, Int>(0)

    private var items: InterfaceChachableLambda<T, ItemStack>? = null

    private var action: ClickInfo<T>.() -> Unit = { }

    companion object {
        fun <T : Context> map(items: List<ItemBuilder<T>>) {

            for (slot in 0..(6 * 9)) {
                items.filter { it.slots == null || it.slots!!.contains(slot) }.withIndex()


            }
        }
    }

    fun usedWhen(
        conditionKey: String = ConditionMap.ANONYMOUS_KEY,
        dependency: InterfaceDependency<T> = InterfaceDependency.all<T>(),
        condition: InterfaceInfo<T>.() -> Boolean
    ) = copy {
        this.condition.set(condition, conditionKey, dependency)
    }

    fun atSlot(slot: Int) = copy {
        this.slots = arrayOf(slot)
    }

    fun atSlots(vararg slots: Int) = atSlots(slots.toList())

    fun atSlots(slots: List<Int>) = copy {
        this.slots = slots.toTypedArray()
    }

    fun forAllSlots() = copy {}

    fun resetConditions(excludingConditionKeys: List<String>) = copy {
        this.condition.resetConditions(excludingConditionKeys)
    }

    fun priority(
        priority: InterfaceInfo<T>.() -> Int,
        dependency: InterfaceDependency<T> = InterfaceDependency.all<T>()
    ): ItemBuilder<T> = copy {
        this.priority = InterfaceChachableLambda(priority, dependency)
    }

    fun priority(priority: Int): ItemBuilder<T> = copy {
        this.priority = InterfaceChachableLambda(priority)
    }

    fun onClick(action: ClickInfo<T>.() -> Unit): ItemBuilder<T> = copy { this.action = action }

    fun displayAs(itemStackCreator: InterfaceInfo<T>.() -> ItemStack) =
        copy { this.items = itemStackCreator.toInterfaceChachableLambda() }

    fun displayAs(
        dependency: InterfaceDependency<T>,
        itemStackCreator: InterfaceInfo<T>.() -> ItemStack,
    ) =
        copy { this.items = itemStackCreator.toInterfaceChachableLambda(dependency) }

    fun displayAs(itemStack: ItemStack): ItemBuilder<T> = copy { this.items = InterfaceChachableLambda(itemStack) }

    fun copy(modifyingBlock: ItemBuilder<T>.() -> Unit): ItemBuilder<T> {
        val copy = ItemBuilder<T>(contextClass).also {
            it.condition = condition.copy()
            it.items = items
            it.action = action
            it.priority = priority
        }
        copy.modifyingBlock()
        return copy
    }

    class ItemsForSlot<T : Context>(
        val items: List<ItemBuilder<T>>,
        val contextClass: Class<T>
    ) {
        var dynamicPriorityItems: List<ItemBuilder<T>> = listOf()
        var staticPriorityItems: List<ItemBuilder<T>> = listOf()
        var staticPriorityItemsSorted: List<Pair<ItemBuilder<T>, Int>>? = null

        var get: (InterfaceInfo<T>) -> ItemBuilder<T>? = { info ->
            staticPriorityItemsSorted = staticPriorityItems
                .map { it to it.priority.get(info) }
                .sortedByDescending { it.second }

            get = { info: InterfaceInfo<T> ->
                val entry = staticPriorityItemsSorted!!.firstOrNull { (item, _) -> item.condition.flattend(info) }
                val condition =
                    if (entry == null) { it: ItemBuilder<T> -> true }
                    else { it: ItemBuilder<T> -> it.priority.get(info) > entry.second }
                val other = dynamicPriorityItems.firstOrNull { it.condition.flattend(info) && condition(it) }
                other ?: entry?.first
            }

            get(info)
        }

        init {
            val grouped = items.groupBy { it.priority.dependency.dependsOnNothing }
            staticPriorityItems = grouped[true] ?: emptyList()
            dynamicPriorityItems = (grouped[false] ?: emptyList())
        }
    }
}

fun <T : Context, E> (InterfaceInfo<T>.() -> E).toInterfaceChachableLambda(
    dependency: InterfaceDependency<T> = InterfaceDependency.all()
) = InterfaceChachableLambda(this, dependency)

fun main() {

    ItemBuilder(NoContextInterface.EmptyContext::class)
        .atSlot(1)
        .displayAs { createItem(Material.COMPASS) }
        .onClick { }
}
