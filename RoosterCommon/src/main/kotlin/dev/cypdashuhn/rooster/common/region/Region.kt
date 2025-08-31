package dev.cypdashuhn.rooster.common.region

import dev.cypdashuhn.rooster.common.util.Box
import dev.cypdashuhn.rooster.common.util.toVector3d
import dev.cypdashuhn.rooster.common.util.value
import org.bukkit.Axis
import org.bukkit.Chunk
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.joml.Vector3d

@Suppress("unused")
class Region(
    val edge1: Location,
    val edge2: Location
) {
    init {
        require(edge1.world == edge2.world) { "Locations of a Region must be in the same World" }
    }

    val world: World by lazy { edge1.world }

    val minX: Int by lazy { edge1.blockX.coerceAtMost(edge2.blockX) }
    val minY: Int by lazy { edge1.blockY.coerceAtMost(edge2.blockY) }
    val minZ: Int by lazy { edge1.blockZ.coerceAtMost(edge2.blockZ) }
    val maxX: Int by lazy { edge1.blockX.coerceAtLeast(edge2.blockX) }
    val maxY: Int by lazy { edge1.blockY.coerceAtLeast(edge2.blockY) }
    val maxZ: Int by lazy { edge1.blockZ.coerceAtLeast(edge2.blockZ) }

    val min: Location by lazy { Location(world, dMinX, dMinY, dMinZ) }
    val max: Location by lazy { Location(world, dMaxX, dMaxY, dMaxZ) }

    val dMinX: Double by lazy { minX.toDouble() }
    val dMinY: Double by lazy { minY.toDouble() }
    val dMinZ: Double by lazy { minZ.toDouble() }
    val dMaxX: Double by lazy { maxX.toDouble() }
    val dMaxY: Double by lazy { maxY.toDouble() }
    val dMaxZ: Double by lazy { maxZ.toDouble() }

    val sizeX: Int by lazy { maxX - minX + 1 }
    val sizeY: Int by lazy { maxY - minY + 1 }
    val sizeZ: Int by lazy { maxZ - minZ + 1 }

    val volume: Int by lazy { sizeX * sizeY * sizeZ }

    val sideSizeX: Int by lazy { sizeY * sizeZ }
    val sideSizeY: Int by lazy { sizeX * sizeZ }
    val sideSizeZ: Int by lazy { sizeX * sizeY }

    val dimensions: Vector3d by lazy { Vector3d(sizeX.toDouble(), sizeY.toDouble(), sizeZ.toDouble()) }

    val minXChunk: Int by lazy { minX / 16 }
    val minZChunk: Int by lazy { minZ / 16 }
    val maxXChunk: Int by lazy { maxX / 16 }
    val maxZChunk: Int by lazy { maxZ / 16 }

    val vector1: Vector3d by lazy { edge1.toVector3d() }
    val vector2: Vector3d by lazy { edge2.toVector3d() }

    val box: Box by lazy { vector1 to vector2 }

    fun contains(location: Location): Boolean {
        return minX <= location.x && location.x <= maxX &&
                minY <= location.y && location.y <= maxY &&
                minZ <= location.z && location.z <= maxZ
    }

    fun contains(region: Region, allowEdges: Boolean = false): Boolean {
        val isContained = contains(region.edge1) && contains(region.edge2)
        if (!isContained) return false

        return if (allowEdges) {
            true
        } else {
            val cornerAtEdge = isEdge(region.edge1) || isEdge(region.edge2)
            !cornerAtEdge
        }
    }

    fun contains(entity: Entity): Boolean {
        return contains(entity.location)
    }

    fun intersects(region: Region): Boolean {
        return contains(region.edge1) || contains(region.edge2)
    }

    val blocks: List<Block>
        get() {
            return iterateRegion { x, y, z ->
                world.getBlockAt(x, y, z)
            }
        }

    val blocksArray: Array<Array<Array<Block>>>
        get() {
            val blocksArray = Array(sizeX) { Array(sizeY) { arrayOfNulls<Block>(sizeZ) } }

            blocks.forEachIndexed { index, block ->
                val x = index % sizeX
                val y = (index / sizeX) % sizeY
                val z = index / (sizeX * sizeY)
                blocksArray[x][y][z] = block
            }
            return blocksArray as Array<Array<Array<Block>>>
        }

    val entities: List<Entity>
        get() = entities()

    fun entities(vararg types: EntityType): List<Entity> {
        return iterateRegion { x, y, z ->
            world.getNearbyEntities(Location(world, x.toDouble(), y.toDouble(), z.toDouble()), 1.0, 1.0, 1.0)
                .filter { entity ->
                    types.isEmpty() || types.contains(entity.type)
                }
        }.flatten()
    }

    @Suppress("UNCHECKED_CAST")
    val players: List<Player>
        get() = entities(EntityType.PLAYER) as List<Player>

    private inline fun <T> iterateRegion(
        blockSupplier: (Int, Int, Int) -> T
    ): List<T> {
        val items = mutableListOf<T>()
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val item = blockSupplier(x, y, z)
                    items.add(item)
                }
            }
        }
        return items
    }

    val chunks: Set<Chunk> by lazy {
        val chunks = mutableSetOf<Chunk>()
        for (x in minXChunk..maxXChunk) {
            for (z in minZChunk..maxZChunk) {
                chunks.add(world.getChunkAt(x, z))
            }
        }
        chunks
    }

    val chunksFull: Set<Chunk> by lazy {
        val chunks = mutableSetOf<Chunk>()
        for (x in minXChunk..maxXChunk) {
            for (z in minZChunk..maxZChunk) {
                val chunk = world.getChunkAt(x, z)
                if (isChunkFullyContained(chunk)) {
                    chunks.add(chunk)
                }
            }
        }
        chunks
    }

    private fun isChunkFullyContained(chunk: Chunk): Boolean {
        val chunkMinX = chunk.x / 16
        val chunkMinZ = chunk.z / 16
        val chunkMaxX = chunkMinX + 15
        val chunkMaxZ = chunkMinZ + 15

        return minX <= chunkMinX && maxX >= chunkMaxX && minZ <= chunkMinZ && maxZ >= chunkMaxZ
    }

    fun enlarge(amount: Int, vararg axes: Axis): Region {
        val faces = Face.entries.filter { axes.contains(it.axis) }
        return changeBorders(amount, true, *faces.toTypedArray())
    }

    fun enlarge(amount: Int, vararg faces: Face): Region {
        return changeBorders(amount, true, *faces)
    }

    fun shrink(amount: Int, vararg axes: Axis): Region {
        val faces = Face.entries.filter { axes.contains(it.axis) }
        return changeBorders(amount, false, *faces.toTypedArray())
    }

    fun shrink(amount: Int, vararg faces: Face): Region {
        return changeBorders(amount, false, *faces)
    }

    private fun changeBorders(amount: Int, enlarge: Boolean, vararg faces: Face): Region {
        var minEdge = edge1
        var maxEdge = edge2

        val allFaces = if (faces.isEmpty()) Face.entries.toTypedArray() else faces
        allFaces.forEach { face ->
            val coordinateChange = amount * (if (enlarge xor face.positive) 1 else -1)
            val changingEdge = if (enlarge xor face.positive) maxEdge else minEdge
            val modifiedEdge = when (face.axis) {
                Axis.X -> Location(world, changingEdge.x + coordinateChange, changingEdge.y, changingEdge.z)
                Axis.Y -> Location(world, changingEdge.x, changingEdge.y + coordinateChange, changingEdge.z)
                Axis.Z -> Location(world, changingEdge.x, changingEdge.y, changingEdge.z + coordinateChange)
            }
            if (enlarge xor face.positive) maxEdge = modifiedEdge else minEdge = modifiedEdge
        }

        return Region(minEdge, maxEdge)
    }

    fun isCorner(location: Location): Boolean = intersectingAxis(location) == 3
    fun isEdge(location: Location): Boolean = intersectingAxis(location) == 2
    fun isFace(location: Location): Boolean = intersectingAxis(location) == 1

    fun intersectingAxis(location: Location): Int {
        var intersectingAxis = 0
        if (location.x.toInt() == this.minX || location.x.toInt() == this.maxX) intersectingAxis++
        if (location.y.toInt() == this.minY || location.y.toInt() == this.maxY) intersectingAxis++
        if (location.z.toInt() == this.minZ || location.z.toInt() == this.maxZ) intersectingAxis++
        return intersectingAxis
    }

    fun edges(): List<Location> {
        return listOf(
            Location(world, dMinX, dMinY, dMinZ),
            Location(world, dMaxX, dMinY, dMinZ),
            Location(world, dMinX, dMaxY, dMinZ),
            Location(world, dMaxX, dMaxY, dMinZ),
            Location(world, dMinX, dMinY, dMaxZ),
            Location(world, dMaxX, dMinY, dMaxZ),
            Location(world, dMinX, dMaxY, dMaxZ),
            Location(world, dMaxX, dMaxY, dMaxZ)
        )
    }

    fun closesDistanceTo(location: Location): Double {
        val distanceEdge1 = edge1.distance(location)
        val distanceEdge2 = edge2.distance(location)

        return distanceEdge1.coerceAtMost(distanceEdge2)
    }

    fun closestDistanceToAxis(axis: Axis, value: Double): Double {
        val distanceEdge1 = value - edge1.value(axis)
        val distanceEdge2 = value - edge2.value(axis)

        return distanceEdge1.coerceAtMost(distanceEdge2)
    }

    enum class AxisComparison {
        BEHIND,
        INTERSECTING,
        BEFORE
    }

    fun compareToAxis(axis: Axis, value: Double, customBox: Box? = null): AxisComparison {
        val currentBox = customBox ?: this.box
        val distanceEdge1 = value - currentBox.first.value(axis)
        val distanceEdge2 = value - currentBox.second.value(axis)

        return if (distanceEdge1 > 0 && distanceEdge2 > 0) {
            AxisComparison.BEHIND
        } else if (distanceEdge1 < 0 && distanceEdge2 < 0) {
            AxisComparison.BEFORE
        } else AxisComparison.INTERSECTING
    }
}
