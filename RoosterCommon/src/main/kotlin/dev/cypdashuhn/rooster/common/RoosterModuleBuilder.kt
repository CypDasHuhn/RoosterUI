package dev.cypdashuhn.rooster.common

import com.google.common.cache.CacheBuilder
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.TimeUnit

fun initRooster(
    plugin: JavaPlugin,
    services: RoosterServices = RoosterServices(),
    cache: RoosterCache<String, Any> = RoosterCache(CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)),
    block: RoosterModuleBuilder.() -> Unit = { }
) {
    RoosterCommon.init(plugin)
    block(RoosterModuleBuilder(plugin, services, cache))
}

class RoosterModuleBuilder(
    val plugin: JavaPlugin,
    val services: RoosterServices,
    val cache: RoosterCache<String, Any>
)