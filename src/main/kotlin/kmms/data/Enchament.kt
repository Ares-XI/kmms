package kmms.data

/**
 * Minecraft enchantment
 *
 * @property namespace Enchantment namespace (default: "minecraft")
 * @property name Enchantment name
 * @property level Enchantment level
 */
data class Enchantment(
  override val namespace: String = "minecraft",
  override val name: String,
  val level: Int
) : NamespacedKey(namespace, name) {

  /**
   * Convert enchantment to request data
   *
   * @return Request data map
   */
  fun toRequestData(): Map<String, Any> = mapOf(
    "key" to name,
    "nameSpace" to namespace,
    "level" to level
  )

  companion object {
    /**
     * Create enchantment from request data
     *
     * @param data Request data map
     * @return Enchantment instance
     */
    fun fromRequestData(data: Map<String, Any>): Enchantment = Enchantment(
      namespace = data["nameSpace"] as String,
      name = data["key"] as String,
      level = (data["level"] as Number).toInt()
    )
  }
}