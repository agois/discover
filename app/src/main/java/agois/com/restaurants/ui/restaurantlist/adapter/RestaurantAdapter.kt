package agois.com.restaurants.ui.restaurantlist.adapter

import agois.com.restaurants.BR
import agois.com.restaurants.R
import agois.com.restaurants.databinding.LoadingListItemBinding
import agois.com.restaurants.databinding.RestaurantListItemBinding
import agois.com.restaurants.model.RestaurantItem
import android.app.Activity
import androidx.databinding.ViewDataBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.lang.IllegalStateException


class RestaurantAdapter(private val activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val UNKNOWN_ITEM = -1
    private val RESTAURANT_ITEM = 0
    private val LOADING_ITEM = 1

    private val inflater: LayoutInflater by lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(activity) }

    private val listItems: List<ListItem>
        get() = differ.currentList

    private val diffCallback = object: DiffUtil.ItemCallback<ListItem>() {
        override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem) =
            oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(result: List<RestaurantItem>?) {
        result?.let { list ->
            val items = list.map { item ->
                var status = item.status.orEmpty()
                if (status.startsWith("pre-order for", ignoreCase = true)) {
                    status = activity.getString(R.string.closed)
                }
                RestaurantListItemViewData(
                    item.id.orEmpty(),
                    item.name.orEmpty(),
                    status,
                    item.imageUrl.orEmpty(),
                    item.description.orEmpty()
                )
            }
            differ.submitList(items)
        }
    }

    fun setLoadingNextPage(isLoading: Boolean) {
        if (isLoading) {
            val list = mutableListOf<ListItem>().apply {
                addAll(listItems)
                add(LoadingItem())
            }
            differ.submitList(list)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (listItems[position])  {
            is RestaurantListItemViewData -> RESTAURANT_ITEM
            is LoadingItem -> LOADING_ITEM
            else -> UNKNOWN_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = when (viewType) {
            RESTAURANT_ITEM -> {
                RestaurantListItemBinding.inflate(inflater, parent, false)
            }
            LOADING_ITEM -> {
                LoadingListItemBinding.inflate(inflater, parent, false)
            }
            else -> throw IllegalStateException()
        }
        return RestaurantItemViewHolder(binding, BR.model)
    }

    override fun getItemCount() = listItems.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RestaurantItemViewHolder).bind(listItems[position])
    }

    private class RestaurantItemViewHolder(val binding: ViewDataBinding, val variableId: Int) : RecyclerView.ViewHolder(binding.root) {
        internal fun bind(item: ListItem) {
            binding.setVariable(variableId, item)
            binding.executePendingBindings()
        }
    }
}