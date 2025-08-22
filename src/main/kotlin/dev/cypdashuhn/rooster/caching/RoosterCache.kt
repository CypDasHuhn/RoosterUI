package dev.cypdashuhn.rooster.caching

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import dev.cypdashuhn.rooster.util.uniqueKey
import org.bukkit.command.CommandSender
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class RoosterCache<K, V>(cacheBuilder: CacheBuilder<Any, Any>, corePoolSize: Int = 1) {
    private var cache: Cache<Pair<String, K>, V> = cacheBuilder.build()

    private val generalKey = "general"

    fun getIfPresent(key: K, sender: CommandSender? = null): V? {
        val typeKey = sender?.uniqueKey() ?: generalKey

        return cache.getIfPresent(typeKey to key)
    }

    fun invalidate(key: K, sender: CommandSender? = null) {
        val typeKey = sender?.uniqueKey() ?: generalKey
        cache.invalidate(typeKey to key)
    }

    private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize)
    fun invalidateWithTimeout(key: K, sender: CommandSender? = null, clearTime: Long, unit: TimeUnit) {
        val typeKey = sender?.uniqueKey() ?: generalKey

        scheduler.schedule({
            cache.invalidate(typeKey to key)
        }, clearTime, unit)
    }

    fun put(
        key: K,
        sender: CommandSender? = null,
        value: V,
        clearTime: Long? = null,
        unit: TimeUnit = TimeUnit.SECONDS
    ) {
        val typeKey = sender?.uniqueKey() ?: generalKey

        cache.put(typeKey to key, value)

        if (clearTime != null) invalidateWithTimeout(key, sender, clearTime, unit)
    }

    fun <T : V> get(
        key: K,
        sender: CommandSender? = null,
        provider: () -> T,
        clearTime: Long? = null,
        unit: TimeUnit = TimeUnit.SECONDS
    ): T {
        val typeKey = sender?.uniqueKey() ?: generalKey

        if (clearTime != null) invalidateWithTimeout(key, sender, clearTime, unit)
        return cache.get(typeKey to key, provider) as T
    }

    fun size() = cache.size()
    fun asMap() = cache.asMap()
    fun addAll(
        map: Map<K, V>,
        sender: CommandSender? = null,
        clearTime: Long? = null,
        unit: TimeUnit = TimeUnit.SECONDS
    ) {
        map.forEach { (key, value) -> put(key, sender, value, clearTime, unit) }
    }

    fun cleanUp() = cache.cleanUp()
    fun getAllPresent(keys: Iterable<K>, sender: CommandSender?) {
        cache.getAllPresent(keys.map {
            (sender?.uniqueKey() ?: generalKey) to it
        })
    }
}