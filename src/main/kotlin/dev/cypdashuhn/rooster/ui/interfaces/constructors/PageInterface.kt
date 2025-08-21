package dev.cypdashuhn.rooster.ui.interfaces.constructors

import dev.cypdashuhn.rooster.core.config.RoosterOptions
import dev.cypdashuhn.rooster.listeners.usable_item.hasClicks
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.interfaces.constructors.PageInterface.Page
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.Slots
import dev.cypdashuhn.rooster.ui.items.constructors.ContextModifierItem
import dev.cypdashuhn.rooster.util.ClickType
import dev.cypdashuhn.rooster.util.createItem
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

/** Generally unfinished in every aspect. just ignore! */
@Suppress("unused")
abstract class PageInterface<T : PageInterface.PageContext>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    /** Value between 1-5 (last row is bottom bar) */
    private val contentRowAmount: Int = 5
) : RoosterInterface<T>(interfaceName, contextClass) {
    companion object {
        const val PAGE_CONDITION_KEY = "page"
    }

    open class PageContext(
        open var page: Int
    ) : Context()

    data class Page<T : Context>(val page: Int, val items: List<InterfaceItem<T>>)

    val bottomBar
        get() = contentRowAmount * 9

    abstract fun initializePages(): List<Page<T>>

    val pageTurner = ContextModifierItem<T>(
        slots = Slots(bottomBar + 8),
        itemStack = createItem(Material.COMPASS, name = Component.empty()),
        contextModifier = { clickInfo ->
            clickInfo.context.also {
                if (clickInfo.event.hasClicks(ClickType.LEFT_CLICK)) it.page += 1
                else it.page -= 1
            }.also { if (it.page < 0) it.page = 0 }
        }
    )

    val forwardPageTurner = pageTurner.changeContextModifierAction(contextModifier = { clickInfo ->
        clickInfo.context.also { it.page += 1 }
    }).also { it.slots = Slots(bottomBar + 7) }

    val backwardsPageTurned = pageTurner.changeContextModifierAction(contextModifier = { clickInfo ->
        clickInfo.context.also { it.page -= 1 }
    })

    open fun customizePageTurner(item: InterfaceItem<T>): InterfaceItem<T> {
        return item
    }

    override fun getInterfaceItems(): List<InterfaceItem<T>> {
        val baseItems = mutableListOf<InterfaceItem<T>>()

        baseItems.addAll(
            initializePages().also { pages ->
                if (pages.isEmpty()) {
                    RoosterOptions.Warnings.INTERFACE_PAGES_EMPTY.warn()
                } else if (pages.none { it.page == 0 } && pages.any { it.page > 0 }) {
                    RoosterOptions.Warnings.INTERFACE_PAGES_SKIPPED_FIRST.warn()
                } else {
                    val overlappingPages = pages.groupBy { it.page }
                        .filter { it.value.size > 1 }

                    if (overlappingPages.isNotEmpty()) {
                        RoosterOptions.Warnings.INTERFACE_PAGES_OVERLAP.warn(overlappingPages.mapValues { it.value.size })
                    }
                }
            }.map { page ->
                page.items.onEach { item ->
                    item.addCondition({ it.context.page == page.page }, PAGE_CONDITION_KEY)
                }
            }.flatten()
        )

        baseItems.add(customizePageTurner(pageTurner))

        return baseItems
    }

    override fun getInventory(player: Player, context: T): Inventory {
        return Bukkit.createInventory(player, (contentRowAmount + 1) * 9, getInventoryName(player, context))
    }

    abstract fun getInventoryName(player: Player, context: T): String

    fun pages(block: PageListBuilder<T>.() -> Unit): List<Page<T>> {
        val builder = PageListBuilder<T>()
        builder.block()
        return builder.build()
    }
}

class PageListBuilder<T : Context> {
    private val pages = mutableListOf<Page<T>>()

    fun page(pageNumber: Int, block: MutableList<InterfaceItem<T>>.() -> Unit) {
        val items = mutableListOf<InterfaceItem<T>>().apply(block)
        pages += Page(pageNumber, items)
    }

    fun build() = pages.toList()
}