package kmms.data

/**
 * Minecraft player
 *
 * @property name Player name
 * @property isOnline Whether player is currently online
 * @property uuid Player UUID
 * @property address Player IP address (nullable if offline)
 * @property location Player location (nullable if offline)
 * @property health Player health (nullable if offline)
 * @property items Player inventory items (nullable if offline)
 */
data class Player(
  val name: String,
  val isOnline: Boolean,
  val uuid: String,
  val address: String? = null,
  val location: Location? = null,
  val health: Double? = null,
  val items: List<ItemStack>? = null
)
 {
  companion object {
    /**
     * Create player from request data
     *
     * @param data Request data map
     * @return Player instance
     */
    fun fromRequestData(data: Map<String, Any>): Player {
      val isOnline = data["isOnline"] as Boolean

      return Player(
        name = data["playerName"] as String,
        isOnline = isOnline,
        uuid = data["UUID"] as String,
        address = if (isOnline) data["address"] as String else null,
        location = if (isOnline) {
          Location.fromRequestData(data["location"] as Map<String, Any>)
        } else null,
        health = if (isOnline) (data["health"] as Number).toDouble() else null,
        items = if (isOnline) {
          (data["items"] as List<Map<String, Any>>).map {
            ItemStack.fromRequestData(it)
          }
        } else null
      )
    }
  }
}