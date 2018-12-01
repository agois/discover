package agois.com.restaurants.ui.restaurantlist

import agois.com.restaurants.model.RestaurantItem
import agois.com.restaurants.service.RestaurantsRepo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign

enum class ListViewState {
    NotInitialized,
    Initialized,
    Empty,
    LastPage,
    Error,
    LoadingFirstPage,
    LoadingNextPage
}

class RestaurantListViewModelFactory(private val repo: RestaurantsRepo) : ViewModelProvider.Factory {

    override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RestaurantListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RestaurantListViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RestaurantListViewModel(private val repo: RestaurantsRepo) : ViewModel() {
    val viewState: LiveData<ListViewState>
        get() = viewStateLiveData

    val list: LiveData<List<RestaurantItem>>
        get() = listLiveData

    private val disposables = CompositeDisposable()
    private val restaurants = ArrayList<RestaurantItem>()
    private val listLiveData = MutableLiveData<List<RestaurantItem>>().apply { value = restaurants }
    private val viewStateLiveData = MutableLiveData<ListViewState>().apply { value = ListViewState.NotInitialized }
    private var nextPage: Int = 0

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        if (viewStateLiveData.value in listOf(ListViewState.LoadingFirstPage, ListViewState.LoadingNextPage))
            return

        disposables += repo.getRestaurantsNearHQ(nextPage)
            .doOnSubscribe { viewStateLiveData.value = if (nextPage == 0) ListViewState.LoadingFirstPage else ListViewState.LoadingNextPage }
            .subscribe({
                if (it.isEmpty()) {
                    viewStateLiveData.value = if (nextPage == 0) ListViewState.Empty else ListViewState.LastPage
                } else {
                    viewStateLiveData.value = ListViewState.Initialized
                }

                restaurants.addAll(it)
                listLiveData.value = restaurants

                nextPage++
            }, {
                if (nextPage == 0) {
                    viewStateLiveData.value = ListViewState.Error
                } else {
                    viewStateLiveData.value = ListViewState.Initialized
                }
            })
    }

    fun onRetry() {
        loadNextPage()
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
