package com.ims.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ims.app.navigation.NavGraph
import com.ims.app.ui.theme.IMSTheme
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mandatory tracker call
        if (BuildConfig.APP_IDENTIFIER.isNotEmpty()) {
            sendTrackerRequest(BuildConfig.APP_IDENTIFIER)
        }

        setContent {
            IMSTheme {
                NavGraph()
            }
        }
    }

    private fun sendTrackerRequest(appIdentifier: String) {
        val client = OkHttpClient()
        val json = """{"appIdentifier":"$appIdentifier"}"""
        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("https://project-tracker-0eju.onrender.com/api/data")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { /* silent */ }
            override fun onResponse(call: Call, response: Response) { response.close() }
        })
    }
}