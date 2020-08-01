package hr.from.ivantoplak.podplay.extensions

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE

fun View.show(show: Boolean = true) {
    if (visibility == VISIBLE && show) return
    if (visibility == GONE && !show) return
    visibility = if (show) VISIBLE else GONE
}

fun View.hide() {
    if (visibility == GONE) return
    visibility = GONE
}