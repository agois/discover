package agois.com.restaurants.ui.bindingadapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import androidx.databinding.BindingAdapter

class AppBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun loadImage(view: ImageView, imageUrl: String?) {
            Glide.with(view).load(imageUrl).into(view)
        }
    }
}
