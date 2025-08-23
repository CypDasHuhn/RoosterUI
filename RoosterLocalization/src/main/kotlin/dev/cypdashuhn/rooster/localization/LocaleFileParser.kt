package dev.cypdashuhn.rooster.localization

import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object LocaleFileParser {
    data class TreeNode(val value: String? = null, val children: Map<String, TreeNode> = emptyMap()) {
        constructor(value: String) : this(value = value, children = emptyMap())

        constructor(children: Map<String, TreeNode>) : this(value = null, children = children)
    }

    fun parseLocalization(inputStream: InputStream): Map<String, TreeNode> {
        val gson = Gson()

        val type = object : TypeToken<Map<String, Any>>() {}.type
        val rawLocalization: Map<String, Any> =
            gson.fromJson(InputStreamReader(inputStream, StandardCharsets.UTF_8), type)

        return rawLocalization.mapValues { (_, value) -> convertToTreeNode(value) }
    }

    fun convertToTreeNode(value: Any): TreeNode {
        return when (value) {
            is String -> TreeNode(value)
            is Map<*, *> -> {
                val children =
                    value.mapValues { (_, childValue) -> convertToTreeNode(childValue!!) } as Map<String, TreeNode>
                TreeNode(children)
            }

            else -> throw IllegalArgumentException("Unsupported value type: $value")
        }
    }

    fun getValueFromNestedMap(map: Map<String, TreeNode>, key: String): String? {
        val keys = key.split(".")
        var currentMap = map

        for (i in 0 until keys.size - 1) {
            val currentNode = currentMap[keys[i]]
            if (currentNode == null || currentNode.children.isEmpty()) {
                return null
            }
            currentMap = currentNode.children
        }

        return currentMap[keys.last()]?.value
    }
}