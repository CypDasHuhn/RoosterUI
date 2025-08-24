package dev.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content

import dev.cypdashuhn.rooster.ui.interfaces.*
import io.papermc.paper.command.brigadier.argument.ArgumentTypes.player
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

class IndexedContentOptions<T : Context> : RoosterInterfaceOptions<T>() {
    val contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>> = (0 to 0) to (8 to 5)
}

abstract class IndexedContentInterface<ContextType : Context, IdType : Any, DataType : Any>(
    override var interfaceName: String,
    override var contextClass: KClass<ContextType>,
    indexedContentOptionsBuilder: IndexedContentOptions<ContextType>.() -> Unit = { }
) : RoosterInterface<ContextType>(interfaceName, contextClass, IndexedContentOptions<ContextType>().apply(indexedContentOptionsBuilder)) {
    val indexedContentOptions = (super.options as IndexedContentOptions<ContextType>).also {
        require(
            it.contentArea.first.first in 0..8 && it.contentArea.first.second in 0..5 &&
                    it.contentArea.second.first >= it.contentArea.first.first && it.contentArea.second.second >= it.contentArea.first.second
        ) {
            "require valid content area. x 0-8, y 0-5, second always larger then first"
        }
    }

    val contentAreaStartX by lazy { indexedContentOptions.contentArea.first.second }
    val contentAreaStartY by lazy { indexedContentOptions.contentArea.first.second }
    val contentAreaEndX by lazy { indexedContentOptions.contentArea.second.first }
    val contentAreaEndY by lazy { indexedContentOptions.contentArea.second.second }

    val contentXWidth by lazy { contentAreaEndX - contentAreaStartX + 1 }
    val contentYWidth by lazy { contentAreaEndY - contentAreaStartY + 1 }
    val contentXRange by lazy { contentAreaStartX..contentAreaEndX }
    val contentYRange by lazy { contentAreaStartY..contentAreaEndY }

    val bottomRow by lazy { (contentAreaEndY + 1) * 9 }

    /** Returns the Offset, the Relative being the upper left corner. */
    fun offset(slot: Slot): Pair<Int, Int>? {
        val x = (slot % 9) // 9 being inventory width
        val y = (slot / 9)

        return if (x in contentXRange && y in contentYRange) {
            x - contentAreaStartX to y - contentAreaStartY
        } else {
            null
        }
    }

    private fun contentItem_() = item()
        .usedWhen {
            if (offset(slot) != null) return@usedWhen false
            val data = dataFromPosition(slot, context, player)
            data != null
        }
        .displayAs {
            val data = dataFromPosition(slot, context, player)!!
            contentCreator(data, context).first(this)
        }
        .onClick {
            val data = dataFromPosition(click.slot, context, click.player)!!
            contentCreator(data, context).second(this)
        }
    private fun contentItem() = InterfaceItem<ContextType>(
        condition = {
            if (offset(it.slot) == null) return@InterfaceItem false

            val data = dataFromPosition(it.slot, it.context, it.player)

            return@InterfaceItem data != null
        },
        itemStackCreator = {
            val data = dataFromPosition(it.slot, it.context, it.player)!!
            return@InterfaceItem contentCreator(data, it.context).first(it)
        },
        action = {
            val data = dataFromPosition(it.click.slot, it.context, it.click.player)!!
            contentCreator(data, it.context).second(it)
        }
    )

    private fun clickInArea() = InterfaceItem<ContextType>(
        condition = {
            if (offset(it.slot) != null) return@InterfaceItem false
            val dataExists = dataFromPosition(it.slot, it.context, it.player) != null
            !dataExists
        },
        itemStackCreator = { ItemStack(Material.AIR) },
        action = {},
        priority = { -1 }
    )

    open fun modifiedContentItem(item: InterfaceItem<ContextType>): InterfaceItem<ContextType> = item
    open fun modifiedClickInArea(item: InterfaceItem<ContextType>): InterfaceItem<ContextType> = item

    abstract fun contentCreator(
        data: DataType,
        context: ContextType
    ): Pair<(InterfaceInfo<ContextType>) -> ItemStack, (ClickInfo<ContextType>) -> Unit>

    abstract override fun getInventory(player: Player, context: ContextType): Inventory
    final override fun getInterfaceItems(): List<InterfaceItem<ContextType>> {
        val list = mutableListOf(
            modifiedContentItem(contentItem()),
            modifiedClickInArea(clickInArea())
        )

        list.addAll(getFrameItems())
        list.addAll(getOtherItems())

        return list
    }

    abstract fun getFrameItems(): List<InterfaceItem<ContextType>>
    abstract fun getOtherItems(): List<InterfaceItem<ContextType>>
    abstract override fun defaultContext(player: Player): ContextType

    abstract fun slotToId(slot: Slot, context: ContextType, player: Player): IdType?
    abstract fun contentProvider(id: IdType, context: ContextType): DataType?
    protected fun dataFromPosition(slot: Int, context: ContextType, player: Player): DataType? {
        val absoluteSlot = slotToId(slot, context, player) ?: return null
        return contentProvider(absoluteSlot, context)
    }
}