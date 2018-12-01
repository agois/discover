package agois.com.restaurants.service

import agois.com.restaurants.model.RestaurantItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val restaurantsRepo: RestaurantsRepo by lazy {
    RestaurantsRepo(
        Retrofit.Builder()
            .baseUrl("https://api.doordash.com/")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<RestaurantsService>(RestaurantsService::class.java))
}

class RestaurantsRepo(private val restaurantsService: RestaurantsService) {

    private val doorDashLat = "37.422740"
    private val doorDashLon = "-122.139956"
    private val pageSize = 50

    private fun getRestarauntList(lat: String, lon: String, limit: Int, offset: Int): Single<List<RestaurantItem>> =
            restaurantsService.getRestaurantList(lat, lon, limit, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    fun getRestaurantsNearHQ(page: Int): Single<List<RestaurantItem>> =
            getRestarauntList(doorDashLat, doorDashLon, pageSize * (page + 1), page * pageSize)
}