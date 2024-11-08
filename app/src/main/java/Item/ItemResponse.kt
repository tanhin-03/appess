
data class ItemResponse(
    var totalItems: Int = 0,
    var totalPages: Int = 0,
    var pageSize: Int = 0,
    var page: Int = 0,
    var items: List<Item> = listOf()
)