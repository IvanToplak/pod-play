package hr.from.ivantoplak.podplay.extensions

import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.text.format.DateUtils
import java.net.URLEncoder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

private val REGEX_ESCAPE_CHARS = "\n".toRegex()
private val REGEX_HTML_TAGS = "(<(/)img>)|(<img.+?>)".toRegex()
private const val DURATION_UNKNOWN = "--:--"

/**
 * Helper method to check if a [String] contains another in a case insensitive way.
 */
fun String?.containsCaseInsensitive(other: String?) =
    if (this == null && other == null) {
        true
    } else if (this != null && other != null) {
        toLowerCase().contains(other.toLowerCase())
    } else {
        false
    }

/**
 * Helper extension to URL encode a [String]. Returns an empty string when called on null.
 */
inline val String?.urlEncoded: String
    get() = if (Charset.isSupported("UTF-8")) {
        URLEncoder.encode(this ?: "", "UTF-8")
    } else {
        // If UTF-8 is not supported, use the default charset.
        @Suppress("deprecation")
        URLEncoder.encode(this ?: "")
    }

/**
 * Helper extension to convert a potentially null [String] to a [Uri] falling back to [Uri.EMPTY]
 */
fun String?.toUri(): Uri = this?.let { Uri.parse(it) } ?: Uri.EMPTY

fun String.htmlToSpannable(): Spanned {

    var newHtmlDesc = this.replace(REGEX_ESCAPE_CHARS, "")
    newHtmlDesc = newHtmlDesc.replace(REGEX_HTML_TAGS, "")

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(newHtmlDesc, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION") Html.fromHtml(newHtmlDesc)
    }
}

/**
 * Transforms date string form one format to another.
 *
 * @param inputPattern input string format pattern
 * @param outputPattern result format pattern
 * @return Empty string if string is null, empty, whitespace or exception is thrown, otherwise formatted string.
 */
fun String?.transformDate(inputPattern: String, outputPattern: String): String {
    if (this.isNullOrBlank()) return ""
    val inFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
    return try {
        inFormat.parse(this)?.toString(outputPattern) ?: ""
    } catch (ex: Exception) {
        ""
    }
}

/**
 * Transforms date string to Date object.
 *
 * @param inputPattern input string format pattern
 * @return null if string is null, empty, whitespace or exception is thrown, otherwise Date from string.
 */
fun String?.transformDate(inputPattern: String): Date? {
    if (this.isNullOrBlank()) return null
    val inFormat = SimpleDateFormat(inputPattern, Locale.getDefault())
    return try {
        inFormat.parse(this) ?: null
    } catch (ex: Exception) {
        null
    }
}

/**
 * Transforms string which contains duration in seconds to "MM:SS" or "H:MM:SS".
 *
 * @return String formatted as "MM:SS" or "H:MM:SS", or "--:--" if unknown.
 */
fun String?.toHourMinSec(): String = when {
    this.isNullOrBlank() -> DURATION_UNKNOWN
    this.toIntOrNull() != null -> DateUtils.formatElapsedTime(this.toLong())
    this.contains(":") -> this
    else -> DURATION_UNKNOWN
}
