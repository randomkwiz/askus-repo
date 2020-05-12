package es.iesnervion.avazquez.askus.utils

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class UtilClass {
    companion object {
        fun hasNetwork(): Boolean {
            var success = false
            try {
                val url = URL("https://google.com")
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 10000
                connection.connect()
                success = connection.responseCode == 200
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return success
        }

        fun getFormattedCurrentDatetime(): String {
            val date = Calendar.getInstance().time
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
            return formatter.format(date)
        }
    }
}