package hr.from.ivantoplak.podplay.extensions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

private const val FADE_IN_DURATION = 1500L
private const val FADE_OUT_DURATION = 500L

fun View.show(show: Boolean = true) {
    if (visibility == VISIBLE && show) return
    if (visibility == GONE && !show) return
    visibility = if (show) VISIBLE else GONE
}

fun View.hide() {
    if (visibility == GONE) return
    visibility = GONE
}

fun View.fadeIn() {
    alpha = 0F
    show()
    animate()
        .alpha(1F)
        .setDuration(FADE_IN_DURATION)
        .setListener(null)
}

fun View.fadeOut() {
    animate()
        .alpha(0F)
        .setDuration(FADE_OUT_DURATION)
        .setListener(null)
}