package kmms.data

/**
 * Minecraft namespaced key
 *
 * @property namespace Namespace (default: "minecraft")
 * @property name Resource name
 */
open class NamespacedKey(
  open val namespace: String = "minecraft",
  open val name: String
) {
  /**
   * Get full name in namespace:name format
   *
   * @return Full namespaced key
   *
   * Example:
   * ```kotlin
   * NamespacedKey("minecraft", "chest").getFullName() // "minecraft:chest"
   * ```
   */
  fun getFullName(): String = "$namespace:$name"

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is NamespacedKey) return false
    return namespace == other.namespace && name == other.name
  }

  override fun hashCode(): Int = 31 * namespace.hashCode() + name.hashCode()

  override fun toString(): String = getFullName()
}