package kmms.commands

abstract class StringCommand{

  companion object {

    fun init(commandType: CommandType, vararg args: String): StringCommand {
      return object: StringCommand() {

        override fun getCommandText(): String {
          return commandType.command + args.joinToString(" ")
        }

        override fun getCommandType(): CommandType {
          return commandType
        }

      }
    }

    fun init(str: String, vararg args: String): StringCommand {
      return object: StringCommand() {

        override fun getCommandText(): String {
          return str + args.joinToString(" ")
        }

        override fun getCommandType(): CommandType {
          return CommandType.CUSTOM(str)
        }

      }
    }
  }

  abstract fun getCommandText(): String

  abstract fun getCommandType(): CommandType

  protected fun composeCommand(vararg args: Any?): String {
    return args.filterNotNull().joinToString(" ") { it.toString() }
  }

  override fun toString(): String {
    return "${this::class.simpleName}: ${getCommandText()}"
  }

}