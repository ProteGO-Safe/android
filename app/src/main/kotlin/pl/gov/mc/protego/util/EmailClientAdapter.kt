package pl.gov.mc.protego.util

import android.content.Intent
import android.content.res.Resources
import pl.gov.mc.protego.R

class EmailClientAdapter(
    private val resources: Resources
) {

    companion object {
        private const val MIME_TYPE = "message/rfc822"
    }

    fun createIntent(email: String): Intent
        = Intent(Intent.ACTION_SEND).apply {
            type = MIME_TYPE
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(email)
            )
        }.let {
            Intent.createChooser(it, resources.getString(R.string.send_email_window_title))
        }
}