package kmms.enums

/**
 * Block change types for setblock command
 */
enum class BlockChange(val value: String) {
  /** Replace the existing block */
  REPLACE("replace"),

  /** Destroy the existing block (drops items) */
  DESTROY("destroy"),

  /** Keep the existing block if not air */
  KEEP("keep");

  /**
   * Returns the string representation of the block change type
   *
   * @return String value for use in commands
   */
  override fun toString(): String = value
}