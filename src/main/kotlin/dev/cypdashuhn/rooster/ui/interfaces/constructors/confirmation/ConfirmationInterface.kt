package dev.cypdashuhn.rooster.ui.interfaces.constructors.confirmation

import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.localization.t
import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.constructors.NoContextInterface
import dev.cypdashuhn.rooster.ui.interfaces.emptyContext
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import kotlin.reflect.KClass

abstract class ConfirmationInterface(
    override val interfaceName: String,
    override val contextClass: KClass<NoContextInterface.EmptyContext>,
    override val onConfirm: (ClickInfo<NoContextInterface.EmptyContext>) -> Unit,
    override val onCancel: (CancelInfo<NoContextInterface.EmptyContext>) -> Unit,
) : BaseConfirmationInterface<NoContextInterface.EmptyContext>(interfaceName, contextClass, onConfirm, onCancel) {
    override fun defaultContext(player: Player): NoContextInterface.EmptyContext {
        return emptyContext as NoContextInterface.EmptyContext
    }

    open fun getInventoryName(player: Player, context: NoContextInterface.EmptyContext): Component {
        return Component
            .text("# ${t("confirm", player)} #")
            .color(TextColor.color(200, 0, 0))
    }

    override fun getInventory(player: Player, context: NoContextInterface.EmptyContext): Inventory {
        return Bukkit.createInventory(null, 9, getInventoryName(player, context))
    }

    override fun getOtherItems(): List<InterfaceItem<NoContextInterface.EmptyContext>> {
        return listOf(
            item().displayAs(createItem(Material.GREEN_STAINED_GLASS_PANE, name = Component.text("")))
        )
    }
}