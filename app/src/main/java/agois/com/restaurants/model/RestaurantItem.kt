package agois.com.restaurants.model

import com.google.gson.annotations.SerializedName

data class RestaurantItem(
    val id: String?,
    val name: String?,
    val description: String?,
    @SerializedName("cover_img_url")
    val imageUrl: String?,
    val status: String?
)