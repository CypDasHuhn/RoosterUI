package dev.cypdashuhn.rooster.ui.interfaces.constructors

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import kotlin.reflect.KClass

// TODO: Finish this
/** Interface not finished, don't use! */
abstract class FilterInterface<T : FilterInterface.FilterContext>(
    override var interfaceName: String,
    override var contextClass: KClass<T>,
) : RoosterInterface<T>(interfaceName, contextClass) {
    abstract class FilterContext(
        val filter: MutableMap<String, Any?>
    ) : Context()

    override fun getInterfaceItems(): List<InterfaceItem<T>> {
        TODO("Not yet implemented")
    }
}