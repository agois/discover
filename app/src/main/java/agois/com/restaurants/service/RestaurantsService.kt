package agois.com.restaurants.service

import agois.com.restaurants.model.RestaurantItem
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantsService {
    @GET("v2/restaurant/")
    fun getRestaurantList(@Query("lat") lat: String, @Query("lng") lon: String,
                          @Query("limit") limit: Int, @Query("offset") offset: Int): Single<List<RestaurantItem>>
}