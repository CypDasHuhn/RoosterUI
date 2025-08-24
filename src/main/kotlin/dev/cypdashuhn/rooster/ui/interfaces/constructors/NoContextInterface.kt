package dev.cypdashuhn.rooster.ui.interfaces.constructors

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.interfaces.options
import org.bukkit.entity.Player

abstract class NoContextInterface(
    override var interfaceName: String,
    options: RoosterInterfaceOptions<EmptyContext> = options { }
) : RoosterInterface<NoContextInterface.EmptyContext>(interfaceName, EmptyContext::class, options) {
    class EmptyContext : Context()

    override fun defaultContext(player: Player): EmptyContext {
        return EmptyContext()
    }
}