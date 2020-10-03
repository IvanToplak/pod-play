package hr.from.ivantoplak.podplay.extensions

import androidx.fragment.app.Fragment
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import hr.from.ivantoplak.podplay.R

fun Fragment.slideRightTransition(): Transition =
    TransitionInflater.from(context).inflateTransition(R.transition.slide_right)