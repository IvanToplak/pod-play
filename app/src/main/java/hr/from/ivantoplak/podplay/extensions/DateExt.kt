package hr.from.ivantoplak.podplay.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.toString(pattern: String): String =
    SimpleDateFormat(pattern, Locale.getDefault()).format(this)