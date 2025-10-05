package kmms.commands.optional

import kmms.commands.CommandType
import kmms.commands.StringCommand

class ClearCommand(
  private val playerName: String,
  private val itemName: String? = null,
  private val amount: Int? = null
) : StringCommand() {

  override fun getCommandText(): String {
    val command = "clear $playerName"
    return composeCommand(command, itemName, amount)
  }

  override fun getCommandType(): CommandType {
    return CommandType.CLEAR
  }

}