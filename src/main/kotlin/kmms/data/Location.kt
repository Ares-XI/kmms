package kmms.data

import kmms.enums.Facing

/**
 * Minecraft location
 *
 * @property world World name (default: "world")
 * @property x X coordinate
 * @property y Y coordinate
 * @property z Z coordinate
 * @property yaw Yaw rotation (default: 0)
 * @property pitch Pitch rotation (default: 0)
 */
data class Location(
  val world: String = "world",
  val x: Double,
  val y: Double,
  val z: Double,
  val yaw: Double = 0.0,
  val pitch: Double = 0.0
) {
  /**
   * Get facing direction based on yaw
   *
   * @return Facing direction
   */
  fun getFacing(): Facing {
    val levels = mapOf(
      (-45.0 to 45.0) to Facing.SOUTH,
      (45.0 to 135.0) to Facing.WEST,
      (135.0 to 180.0) to Facing.NORTH,
      (-180.0 to -135.0) to Facing.NORTH,
      (-135.0 to -45.0) to Facing.EAST
    )

    for ((borders, facing) in levels) {
      if (yaw > borders.first && yaw <= borders.second) {
        return facing
      }
    }
    return Facing.NORTH
  }

  /**
   * Convert location to request data
   *
   * @return Request data map
   */
  fun toRequestData(): Map<String, Any> = mapOf(
    "worldName" to world,
    "x" to x,
    "y" to y,
    "z" to z,
    "yaw" to yaw,
    "pitch" to pitch
  )

  companion object {
    /**
     * Create location from request data
     *
     * @param data Request data map
     * @return Location instance
     */
    fun fromRequestData(data: Map<String, Any>): Location = Location(
      world = data["worldName"] as String,
      x = (data["x"] as Number).toDouble(),
      y = (data["y"] as Number).toDouble(),
      z = (data["z"] as Number).toDouble(),
      yaw = (data["yaw"] as Number).toDouble(),
      pitch = (data["pitch"] as Number).toDouble()
    )
  }
}