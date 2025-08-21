package dev.cypdashuhn.rooster.ui.interfaces

import dev.cypdashuhn.rooster.*
import dev.cypdashuhn.rooster.core.Rooster
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.PlayerInventory

typealias InterfaceName = String

object InterfaceManager {
    /**
     * A map which links players to an interface, and whether its
     * state is currently protected (as to not be overwritten by the
     * InventoryOpeningListener, which would else set it as empty.
     * This needs to be performed while changing between interfaces)
     */
    private var playerInterfaceMap = HashMap<Player, InterfaceName>()

    fun closeInterface(player: Player, event: InventoryCloseEvent) {
        playerInterfaceMap[player] = ""

        val correspondingInterface = currentInterface(player) as RoosterInterface<Context>? ?: return

        val context = correspondingInterface.getContext(player)

        correspondingInterface.onClose(player, context, event)
    }

    const val CHANGES_INTERFACE_KEY = "changes_interface"

    /**
     * This function opens the interface it could find depending on the
     * [targetInterface] for the given [player] applied with the current state
     * of the interface ([context]).
     */
    fun <T : Context> openTargetInterface(player: Player, targetInterface: RoosterInterface<T>, context: T): Inventory {
        Rooster.cache.put(CHANGES_INTERFACE_KEY, player, true)

        playerInterfaceMap[player] = targetInterface.interfaceName

        val inventory =
            targetInterface.getInventory(player, context)
                .fillInventory(targetInterface.items, context, player)

        Rooster.interfaceContextProvider.updateContext(player, targetInterface, context)

        /*
        TODO: Look out how to properly handle this with a decentralized Simulator now
        Simulator.onlyTest {
            if (Simulator.isTerminal) InterfaceSimulatorHandler.printInterface(inventory)
            Simulator.currentInventory = inventory
        }
        Simulator.nonTest {

        }
        */
        player.openInventory(inventory)
        return inventory
    }

    /**
     * This function fills the [Inventory] with [ItemStack]'s using the
     * registered [interfaceItems]'s and the current Interface State
     * ([context]).
     */
    private fun <T : Context> Inventory.fillInventory(
        interfaceItems: List<InterfaceItem<T>>,
        context: T,
        player: Player
    ): Inventory {
        for (slot in 0 until this.size) {
            interfaceItems
                .filter { it.totalCondition(InterfaceInfo(slot, context, player)) }
                .sortedBy { it.priority?.invoke(InterfaceInfo(slot, context, player)) ?: 0 }
                .forEach { this@fillInventory.setItem(slot, it.itemStackCreator(InterfaceInfo(slot, context, player))) }
        }
        return this
    }

    fun <T : Context> getInventory(
        targetInventory: RoosterInterface<T>,
        context: T,
        player: Player
    ): Inventory {
        val inventory = targetInventory.getInventory(player, context)
        val items = targetInventory.getInterfaceItems()
        for (slot in 0 until inventory.size) {
            items.filter { it.totalCondition(InterfaceInfo(slot, context, player)) }
                .sortedBy { it.priority?.invoke(InterfaceInfo(slot, context, player)) ?: 0 }
                .forEach {
                    inventory.setItem(slot, it.itemStackCreator(InterfaceInfo(slot, context, player)))
                }
        }
        return inventory
    }

    fun currentInterface(player: Player): RoosterInterface<*>? {
        val interfaceName = playerInterfaceMap[player] ?: return null
        if (interfaceName.isEmpty()) return null

        return Rooster.registered.interfaces
            .firstOrNull { currentInterface -> currentInterface.interfaceName == interfaceName }
    }

    fun handleInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        val correspondingInterface = currentInterface(player) ?: return

        if (event.currentItem == null && correspondingInterface.ignoreEmptySlots) return
        if (event.inventory is PlayerInventory && correspondingInterface.ignorePlayerInventory) return

        val click = Click(event, player, event.currentItem, event.currentItem?.type, event.slot)

        click(click, event, correspondingInterface, player)
    }

    fun <T : Context> click(
        click: Click,
        inventoryClickEvent: InventoryClickEvent,
        targetInterface: RoosterInterface<T>,
        player: Player
    ) {
        @Suppress("UNCHECKED_CAST")
        val typedInterface = targetInterface as RoosterInterface<Context>

        val context = typedInterface.getContext(player)

        typedInterface.items
            .filter { it.totalCondition(InterfaceInfo(click.slot, context, click.player)) }
            .sortedBy { it.priority?.invoke(InterfaceInfo(click.slot, context, player)) ?: 0 }
            .forEach { it.action(ClickInfo(click, context, inventoryClickEvent, targetInterface)) }
    }
}