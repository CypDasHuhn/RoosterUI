package dev.cypdashuhn.rooster.ui

import kotlin.collections.set
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

interface RoosterService {
    fun targetClass(): KClass<out RoosterService>
}

object RoosterServices {
    private val services: MutableMap<KClass<out RoosterService>, RoosterService> = mutableMapOf()

    fun <T : RoosterService> set(instance: T): T {
        services[instance.targetClass()] = instance
        return instance
    }

    fun <T : RoosterService> get(clazz: KClass<T>): T {
        return services[clazz] as? T ?: error("Service ${clazz.simpleName} not found.")
    }

    inline fun <reified T : RoosterService> get(): T = get(T::class)

    fun <T : RoosterService> getIfPresent(clazz: KClass<T>): T? = services[clazz] as? T
    inline fun <reified T : RoosterService> getIfPresent(): T? = getIfPresent(T::class)

    fun <T : RoosterService> delegate(clazz: KClass<T>): Delegate<T> =
        ReadOnlyProperty { _, _ -> get(clazz) }

    inline fun <reified T : RoosterService> delegate(): Delegate<T> = delegate(T::class)

    fun <T : RoosterService> setDelegate(instance: T): Delegate<T> {
        set(instance)
        return delegate(instance::class)
    }
}

typealias Delegate<T> = ReadOnlyProperty<Any?, T>