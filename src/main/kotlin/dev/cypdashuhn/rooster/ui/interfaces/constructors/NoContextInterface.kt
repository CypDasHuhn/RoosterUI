package dev.cypdashuhn.rooster.ui.interfaces.constructors

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.confirmation.CancelEvent
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

abstract class NoContextInterface(
    override val interfaceName: String,
    override val cancelEvent: (ClickInfo<EmptyContext>) -> Boolean = { true },
    override val ignorePlayerInventory: Boolean = true,
    override val ignoreEmptySlots: Boolean = true
) : RoosterInterface<NoContextInterface.EmptyContext>(interfaceName, EmptyContext::class) {
    class EmptyContext : Context()

    override fun defaultContext(player: Player): EmptyContext {
        return EmptyContext()
    }
}