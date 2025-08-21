package dev.cypdashuhn.rooster.ui.interfaces.constructors.confirmation

import dev.cypdashuhn.rooster.ui.interfaces.ClickInfo
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import dev.cypdashuhn.rooster.ui.items.Slots
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass


abstract class BaseConfirmationInterface<T : Context>(
    override val interfaceName: String,
    override val contextClass: KClass<T>,
    open val onConfirm: (ClickInfo<T>) -> Unit,
    open val onCancel: (CancelInfo<T>) -> Unit
) : RoosterInterface<T>(interfaceName, contextClass) {
    private fun confirmationItem() = InterfaceItem(
        slots = Slots(8),
        itemStackCreator = { ItemStack(Material.GREEN_STAINED_GLASS_PANE) },
        action = onConfirm
    )

    protected fun modifyConfirmationItem(item: InterfaceItem<T>) = item
    private fun cancelItem() = InterfaceItem(
        slots = Slots(0),
        itemStackCreator = { ItemStack(Material.RED_STAINED_GLASS_PANE) },
        action = onConfirm
    )

    protected fun modifyCancelItem(item: InterfaceItem<T>) = item

    abstract fun getOtherItems(): List<InterfaceItem<T>>
    override fun getInterfaceItems(): List<InterfaceItem<T>> {
        val list = mutableListOf(
            modifyConfirmationItem(confirmationItem()),
            modifyCancelItem(cancelItem())
        )

        list.addAll(getOtherItems())

        return list
    }

    override fun onClose(player: Player, context: T, event: InventoryCloseEvent) {
        onCancel(CancelInfo(CancelEvent(event), this, context))
    }
}