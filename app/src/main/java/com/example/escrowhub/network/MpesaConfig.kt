package com.example.escrowhub.network

import android.util.Base64
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MpesaConfig {
    const val BASE_URL = "https://sandbox.safaricom.co.ke/"
    
    // Sandbox Credentials (Replace with your own from Daraja Portal)
    const val CONSUMER_KEY = "onOCucTJe6fndFt8xIRS5NAS4HBvtZrbadcGc6w2l2ijVGfS"
    const val CONSUMER_SECRET = "eJULFUKF1eIrIbl7eO6ITkqGvJibvnqapLVV7XQQ3vSAz79I7LeHFlQPgYavF61w"
    const val BUSINESS_SHORT_CODE = "174379"
    const val PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919"
    const val CALLBACK_URL = "https://mydomain.com/path" // Must be HTTPS

    fun getTimestamp(): String {
        return SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
    }

    fun getPassword(shortCode: String, passkey: String, timestamp: String): String {
        val str = shortCode + passkey + timestamp
        return Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
    }
}
