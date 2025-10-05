package kmms.data

/**
 * Minecraft item stack
 *
 * @property namespace Item namespace (default: "minecraft")
 * @property name Item name
 * @property index Inventory index (default: -1)
 * @property count Item count
 * @property enchantments List of enchantments (default: empty list)
 */
data class ItemStack(
  override val namespace: String = "minecraft",
  override val name: String,
  val index: Int = -1,
  val count: Int,
  val enchantments: List<Enchantment> = emptyList()
) : NamespacedKey(namespace, name) {

  /**
   * Convert item stack to request data
   *
   * @return Request data map
   */
  fun toRequestData(): Map<String, Any> = mapOf(
    "materialKey" to name,
    "materialNameSpaceKey" to namespace,
    "Index" to index,
    "Count" to count,
    "enchantments" to enchantments.map { it.toRequestData() }
  )

  companion object {
    /**
     * Create item stack from request data
     *
     * @param data Request data map
     * @return ItemStack instance
     */
    fun fromRequestData(data: Map<String, Any>): ItemStack {
      val enchantments = if (data.containsKey("enchantments")) {
        (data["enchantments"] as List<Map<String, Any>>).map {
          Enchantment.fromRequestData(it)
        }
      } else {
        emptyList()
      }

      return ItemStack(
        name = data["materialKey"] as String,
        namespace = data["materialNameSpaceKey"] as String,
        index = (data["Index"] as Number).toInt(),
        count = (data["Count"] as Number).toInt(),
        enchantments = enchantments
      )
    }
  }
}