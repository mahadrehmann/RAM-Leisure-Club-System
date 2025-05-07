package com.arshman.mahad.rehan

import android.content.Context
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class NotificationService(private val context: Context) {

    companion object {
        private const val ONESIGNAL_APP_ID = "e31abef6-4c88-4338-8d3e-d4fb59f34de1"
        private const val REST_API_KEY = "os_v2_app_4mnl55smrbbtrdj62t5vt42n4fz4r56zetxu7p5fyegdstql7kxw3bfinucy2dt4xcijfnu6foguozi22mv2jis57hu5q46ttp5lb6y"
        private const val API_URL = "https://onesignal.com/api/v1/notifications"
        private val JSON = "application/json; charset=utf-8".toMediaType()
        private const val TAG = "NotificationService"
    }

    private val client = OkHttpClient()

    fun sendBookingNotification(
        playerId: String,
        heading: String,
        message: String,
        data: Map<String, Any>? = null
    ) {
        val payload = JSONObject().apply {
            put("app_id", ONESIGNAL_APP_ID)
            put("include_player_ids", JSONArray().put(playerId))
            put("headings", JSONObject().put("en", heading))
            put("contents", JSONObject().put("en", message))
            put("priority", 10)
            
            if (data != null) {
                val dataJson = JSONObject()
                for ((key, value) in data) {
                    dataJson.put(key, value)
                }
                put("data", dataJson)
            }
        }

        val body = payload.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Authorization", "Basic $REST_API_KEY")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Failed to send push notification", e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val responseBody = it.body?.string()
                    if (it.isSuccessful) {
                        Log.i(TAG, "Push notification sent successfully: $responseBody")
                    } else {
                        Log.e(TAG, "Error sending push notification. Code: ${it.code}, Response: $responseBody")
                    }
                }
            }
        })
    }
}