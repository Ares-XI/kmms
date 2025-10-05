package kmms.enums

/**
 * Block facing directions
 */
enum class Facing(val value: String) {
  /** North direction */
  NORTH("north"),

  /** South direction */
  SOUTH("south"),

  /** East direction */
  EAST("east"),

  /** West direction */
  WEST("west");

  /**
   * Returns the string representation of the facing direction
   *
   * @return String value for use in commands and block states
   */
  override fun toString(): String = value
}