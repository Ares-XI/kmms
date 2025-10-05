package kmms.enums

/**
 * Block handling types for fill command
 */
enum class BlockHandling(val value: String) {
  /** Replace blocks on the outer edge only */
  HOLLOW("hollow"),

  /** Replace blocks on the outer edge only (same as hollow) */
  OUTLINE("outline"),

  /** Replace all blocks in the region */
  REPLACE("replace"),

  /** Destroy all blocks in the region (drops items) */
  DESTROY("destroy"),

  /** Keep existing blocks if not air */
  KEEP("keep");

  /**
   * Returns the string representation of the block handling type
   *
   * @return String value for use in commands
   */
  override fun toString(): String = value
}