package agois.com.restaurants

import agois.com.restaurants.model.RestaurantItem
import agois.com.restaurants.rules.ImmediateSchedulersRule
import agois.com.restaurants.service.RestaurantsRepo
import agois.com.restaurants.service.RestaurantsService
import agois.com.restaurants.ui.restaurantlist.ListViewState
import agois.com.restaurants.ui.restaurantlist.RestaurantListViewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestViewModel {
    @Rule
    @JvmField
    val rxRule: ImmediateSchedulersRule = ImmediateSchedulersRule()

    @Test
    fun `Test that when viewmodel is created first page is loaded as empty`() {
        val viewModel = RestaurantListViewModel(RestaurantsRepo(object: RestaurantsService {
            override fun getRestaurantList(
                lat: String,
                lon: String,
                limit: Int,
                offset: Int
            ): Single<List<RestaurantItem>> {
                return Single.just(emptyList())
            }
        }))

        assert(viewModel.viewState.value == ListViewState.Empty)
    }

    @Test
    fun `Test that when viewmodel is created first page is loaded with data`() {
        val viewModel = RestaurantListViewModel(RestaurantsRepo(object: RestaurantsService {
            override fun getRestaurantList(
                lat: String,
                lon: String,
                limit: Int,
                offset: Int
            ): Single<List<RestaurantItem>> {
                return Single.just(listOf(
                    RestaurantItem("1", null, null, null, null),
                    RestaurantItem("2", null, null, null, null)))
            }
        }))

        assert(viewModel.viewState.value == ListViewState.Initialized)
        assert(viewModel.list.value?.size == 2)
        assert(viewModel.list.value?.let { it[0].id == "1" } ?: false)
        assert(viewModel.list.value?.let { it[1].id == "2" } ?: false)
    }

    @Test
    fun `Test that when viewmodel gets an error on first page, it goes into error state`() {
        val viewModel = RestaurantListViewModel(RestaurantsRepo(object: RestaurantsService {
            override fun getRestaurantList(
                lat: String,
                lon: String,
                limit: Int,
                offset: Int
            ): Single<List<RestaurantItem>> {
                return Single.error(IllegalArgumentException())
            }
        }))

        assert(viewModel.viewState.value == ListViewState.Error)
    }

    @Test
    fun `Test that when viewmodel gets an error on second page, it goes into initialized state`() {
        val viewModel = RestaurantListViewModel(RestaurantsRepo(object: RestaurantsService {
            override fun getRestaurantList(
                lat: String,
                lon: String,
                limit: Int,
                offset: Int
            ): Single<List<RestaurantItem>> {
                return if (offset == 0) {
                    Single.just(
                        listOf(
                            RestaurantItem("1", null, null, null, null),
                            RestaurantItem("2", null, null, null, null)
                        )
                    )
                } else {
                    Single.error(IllegalArgumentException())
                }
            }
        }))

        viewModel.loadNextPage()

        assert(viewModel.viewState.value == ListViewState.Initialized)
    }

    @Test
    fun `Test that viewmodel gets second page correctly`() {
        val viewModel = RestaurantListViewModel(RestaurantsRepo(object: RestaurantsService {
            override fun getRestaurantList(
                lat: String,
                lon: String,
                limit: Int,
                offset: Int
            ): Single<List<RestaurantItem>> {
                return if (offset == 0) {
                    Single.just(
                        listOf(
                            RestaurantItem("1", null, null, null, null),
                            RestaurantItem("2", null, null, null, null)
                        )
                    )
                } else {
                    Single.just(
                        listOf(
                            RestaurantItem("3", null, null, null, null),
                            RestaurantItem("4", null, null, null, null)
                        )
                    )
                }
            }
        }))

        viewModel.loadNextPage()

        assert(viewModel.viewState.value == ListViewState.Initialized)
        assert(viewModel.list.value?.size == 4)
        assert(viewModel.list.value?.let { it[0].id == "1" } ?: false)
        assert(viewModel.list.value?.let { it[1].id == "2" } ?: false)
        assert(viewModel.list.value?.let { it[2].id == "3" } ?: false)
        assert(viewModel.list.value?.let { it[3].id == "4" } ?: false)
    }

    @Test
    fun `Test that viewmodel first page can be retried`() {
        var firstTime = true
        val viewModel = RestaurantListViewModel(RestaurantsRepo(object: RestaurantsService {
            override fun getRestaurantList(
                lat: String,
                lon: String,
                limit: Int,
                offset: Int
            ): Single<List<RestaurantItem>> {
                return if (firstTime) {
                    firstTime = false
                    Single.error(IllegalArgumentException())
                } else {
                    Single.just(
                        listOf(
                            RestaurantItem("1", null, null, null, null),
                            RestaurantItem("2", null, null, null, null)
                        )
                    )
                }
            }
        }))

        assert(viewModel.viewState.value == ListViewState.Error)

        viewModel.onRetry()

        assert(viewModel.viewState.value == ListViewState.Initialized)
        assert(viewModel.list.value?.size == 2)
        assert(viewModel.list.value?.let { it[0].id == "1" } ?: false)
        assert(viewModel.list.value?.let { it[1].id == "2" } ?: false)
    }
}
