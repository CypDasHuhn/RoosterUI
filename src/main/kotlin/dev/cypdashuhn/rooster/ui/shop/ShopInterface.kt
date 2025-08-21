package dev.cypdashuhn.rooster.ui.shop

import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.InterfaceInfo
import dev.cypdashuhn.rooster.ui.items.ItemBuilder

class ShopInterface

class ShopItem {
    fun <T : Context> addShopAttributes(
        item: ItemBuilder<T>,
        costs: InterfaceInfo<T>.() -> Number
    ) {

        item.onClick {

        }
    }
}