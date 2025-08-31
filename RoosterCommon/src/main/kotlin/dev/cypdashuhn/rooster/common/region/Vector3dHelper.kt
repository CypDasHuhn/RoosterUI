package dev.cypdashuhn.rooster.common.region

import dev.cypdashuhn.rooster.common.util.toComponent
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.joml.Vector3d
import kotlin.math.absoluteValue

infix fun Vector3d.distance(vector3d: Vector3d): Vector3d {
    return Vector3d(this.x - vector3d.x, this.y - vector3d.y, this.z - vector3d.z)
}

fun compareVectors(player: Player, vector3d1: Vector3d, vector3d2: Vector3d): Boolean {
    val distance = vector3d1 distance vector3d2
    if (distance.x != 0.0 || distance.y != 0.0 || distance.z != 0.0) {
        fun distanceCheck(name: String, field: Double) {
            if (field == 0.0) return

            val biggerOrSmaller =
                Component.translatable(if (field > 0.0) "rooster.vector.bigger" else "rooster.vector.smaller")

            player.sendMessage(
                Component.translatable(
                    "rooster.vector.${name}_error",
                    field.absoluteValue.toString().toComponent(),
                    biggerOrSmaller
                ),
            )
        }

        player.sendMessage(Component.translatable("rooster.vector.error"))
        distanceCheck("x", distance.x)
        distanceCheck("y", distance.y)
        distanceCheck("z", distance.z)
        return false
    } else return true
}
