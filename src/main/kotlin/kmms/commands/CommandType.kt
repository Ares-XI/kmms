package kmms.commands

class CommandType private constructor(str: String) {

  val command: String = str

  companion object {
    val CLEAR = CommandType("clear")
    val GIVE = CommandType("give")
    val SET_BLOCK = CommandType("setblock")
    val FILL = CommandType("fill")
    val SUMMON = CommandType("summon");

    fun CUSTOM(str: String): CommandType {
      return CommandType(str)
    }
  }
}