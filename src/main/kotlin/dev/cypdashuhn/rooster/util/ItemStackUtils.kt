package dev.cypdashuhn.rooster.util

import net.kyori.adventure.text.TextComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class TranslatableItemStack {
    val itemStack: ItemStack
    val nameKey: String?
    val descriptionKey: String?
    lateinit var nameReplacements: Map<String, String>
    lateinit var descriptionReplacements: Map<String, String>

    fun create(): ItemStack {
        // TODO: Add Localization
        return itemStack
    }

    constructor(
        itemStack: ItemStack,
        nameKey: String? = null,
        descriptionKey: String? = null
    ) {
        this.itemStack = itemStack
        this.nameKey = nameKey
        this.descriptionKey = descriptionKey
    }

    constructor(
        material: Material,
        nameKey: String,
        nameReplacements: Map<String, String> = mapOf(),
        descriptionKey: String? = null,
        amount: Int = 1
    ) {
        this.itemStack = ItemStack(material, amount)
        this.nameKey = nameKey
        this.descriptionKey = descriptionKey

        this.nameReplacements = nameReplacements
    }

    constructor(
        material: Material,
        nameKey: String,
        nameReplacements: Map<String, String> = mapOf(),
        descriptionKey: String,
        descriptionReplacements: Map<String, String> = mapOf(),
        amount: Int = 1
    ) {
        this.itemStack = ItemStack(material, amount)
        this.nameKey = nameKey
        this.descriptionKey = descriptionKey

        this.nameReplacements = nameReplacements
        this.descriptionReplacements = descriptionReplacements
    }

    constructor(
        material: Material,
        nameKey: String? = null,
        descriptionKey: String? = null,
        descriptionReplacements: Map<String, String> = mapOf(),
        amount: Int = 1
    ) {
        this.itemStack = ItemStack(material, amount)
        this.nameKey = nameKey
        this.descriptionKey = descriptionKey

        this.descriptionReplacements = descriptionReplacements
    }
}

fun createItem(
    material: Material,
    name: TextComponent? = null,
    description: List<TextComponent>? = null,
    amount: Int = 1,
    additional: (ItemMeta) -> Unit = {},
): ItemStack {
    val item = ItemStack(material, amount)
    val itemMeta = item.itemMeta
    if (name != null) itemMeta.displayName(name)
    if (description != null) itemMeta.lore(description)
    additional(itemMeta)
    item.itemMeta = itemMeta

    return item
}

fun ItemStack.modify(
    material: Material?,
    name: TextComponent? = null,
    description: List<TextComponent>? = null,
    amount: Int? = null,
    additional: (ItemMeta) -> Unit = {},
): ItemStack {
    val itemMeta = this.itemMeta
    val item = if (material != null && this.type != material) ItemStack(material) else this
    if (amount != null) item.amount = amount
    if (name != null) itemMeta.displayName(name)
    if (description != null) itemMeta.lore(description)
    additional(itemMeta)
    item.itemMeta = itemMeta

    return item
}