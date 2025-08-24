package dev.cypdashuhn.rooster.ui.interfaces.constructors

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterfaceOptions
import dev.cypdashuhn.rooster.ui.interfaces.constructors.confirmation.CancelEvent
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

abstract class NoContextInterface(
    override var interfaceName: String,
    open val settingsBuilder: (RoosterInterfaceOptions<EmptyContext>) -> Unit = { }
) : RoosterInterface<NoContextInterface.EmptyContext>(interfaceName, EmptyContext::class, settingsBuilder) {
    class EmptyContext : Context()

    override fun defaultContext(player: Player): EmptyContext {
        return EmptyContext()
    }
}