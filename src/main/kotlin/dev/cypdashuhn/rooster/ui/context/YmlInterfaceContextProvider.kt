package dev.cypdashuhn.rooster.ui.context

import com.google.gson.Gson
import dev.cypdashuhn.rooster.database.YmlOperations
import dev.cypdashuhn.rooster.database.YmlShell
import dev.cypdashuhn.rooster.ui.interfaces.Context
import dev.cypdashuhn.rooster.ui.interfaces.RoosterInterface
import dev.cypdashuhn.rooster.util.uuid
import org.bukkit.entity.Player

class YmlInterfaceContextProvider : InterfaceContextProvider(), YmlOperations by YmlShell("interfaceContexts.yml") {
    override fun <T : Context> updateContext(player: Player, interfaceInstance: RoosterInterface<T>, context: T) {
        changeConfig {
            config.set("Players.${player.uuid()}.${interfaceInstance.interfaceName}", Gson().toJson(context))
        }
    }

    override fun <T : Context> getContext(player: Player, interfaceInstance: RoosterInterface<T>): T? {
        val jsonString = config.getString("Players.${player.uuid()}.${interfaceInstance.interfaceName}")
        return Gson().fromJson(jsonString, interfaceInstance.contextClass.java)
    }
}