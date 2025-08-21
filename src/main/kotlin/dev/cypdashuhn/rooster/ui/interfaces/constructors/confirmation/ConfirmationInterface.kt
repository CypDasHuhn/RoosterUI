package dev.cypdashuhn.rooster.ui.interfaces.constructors.confirmation

import dev.cypdashuhn.rooster.localization.t
import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.emptyContext
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.constructors.PlaceholderItem
import dev.cypdashuhn.rooster.util.createItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

abstract class ConfirmationInterface<T : Context>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    override val onConfirm: (ClickInfo<T>) -> Unit,
    override val onCancel: (CancelInfo<T>) -> Unit,
) : BaseConfirmationInterface<T>(interfaceName, contextClass, onConfirm, onCancel) {
    override fun defaultContext(player: Player): T {
        return emptyContext as T
    }

    open fun getInventoryName(player: Player, context: T): Component {
        return Component
            .text("# ${t("confirm", player)} #")
            .color(TextColor.color(200, 0, 0))
    }

    override fun getInventory(player: Player, context: T): Inventory {
        return Bukkit.createInventory(null, 9, getInventoryName(player, context))
    }

    override fun getOtherItems(): List<InterfaceItem<T>> {
        return listOf(
            PlaceholderItem<T>(
                condition = { true },
                itemStack = { createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, name = Component.text("")) }
            )
        )
    }
}