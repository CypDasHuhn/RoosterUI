package dev.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content

import dev.cypdashuhn.rooster.common.util.ClickType
import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.common.util.typeOf
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.Slot
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.reflect.KClass

abstract class ScrollInterface<ContextType : ScrollInterface.ScrollContext, DataType : Any>(
    override var interfaceName: String,
    override var contextClass: KClass<ContextType>,
    val scrollOptions: ScrollInterfaceOptions<ContextType> = ScrollInterfaceOptions()
) : IndexedContentInterface<ContextType, Int, DataType>(interfaceName, contextClass, scrollOptions) {
    open class ScrollInterfaceOptions<T : Context> : IndexedContentOptions<T>() {
        var scrollDirection: ScrollDirection = ScrollDirection.TOP_BOTTOM

        var modifyScroller: InterfaceItem<T>.() -> Unit = { }
    }

    enum class ScrollDirection {
        TOP_BOTTOM,
        LEFT_RIGHT
    }

    open class ScrollContext(
        open var position: Int = 0
    ) : Context()

    private val rowSize: Int
        get() = if (scrollOptions.scrollDirection == ScrollDirection.LEFT_RIGHT) contentXWidth else contentYWidth

    private val scroller = item()
        .atSlot(bottomRow + 8)
        .displayAs(createItem(Material.COMPASS))
        .modifyContext {
            var scrollAmount = if (event.typeOf(ClickType.SHIFT_CLICK)) 5 else 1
            if (event.typeOf(ClickType.LEFT_CLICK)) scrollAmount *= -1

            context.position += scrollAmount
            if (context.position < 0) context.position = 0
        }.also {
            scrollOptions.modifyScroller(it)
        }


    final override fun getFrameItems(): List<InterfaceItem<ContextType>> = listOf(
        scroller
    )

    final override fun slotToId(slot: Slot, context: ContextType, player: Player): Int? {
        val (x, y) = offset(slot) ?: return null
        val result = if (scrollOptions.scrollDirection == ScrollDirection.LEFT_RIGHT)
            x + (y + context.position) * contentXWidth
        else y + (x + context.position) * contentYWidth

        return result
    }
}