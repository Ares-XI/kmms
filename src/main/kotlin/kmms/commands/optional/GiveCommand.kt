package kmms.commands.optional

import kmms.Utils.formatTags
import kmms.commands.CommandType
import kmms.commands.StringCommand

class GiveCommand(

  private val playerName: String,
  private val itemName: String,
  private val amount: Int = 1,
  private val tags: Map<String, Any>? = null

) : StringCommand() {

  override fun getCommandText(): String {
    var command = "give $playerName $itemName"

    tags?.let {
      command += formatTags(it)
    }

    if (amount > 1) {
      command += " $amount"
    }

    return command
  }

  override fun getCommandType(): CommandType {
    return CommandType.GIVE
  }

}