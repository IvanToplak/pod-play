package hr.from.ivantoplak.podplay.ui.common

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MessageProviderImpl @Inject constructor(@ApplicationContext private val context: Context) : MessageProvider {

    override fun shortPopup(message: String) {
        showMessage(message, Toast.LENGTH_SHORT)
    }

    override fun longPopup(message: String) {
        showMessage(message, Toast.LENGTH_LONG)
    }

    private fun showMessage(message: String, duration: Int) {
        if (duration != Toast.LENGTH_SHORT && duration != Toast.LENGTH_LONG) return
        Toast.makeText(context, message, duration).show()
    }
}