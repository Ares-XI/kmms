package kmms.data

import kmms.Utils.parseBlockData

/**
 * Get block response
 *
 * @property success Whether the operation was successful
 * @property exception Error message if operation failed (nullable)
 * @property result Block data if operation succeeded (nullable)
 */
data class GetBlockResponse(
  val success: Boolean,
  val exception: String? = null,
  val result: Block? = null
) {
  companion object {
    /**
     * Parse get block response from request and response data
     *
     * @param blockRequest Location of the requested block
     * @param blockResponse Response data from server
     * @return GetBlockResponse instance
     */
    fun fromRequestResponseData(
      blockRequest: Location,
      blockResponse: Map<String, Any>
    ): GetBlockResponse {
      val block = if (blockResponse["success"] as Boolean) {
        val (blockFullName, state) = parseBlockData(blockResponse["result"] as String)
        val parts = blockFullName.split(":", limit = 2)

        val inventory = if (blockResponse.containsKey("items")) {
          (blockResponse["items"] as List<Map<String, String>>).map {
            ItemStack.fromRequestData(it)
          }
        } else {
          emptyList()
        }

        Block(
          namespace = parts[0],
          name = parts[1],
          location = blockRequest,
          state = state,
          inventory = inventory
        )
      } else {
        null
      }

      return GetBlockResponse(
        success = blockResponse["success"] as Boolean,
        exception = blockResponse["exception"] as? String,
        result = block
      )
    }
  }
}