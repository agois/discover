package agois.com.restaurants.ui.restaurantlist

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import agois.com.restaurants.R

class RestaurantListFragment : Fragment() {

    companion object {
        fun newInstance() = RestaurantListFragment()
    }

    private lateinit var viewModel: RestaurantListViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.restaurant_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RestaurantListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
