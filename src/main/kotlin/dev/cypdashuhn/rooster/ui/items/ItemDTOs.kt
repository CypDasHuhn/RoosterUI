package dev.cypdashuhn.rooster.ui.items

import dev.cypdashuhn.rooster.caching.InterfaceChachableLambda
import dev.cypdashuhn.rooster.caching.InterfaceDependency
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.interfaces.Slot
import dev.cypdashuhn.rooster.util.createItem
import dev.cypdashuhn.rooster.util.infix_gate.and
import dev.cypdashuhn.rooster.util.nextName
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

class ConditionMap<T : Context> {
    private constructor(map: Map<String, InterfaceChachableLambda<T, Boolean>>, clazz: KClass<T>) {
        this.conditionMap = map.toMutableMap()
        this.clazz = clazz
    }

    constructor(clazz: KClass<T>) {
        this.clazz = clazz
    }

    companion object {
        const val ANONYMOUS_KEY = "ANONYMOUS"
    }

    private var conditionMap: MutableMap<String, InterfaceChachableLambda<T, Boolean>> = mutableMapOf()
    private val clazz: KClass<T>

    fun add(
        condition: InterfaceInfo<T>.() -> Boolean,
        key: String = ANONYMOUS_KEY,
        dependency: InterfaceDependency<T> = InterfaceDependency.all<T>()
    ) {
        conditionMap[nextName(key, conditionMap.keys.toList())] = condition.toInterfaceChachableLambda(dependency)
    }

    fun set(
        condition: InterfaceInfo<T>.() -> Boolean,
        key: String = ANONYMOUS_KEY,
        dependency: InterfaceDependency<T> = InterfaceDependency.all<T>()
    ) {
        conditionMap[key] = condition.toInterfaceChachableLambda(dependency)
    }

    fun resetConditions(excludingConditionKeys: List<String>) {
        conditionMap.keys
            .filter { it !in excludingConditionKeys }
            .forEach { conditionMap.remove(it) }
    }

    val flattend by lazy { { info: InterfaceInfo<T> -> conditionMap.values.all { it.get(info) } } }
    fun getMap(): Map<String, InterfaceChachableLambda<T, Boolean>> = conditionMap
    fun copy(): ConditionMap<T> = ConditionMap(conditionMap, clazz)
}

class Condition<T : Context> {
    var condition: (InterfaceInfo<T>) -> Boolean = { true }

    constructor(
        condition: (InterfaceInfo<T>) -> Boolean
    ) {
        this.condition = condition
    }

    constructor(int: Slot, condition: (InterfaceInfo<T>) -> Boolean = { true }) {
        this.condition = { it: InterfaceInfo<T> -> it.slot == int && condition(it) } and condition
    }

    constructor(int: IntRange, condition: (InterfaceInfo<T>) -> Boolean = { true }) {
        this.condition = { it: InterfaceInfo<T> -> it.slot in int && condition(it) } and condition
    }

    operator fun invoke(): (InterfaceInfo<T>) -> Boolean {
        return condition
    }
}

class ItemStackCreator<T : Context> {
    var itemStackCreator: (InterfaceInfo<T>) -> ItemStack

    constructor(itemStackCreator: (InterfaceInfo<T>) -> ItemStack) {
        this.itemStackCreator = itemStackCreator
    }

    constructor(itemStack: ItemStack) {
        this.itemStackCreator = { itemStack }
    }

    constructor(material: Material, name: String? = null) {
        this.itemStackCreator = { createItem(material, name?.let { Component.text(it) }) }
    }

    operator fun invoke(): (InterfaceInfo<T>) -> ItemStack {
        return itemStackCreator
    }
}