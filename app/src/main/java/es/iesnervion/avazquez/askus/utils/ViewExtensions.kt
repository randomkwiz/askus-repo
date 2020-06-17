import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.TranslateAnimation

fun ViewGroup.inflate(layoutRes: Int): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, false)
}

fun View.setVisibilityToGone() {
    this.visibility = View.GONE
}

fun View.setVisibilityToVisible() {
    this.visibility = View.VISIBLE
}

// slide the view from below itself to the current position
fun View.slideUp() {
    val animate = TranslateAnimation(0F,  // fromXDelta
        0F,  // toXDelta
        this.height.toFloat(),  // fromYDelta
        0F) // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    this.startAnimation(animate)
}

// slide the view from its current position to below itself
fun View.slideDown() {
    val animate = TranslateAnimation(0F,  // fromXDelta
        0F,  // toXDelta
        0F,  // fromYDelta
        this.height.toFloat()) // toYDelta
    animate.duration = 500
    animate.fillAfter = true
    this.startAnimation(animate)
}

fun Float.round(decimals: Int = 2): String = "%.${decimals}f".format(this)