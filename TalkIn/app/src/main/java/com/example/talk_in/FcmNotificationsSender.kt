package com.example.talk_in

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class FcmNotificationsSender(
    private val userFcmToken: String,
    private val title: String,
    private val body: String,
    private val senderUid: String,
    private val senderName: String,
    private val mContext: Context,
    private val mActivity: Activity
) {
    private val requestQueue: RequestQueue = Volley.newRequestQueue(mContext)
    private val postUrl = "https://fcm.googleapis.com/fcm/send"
    private val fcmServerKey = "AAAAt0loRzU:APA91bE7oxyox_4Q2dHihHCKlQCe9pV7wbGgPXL4uvRsC-1kKfV-H0v37M4prqmV784RdKK7nW8c7POGh3Psyb-gAaqq78wya6yoEdimE0XLARHPtvi1VHdG5VrwHx4n9m6YRqZvY7-s"

    fun sendNotifications() {
        Log.d(TAG, "Sending FCM notification")
        val mainObj = JSONObject()
        try {
            mainObj.put("to", userFcmToken)
            val notiObject = JSONObject()
            notiObject.put("title", title)
            notiObject.put("body", body)
            notiObject.put("icon", R.drawable.talkin)
            mainObj.put("notification", notiObject)

            // Add data payload
            val dataObject = JSONObject()
            dataObject.put("senderUid", senderUid)
            dataObject.put("senderName", senderName)
            dataObject.put("message", body)
            mainObj.put("data", dataObject)

            val request = object : JsonObjectRequest(
                Request.Method.POST, postUrl, mainObj,
                Response.Listener { response ->

                },
                Response.ErrorListener { error ->

                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val header = HashMap<String, String>()
                    header["content-type"] = "application/json"
                    header["authorization"] = "key=$fcmServerKey"
                    return header
                }
            }
            requestQueue.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TAG = "FcmNotificationsSender"
    }
}
