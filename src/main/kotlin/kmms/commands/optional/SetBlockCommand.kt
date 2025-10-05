package kmms.commands.optional

import kmms.Utils.formatCoordinates
import kmms.Utils.formatState
import kmms.Utils.formatTags
import kmms.commands.CommandType
import kmms.commands.StringCommand
import kmms.enums.BlockChange
import kmms.enums.Coordinate

class SetBlockCommand(
  private val x: Number,
  private val y: Number,
  private val z: Number,
  private val blockName: String,
  private val coordinate: Coordinate? = null,
  private val change: BlockChange = BlockChange.REPLACE,
  private val state: Map<String, String>? = null,
  private val tags: Map<String, String>? = null
) : StringCommand() {

  override fun getCommandText(): String {
    val coordinates = formatCoordinates(x, y, z, coordinate)
    var command = "setblock $coordinates $blockName"

    state?.let {
      command += formatState(it)
    }

    tags?.let {
      command += formatTags(it)
    }

    if (change != BlockChange.REPLACE) {
      command += " $change"
    }

    return command
  }

  override fun getCommandType(): CommandType {
    return CommandType.SET_BLOCK
  }
}