import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.setVisibilityToGone() {
    this.visibility = View.GONE
}

fun View.setVisibilityToVisible() {
    this.visibility = View.VISIBLE
}