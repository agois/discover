package agois.com.restaurants

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import agois.com.restaurants.ui.restaurantlist.RestaurantListFragment

class RestaurantListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.restaurant_list_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RestaurantListFragment.newInstance())
                .commitNow()
        }
    }

}
