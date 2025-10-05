package kmms.connection

import kmms.commands.StringCommand
import kmms.data.Block
import kmms.data.GetBlockResponse
import kmms.data.Location
import kmms.data.Player
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Connection to MCMS (Minecraft Server Management System)
 *
 * This class provides methods to interact with Minecraft server through HTTP API.
 * It handles authentication, request serialization and response parsing.
 *
 * @param address Server IP address or hostname
 * @param port Server port number
 * @param credentials Optional username/password for basic authentication
 *
 * Example:
 * ```kotlin
 * val connection = Connection("localhost", 8000)
 * val authConnection = Connection("localhost", 8000, "admin" to "password")
 * ```
 */
class Connection(
  private val address: String,
  private val port: Int,
  private val credentials: Pair<String, String>? = null
) {
  private val baseUrl = "http://$address:$port"

  /**
   * Returns the connection string representation
   */
  override fun toString(): String = baseUrl

  /**
   * Send HTTP POST request to MCMS API
   *
   * @param path API endpoint path (e.g., "/runCommand")
   * @param data Data to send in the request body
   * @param timeout Request timeout in seconds (default: 10)
   * @return Parsed response from server
   * @throws IOException if request fails or returns non-200 status
   */
  private fun httpPost(path: String, data: Any, timeout: Int = 10): Any {
    // Convert data to JSON string
    val json = when (data) {
      is String -> "\"${data.replace("\"", "\\\"")}\""
      is Number, is Boolean -> data.toString()
      is List<*> -> data.joinToString(",", "[", "]") { httpPost("", it ?: "") as String }
      is Map<*, *> -> data.entries.joinToString(",", "{", "}") {
        "\"${it.key}\":${httpPost("", it.value ?: "")}"
      }
      else -> "\"$data\""
    }

    // Create and configure HTTP connection
    val connection = URL("$baseUrl$path").openConnection() as HttpURLConnection
    connection.apply {
      requestMethod = "POST"
      doOutput = true
      setRequestProperty("Content-Type", "text/plain")
      connectTimeout = timeout * 1000
      readTimeout = timeout * 1000

      // Add basic authentication if credentials provided
      credentials?.let { (username, password) ->
        val auth = "$username:$password"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
        setRequestProperty("Authorization", "Basic $encodedAuth")
      }

      // Send encoded data
      outputStream.use { output ->
        val encodedData = Base64.getEncoder().encode(json.toByteArray())
        output.write(encodedData)
      }
    }

    // Check response status
    if (connection.responseCode !in 200..299) {
      throw IOException("HTTP ${connection.responseCode}: ${connection.responseMessage}")
    }

    // Read and parse response
    return connection.inputStream.use { input ->
      val responseText = input.bufferedReader().readText()
      parseJson(responseText) ?: ""
    }
  }

  /**
   * Parse JSON string into Kotlin objects
   *
   * @param jsonText JSON string to parse
   * @return Parsed object (Map, List, String, Number, Boolean, or null)
   */
  private fun parseJson(jsonText: String): Any? {
    val trimmed = jsonText.trim()
    return when {
      trimmed == "true" -> true
      trimmed == "false" -> false
      trimmed == "null" -> null
      trimmed.startsWith('{') -> parseJsonObject(trimmed)
      trimmed.startsWith('[') -> parseJsonArray(trimmed)
      trimmed.startsWith('"') -> parseJsonString(trimmed)
      else -> parseJsonNumber(trimmed)
    }
  }

  /**
   * Parse JSON object into Map
   */
  private fun parseJsonObject(json: String): Map<String, Any> {
    val result = mutableMapOf<String, Any>()
    var content = json.substring(1, json.length - 1).trim()

    while (content.isNotEmpty()) {
      // Extract key
      val key = parseJson(content.substringBefore(':')) as String
      content = content.substringAfter(':').trim()

      // Extract value
      val valueEnd = findJsonValueEnd(content)
      val valueStr = content.substring(0, valueEnd)
      result[key] = parseJson(valueStr) ?: return mapOf()

      // Move to next key-value pair
      content = content.substring(valueEnd).removePrefix(",").trim()
    }

    return result
  }

  /**
   * Parse JSON array into List
   */
  private fun parseJsonArray(json: String): List<Any> {
    val result = mutableListOf<Any>()
    var content = json.substring(1, json.length - 1).trim()

    while (content.isNotEmpty()) {
      val valueEnd = findJsonValueEnd(content)
      val valueStr = content.substring(0, valueEnd)
      result.add(parseJson(valueStr) ?: "")
      content = content.substring(valueEnd).removePrefix(",").trim()
    }

    return result
  }

  /**
   * Parse JSON string (remove quotes and unescape characters)
   */
  private fun parseJsonString(json: String): String {
    return json.substring(1, json.length - 1).replace("\\\"", "\"")
  }

  /**
   * Parse JSON number (try Long first, then Double, fallback to String)
   */
  private fun parseJsonNumber(json: String): Any {
    return json.toLongOrNull() ?: json.toDoubleOrNull() ?: json
  }

  /**
   * Find the end of a JSON value (accounting for nested structures and strings)
   */
  private fun findJsonValueEnd(json: String): Int {
    var depth = 0
    var inString = false
    var escape = false

    for ((index, char) in json.withIndex()) {
      when (char) {
        '"' -> if (!escape) inString = !inString
        '{', '[' -> if (!inString) depth++
        '}', ']' -> if (!inString) depth--
        ',' -> if (!inString && depth == 0) return index
        '\\' -> escape = !escape
        else -> if (escape) escape = false
      }

      if (char != '\\') escape = false
    }

    return json.length
  }

  /**
   * Execute a command or list of commands on the Minecraft server
   *
   * The command can be:
   * - String: simple command string
   * - StringCommand: command object
   * - Pair<String, String>: command and player name
   * - Pair<StringCommand, String>: command object and player name
   * - List of any above types
   *
   * @param command Command(s) to execute
   * @param timeout Request timeout in seconds (default: 10)
   * @return List of boolean results indicating command success
   *
   * Example:
   * ```kotlin
   * // Single string command
   * connection.executeCommands("time set day")
   *
   * // StringCommand object
   * val cmd = ClearCommand("Steve", "dirt", 5)
   * connection.executeCommands(cmd)
   *
   * // Command with player context
   * connection.executeCommands(Pair("give diamond", "Steve"))
   *
   * // Multiple commands
   * connection.executeCommands(listOf("time set day", "weather clear"))
   * ```
   */
  fun executeCommands(command: Any, timeout: Int = 10): List<Boolean> {
    /**
     * Convert command to API request format
     */
    fun convertToRequestFormat(cmd: Any): Map<String, String> {
      return when (cmd) {
        is String -> mapOf("commandText" to cmd)
        is StringCommand -> mapOf("commandText" to cmd.getCommandText())
        is Pair<*, *> -> {
          val (commandPart, playerName) = cmd
          when (commandPart) {
            is StringCommand -> mapOf(
              "commandText" to commandPart.getCommandText(),
              "playerName" to playerName.toString()
            )
            is String -> mapOf(
              "commandText" to commandPart,
              "playerName" to playerName.toString()
            )
            else -> throw IllegalArgumentException("Invalid command type in pair")
          }
        }
        else -> throw IllegalArgumentException("Invalid command type: ${cmd::class.simpleName}")
      }
    }

    val commandsList = if (command is List<*>) {
      command.map { convertToRequestFormat(it!!) }
    } else {
      listOf(convertToRequestFormat(command))
    }

    @Suppress("UNCHECKED_CAST")
    return httpPost("/runCommand", commandsList, timeout) as List<Boolean>
  }

  /**
   * Get block information from specified location(s)
   *
   * @param location Single location or list of locations
   * @param timeout Request timeout in seconds (default: 10)
   * @return List of GetBlockResponse objects with block information
   *
   * Example:
   * ```kotlin
   * // Single location
   * val block = connection.getBlocks(Location(100, 64, 200))
   *
   * // Multiple locations
   * val locations = listOf(Location(100, 64, 200), Location(101, 64, 200))
   * val blocks = connection.getBlocks(locations)
   * ```
   */

  fun getBlocks(location: Any, timeout: Int = 10): List<GetBlockResponse> {
    val locations = if (location is List<*>) {
      location.filterIsInstance<Location>()
    } else {
      listOf(location as Location)
    }

    val blocksList = locations.map { it.toRequestData() }
    val blockResponse = httpPost("/getBlock", blocksList, timeout) as List<Map<String, Any>>

    // Combine request locations with response data
    val blockSource = locations.zip(blockResponse)
    return blockSource.map { (request, response) ->
      GetBlockResponse.fromRequestResponseData(request, response)
    }
  }

  /**
   * Set blocks at specified location(s)
   *
   * @param blocks Single block or list of blocks to set
   * @param timeout Request timeout in seconds (default: 10)
   * @return List of pairs (success, errorMessage) for each block operation
   *
   * Example:
   * ```kotlin
   * // Single block
   * val result = connection.setBlocks(Block(100, 64, 200, "stone"))
   *
   * // Multiple blocks
   * val blocks = listOf(
   *     Block(100, 64, 200, "stone"),
   *     Block(101, 64, 200, "dirt")
   * )
   * val results = connection.setBlocks(blocks)
   * ```
   */

  fun setBlocks(blocks: Any, timeout: Int = 10): List<Pair<Boolean, String>> {
    val blocksList = if (blocks is List<*>) {
      blocks.filterIsInstance<Block>()
    } else {
      listOf(blocks as Block)
    }

    val blocksRequest = blocksList.map { it.toRequestData() }

    @Suppress("UNCHECKED_CAST")
    return httpPost("/setBlock", blocksRequest, timeout) as List<Pair<Boolean, String>>
  }

  /**
   * Get player information from the server
   *
   * @param onlyOnline If true, returns only online players (default: true)
   * @param timeout Request timeout in seconds (default: 10)
   * @return List of Player objects
   *
   * Example:
   * ```kotlin
   * // Online players only
   * val onlinePlayers = connection.getPlayers()
   *
   * // All players (including offline)
   * val allPlayers = connection.getPlayers(onlyOnline = false)
   * ```
   */

  fun getPlayers(onlyOnline: Boolean = true, timeout: Int = 10): List<Player> {
    val response = httpPost("/getPlayers", onlyOnline, timeout) as List<Map<String, Any>>
    return response.map { Player.fromRequestData(it) }
  }

}