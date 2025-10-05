package kmms

import kmms.enums.Coordinate

data object Utils{
  /**
   * Utilities for MCMS (Minecraft Server Management System)
   * Provides formatting and parsing functions for Minecraft data types
   */

  /**
   * Format state dictionary to Minecraft block state string
   *
   * @param state State dictionary to format
   * @return Formatted state string in format [key1=value1, key2=value2]
   *
   * Examples:
   * ```kotlin
   * formatState(mapOf("string_value" to "mystring", "int_value" to 1, "bool_value" to true))
   * // "[string_value=mystring, int_value=1, bool_value=true]"
   * ```
   */
  fun formatState(state: Map<String, Any>): String {
    return "[" + state.entries.joinToString(", ") { (key, value) ->
      "$key=$value"
    } + "]"
  }

  /**
   * Parse state data from block data string
   *
   * @param blockData Block data string to parse
   * @return State dictionary with parsed values
   *
   * Examples:
   * ```kotlin
   * parseState("minecraft:chest[string_value=mystring, int_value=1, bool_value=true]")
   * // mapOf("string_value" to "mystring", "int_value" to 1, "bool_value" to true)
   * ```
   */
  fun parseState(blockData: String): Map<String, Any> {
    fun parsePrimitive(value: String): Any {
      return when {
        value.toIntOrNull() != null -> value.toInt()
        value.equals("true", ignoreCase = true) -> true
        value.equals("false", ignoreCase = true) -> false
        else -> value
      }
    }

    val state = mutableMapOf<String, Any>()

    // Extract state part using regex
    val stateRegex = Regex("""\[(.*?)\]""")
    val stateMatch = stateRegex.find(blockData)

    stateMatch?.groups?.get(1)?.value?.let { stateContent ->
      stateContent.split(",").forEach { pair ->
        val keyValue = pair.split("=", limit = 2)
        if (keyValue.size == 2) {
          val key = keyValue[0].trim()
          val value = parsePrimitive(keyValue[1].trim())
          state[key] = value
        }
      }
    }

    return state
  }

  /**
   * Parse block data from string
   *
   * @param blockData Block data string to parse
   * @return Pair of block namespace and state dictionary
   *
   * Examples:
   * ```kotlin
   * parseBlockData("minecraft:chest[facing=west,type=single,waterlogged=false]")
   * // Pair("minecraft:chest", mapOf("facing" to "west", "type" to "single", "waterlogged" to false))
   * ```
   */
  fun parseBlockData(blockData: String): Pair<String, Map<String, Any>> {
    // Extract block name (everything before first '[')
    val namespaceData = blockData.substringBefore("[")

    // Parse state
    val stateData = parseState(blockData)

    return Pair(namespaceData, stateData)
  }

  /**
   * Format tags dictionary to Minecraft NBT string
   *
   * @param tags Tags dictionary to format
   * @return Formatted NBT string
   *
   * Examples:
   * ```kotlin
   * formatTags(mapOf(
   *     "Enchantments" to listOf(
   *         mapOf("id" to "sharpness", "lvl" to 999),
   *         mapOf("id" to "unbreaking", "lvl" to 999)
   *     )
   * ))
   * // "{Enchantments:[{id:\"sharpness\",lvl:999},{id:\"unbreaking\",lvl:999}]}"
   * ```
   */
  fun formatTags(tags: Map<String, Any>): String {
    fun formatType(value: Any): String {
      return when (value) {
        is Map<*, *> -> formatTags(value as Map<String, Any>)
        is List<*> -> "[" + value.joinToString(",") { item -> formatType(item!!) } + "]"
        is String -> "\"$value\""
        else -> value.toString()
      }
    }

    return "{" + tags.entries.joinToString(",") { (key, value) ->
      "$key:${formatType(value)}"
    } + "}"
  }

  /**
   * Format coordinates for Minecraft commands
   *
   * @param x X coordinate (Int or Pair<String, Int> for relative/local coordinates)
   * @param y Y coordinate (Int or Pair<String, Int> for relative/local coordinates)
   * @param z Z coordinate (Int or Pair<String, Int> for relative/local coordinates)
   * @param coordinate Optional coordinate type for all coordinates. If null,
   *        coordinates will be formatted according to their individual types
   * @return Formatted coordinates string
   *
   * Examples:
   * ```kotlin
   * formatCoordinates(Pair("~", 0), Pair("^", 70), 161)
   * // "~0 ^70 161"
   *
   * formatCoordinates(196, 11, 57, Coordinate.RELATIVE)
   * // "~196 ~11 ~57"
   * ```
   */
  fun formatCoordinates(
    x: Any, // Int or Pair<String, Int>
    y: Any, // Int or Pair<String, Int>
    z: Any, // Int or Pair<String, Int>
    coordinate: Coordinate? = null
  ): String {
    fun formatSingle(coord: Any): String {
      return when (coord) {
        is Pair<*, *> -> {
          val (prefix, value) = coord
          if (coordinate != null) {
            "${coordinate}$value"
          } else {
            "$prefix$value"
          }
        }
        is Int -> {
          if (coordinate != null) {
            "${coordinate}$coord"
          } else {
            coord.toString()
          }
        }
        else -> coord.toString()
      }
    }

    return "${formatSingle(x)} ${formatSingle(y)} ${formatSingle(z)}"
  }

  /**
   * Alternative overload for formatCoordinates with explicit types
   */
  fun formatCoordinates(
    x: Int,
    y: Int,
    z: Int,
    coordinate: Coordinate? = null
  ): String = formatCoordinates(x as Any, y as Any, z as Any, coordinate)

  /**
   * Alternative overload for formatCoordinates with explicit Pair types
   */
  fun formatCoordinates(
    x: Pair<String, Int>,
    y: Pair<String, Int>,
    z: Pair<String, Int>,
    coordinate: Coordinate? = null
  ): String = formatCoordinates(x as Any, y as Any, z as Any, coordinate)
}