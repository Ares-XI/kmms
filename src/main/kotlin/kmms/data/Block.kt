package kmms.data

import kmms.Utils.formatState

/**
 * Minecraft block
 *
 * @property namespace Block namespace (default: "minecraft")
 * @property name Block name
 * @property location Block location
 * @property state Block state properties (default: empty map)
 * @property inventory Block inventory items (default: empty list)
 */
data class Block(
  override val namespace: String = "minecraft",
  override val name: String,
  val location: Location,
  val state: Map<String, Any> = emptyMap(),
  val inventory: List<ItemStack> = emptyList()
) : NamespacedKey(namespace, name) {

  /**
   * Get block data as string
   *
   * @return String that can be used to recreate this block data
   *
   * Example:
   * ```kotlin
   * Block(
   *     "minecraft", "chest",
   *     Location(world = "world", x = 161.0, y = 66.0, z = 119.0),
   *     mapOf("facing" to "west", "type" to "single", "waterlogged" to false)
   * ).getBlockDataAsString()
   * // "minecraft:chest[facing=west,type=single,waterlogged=false]"
   * ```
   */
  fun getBlockDataAsString(): String {
    var result = getFullName()
    if (state.isNotEmpty()) {
      result += formatState(state)
    }
    return result
  }

  /**
   * Convert block to request data
   *
   * @return Request data map
   */
  fun toRequestData(): Map<String, Any> {
    val blockData = getBlockDataAsString()
    val items = inventory.map { it.toRequestData() }

    return mapOf(
      "locationData" to location.toRequestData(),
      "blockData" to blockData,
      "items" to items
    )
  }
}