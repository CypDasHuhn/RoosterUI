package dev.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.Slot
import dev.cypdashuhn.rooster.ui.interfaces.options
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.reflect.KClass

/** Interface not finished, don't use! */
abstract class GraphInterface<ContextType : GraphInterface.GraphContext, DataType : Any>(
    override val interfaceName: String,
    override val contextClass: KClass<ContextType>,
    val graphOptions: GraphOptions<ContextType> = options { }
) : IndexedContentInterface<ContextType, Pair<Int, Int>, DataType>(interfaceName, contextClass, graphOptions) {
    class GraphOptions<T : Context> : IndexedContentOptions<T>() {
        var modifyVerticalPager: InterfaceItem<T>.() -> InterfaceItem<T> = { this }
        var modifyHorizontalPager: InterfaceItem<T>.() -> InterfaceItem<T> = { this }
    }

    open class GraphContext(
        open var position: Pair<Int, Int> = 0 to 0
    ) : Context()

    private val verticalPager
        get() = item()
            .atSlot(bottomRow + 8)
            .displayAs(createItem(Material.COMPASS))
            .modifyContext {
                val y = context.position.second
                var scrollAmount = if (event.isShiftClick) 5 else 1
                if (event.click.isLeftClick) scrollAmount *= -1
                context.position = context.position.first to y + scrollAmount
            }

    private val horizontalPager
        get() = item()
            .atSlot(bottomRow + 8)
            .displayAs(createItem(Material.COMPASS))
            .modifyContext {
                val x = context.position.first
                var scrollAmount = if (event.isShiftClick) 5 else 1
                if (event.click.isLeftClick) scrollAmount *= -1
                context.position = x + scrollAmount to context.position.second
            }

    final override fun getFrameItems(): List<InterfaceItem<ContextType>> = listOf(
        graphOptions.modifyVerticalPager(verticalPager),
        graphOptions.modifyHorizontalPager(horizontalPager)
    )

    override fun slotToId(slot: Slot, context: ContextType, player: Player): Pair<Int, Int>? {
        val (x, y) = offset(slot) ?: return null
        val (posX, posY) = context.position

        return x + posX to y + posY
    }
}