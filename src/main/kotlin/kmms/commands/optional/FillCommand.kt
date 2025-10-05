package kmms.commands.optional

import kmms.Utils.formatCoordinates
import kmms.commands.CommandType
import kmms.commands.StringCommand
import kmms.enums.BlockHandling
import kmms.enums.Coordinate

class FillCommand(
  private val x1: Number,
  private val y1: Number,
  private val z1: Number,
  private val x2: Number,
  private val y2: Number,
  private val z2: Number,
  private val blockName: String,
  private val coordinate: Coordinate? = null,
  private val blockHandling: BlockHandling? = null,
  private val replaceBlockName: String? = null
) : StringCommand() {

  override fun getCommandText(): String {
    val coordinatesStart = formatCoordinates(x1, y1, z1, coordinate)
    val coordinatesEnd = formatCoordinates(x2, y2, z2, coordinate)
    var command = "fill $coordinatesStart $coordinatesEnd $blockName"

    blockHandling?.let { handling ->
      command += " $handling"
      if (handling == BlockHandling.REPLACE) {
        if (replaceBlockName == null) {
          throw IllegalArgumentException(
            "replaceBlockName must be specified if blockHandling is 'replace'"
          )
        }
        command += " $replaceBlockName"
      }
    }

    return command
  }

  override fun getCommandType(): CommandType {
    return CommandType.FILL
  }
}