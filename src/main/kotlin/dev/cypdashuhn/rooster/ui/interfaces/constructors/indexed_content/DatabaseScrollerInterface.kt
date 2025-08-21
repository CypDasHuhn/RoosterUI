package dev.cypdashuhn.rooster.ui.interfaces.constructors.indexed_content

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.util.createItem
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import kotlin.reflect.KClass

abstract class DatabaseScrollerInterface<T : DatabaseScrollerInterface.DatabaseScrollerContext, K : IntEntity>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    override val scrollDirection: ScrollDirection,
    override val contentArea: Pair<Pair<Int, Int>, Pair<Int, Int>> = Pair(0 to 0, 8 to 5),
    val targetTable: IntIdTable,
    val itemColumn: Column<IntEntity>? = null,
    val displayNameColumn: Column<IntEntity>? = null
) : ScrollInterface<T, ResultRow>(interfaceName, contextClass, scrollDirection) {
    open class DatabaseScrollerContext : ScrollContext()

    override fun contentCreator(
        data: ResultRow,
        context: T
    ): Pair<(InterfaceInfo<T>) -> ItemStack, (ClickInfo<T>) -> Unit> {
        if (itemColumn != null) {
            //val item = ItemStack.deserialize(Gson().fromJson(data[itemColumn], Map::class.java) as Map<String, Any>)
        }
        return { _: InterfaceInfo<T> -> createItem(Material.AIR) } to {}
    }

    open fun modifyItemStack(itemStack: ItemStack): ItemStack = itemStack
    abstract fun onItemClick(data: K): (ClickInfo<T>) -> Unit

    open fun interfaceDisplayName(player: Player, context: T): String {
        return "${targetTable.tableName} #${context.position + 1}"
    }

    override fun getInventory(player: Player, context: T): Inventory {
        return Bukkit.createInventory(null, (contentArea.second.second + 1) * 9, interfaceDisplayName(player, context))
    }

    override fun getOtherItems(): List<InterfaceItem<T>> {
        TODO("Not yet implemented")
    }

    open fun sortedQuery(): Query {
        return targetTable.selectAll().orderBy(targetTable.id)
    }

    override fun contentProvider(id: Int, context: T): ResultRow? {
        return sortedQuery().limit(1, offset = id.toLong()).firstOrNull()
    }
}