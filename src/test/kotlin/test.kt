import dev.cypdashuhn.rooster.common.util.createItem
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.items.InterfaceItem
import org.bukkit.Material

fun main() {
    println("Hello World")

    var item = InterfaceItem(Context::class).atSlot(4).displayAs { createItem(Material.BEDROCK) }
    println(item.slots?.slots?.toList().toString())
}