package agois.com.restaurants.ui.restaurantlist

import agois.com.restaurants.databinding.RestaurantListFragmentBinding
import agois.com.restaurants.model.RestaurantItem
import agois.com.restaurants.service.restaurantsRepo
import agois.com.restaurants.ui.restaurantlist.adapter.RestaurantAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders


class RestaurantListFragment : Fragment() {

    companion object {
        fun newInstance() = RestaurantListFragment()
    }

    private lateinit var viewModel: RestaurantListViewModel
    private lateinit var adapter: RestaurantAdapter
    private lateinit var binding: RestaurantListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = RestaurantListFragmentBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter = RestaurantAdapter(requireActivity())
        viewModel = ViewModelProviders.of(this, RestaurantListViewModelFactory(restaurantsRepo)).get(RestaurantListViewModel::class.java)

        binding.model = viewModel
        binding.setLifecycleOwner(this)
        binding.restaurantList.adapter = adapter

        val layoutManager = binding.restaurantList.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        binding.restaurantList.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (viewModel.viewState.value != ListViewState.LoadingNextPage && viewModel.viewState.value != ListViewState.LastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                    ) {
                        viewModel.loadNextPage()
                    }
                }
            }
        })

        viewModel.list.observe(this, Observer<List<RestaurantItem>> { list ->
            adapter.submitList(list)
        })

        viewModel.viewState.observe(this, Observer {
            adapter.setLoadingNextPage(it == ListViewState.LoadingNextPage)
        })
    }
}
