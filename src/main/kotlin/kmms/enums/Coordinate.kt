package kmms.enums

/**
 * Coordinate types for position specification
 */
enum class Coordinate(val value: String) {
  /** Absolute world coordinates */
  ABSOLUTE(""),

  /** Relative coordinates (~) */
  RELATIVE("~"),

  /** Local coordinates (^) */
  LOCAL("^");

  /**
   * Returns the string representation of the coordinate type
   *
   * @return String prefix for coordinates
   */
  override fun toString(): String = value
}