package com.monarch.pos.network

import com.monarch.pos.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

data class PaymentIntentResponse(val clientSecret: String)

object BackendClient {
    private val client = OkHttpClient()
    private val baseUrl = BuildConfig.BACKEND_URL

    suspend fun createPaymentIntent(
        amountCents: Int,
        currency: String = "aud",
        label: String = "Monarch Detailing"
    ): PaymentIntentResponse = withContext(Dispatchers.IO) {
        val json = JSONObject().apply {
            put("amount", amountCents)
            put("currency", currency)
            put("description", label)
        }
        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$baseUrl/create-payment-intent")
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Backend error: ${response.code}")
            val respJson = JSONObject(response.body!!.string())
            PaymentIntentResponse(clientSecret = respJson.getString("clientSecret"))
        }
    }
}
