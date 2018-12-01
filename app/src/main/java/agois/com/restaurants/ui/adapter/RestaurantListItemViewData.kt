package agois.com.restaurants.ui.adapter

interface ListItem {
    val id: String
}

data class RestaurantListItemViewData(
    override val id: String,
    val name: String,
    val status: String,
    val imageUrl: String,
    val description: String
) : ListItem

class LoadingItem(override val id: String = "loading") : ListItem