package dev.cypdashuhn.rooster.ui.context

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import org.bukkit.entity.Player

abstract class InterfaceContextProvider {
    /**
     * (Upsert) Saves a Context-Instance, with the keys player and interface
     * (recommended: PlayerUUID, InterfaceName)
     */
    abstract fun <T : Context> updateContext(player: Player, interfaceInstance: RoosterInterface<T>, context: T)

    /**
     * Reads the Context-Instance if it exists, with the keys player and
     * interface (recommended: PlayerUUID, InterfaceName)
     */
    abstract fun <T : Context> getContext(player: Player, interfaceInstance: RoosterInterface<T>): T?
}