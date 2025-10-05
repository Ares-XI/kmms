package kmms.commands.optional

import kmms.Utils.formatCoordinates
import kmms.Utils.formatTags
import kmms.commands.CommandType
import kmms.commands.StringCommand
import kmms.enums.Coordinate

class SummonCommand(
  private val entityName: String,
  private val x: Number? = null,
  private val y: Number? = null,
  private val z: Number? = null,
  private val coordinate: Coordinate? = null,
  private val tags: Map<String, String>? = null
) : StringCommand() {

  override fun getCommandText(): String {
    val command = if (x != null && y != null && z != null) {
      val coordinates = formatCoordinates(x, y, z, coordinate)
      "summon $entityName $coordinates"
    } else {
      "summon $entityName"
    }

    return tags?.let { command + formatTags(it) } ?: command
  }

  override fun getCommandType(): CommandType {
    return CommandType.SUMMON
  }
}