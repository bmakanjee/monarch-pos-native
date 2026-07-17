package com.monarch.pos.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.monarch.pos.model.ServiceCatalog

object JobberClient {
    private val client = OkHttpClient()
    
    // This will be loaded from secure storage or environment variables in the real implementation
    private const val JOBBER_API_TOKEN = "YOUR_JOBBER_API_TOKEN_HERE" 
    private const val JOBBER_API_URL = "https://api.jobber.com/graphql"
    
    suspend fun healthCheck(): Boolean = withContext(Dispatchers.IO) {
        try {
            val query = """
                query {
                    account {
                        id
                        name
                    }
                }
            """.trimIndent()
            
            val request = createGraphQLRequest(query)
            val response = client.newCall(request).execute()
            response.isSuccessful && 
                response.body?.string()?.contains("\"account\"") == true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun validateService(serviceId: String, vehicleType: String): Boolean = withContext(Dispatchers.IO) {
        // This would normally query Jobber's services API to verify service and pricing
        try {
            val service = ServiceCatalog.services.find { it.id == serviceId } ?: return@withContext false
            
            // Check if the vehicle type is valid for this service (based on catalog)
            when(vehicleType.lowercase()) {
                "sedan", "hatch", "coupe" -> true
                "suv", "ute" -> true  
                else -> false
            }
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun createJob(
        serviceId: String,
        vehicleType: String, // "sedan", "suv"
        clientName: String,
        clientPhone: String,
        scheduledDate: String, // ISO format like 2026-07-18
        scheduledTime: String,  // HH:mm format like 14:30
        priceCents: Int? = null // If not provided, use catalog pricing
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val service = ServiceCatalog.services.find { it.id == serviceId }
            
            // Validate the service exists and vehicle type is valid
            if (service == null || !validateService(serviceId, vehicleType)) return@withContext false
            
            // If no price provided, use the catalog price based on vehicle type
            val actualPrice = priceCents ?: when(vehicleType.lowercase()) {
                "sedan", "hatch", "coupe" -> service.sedanPrice
                "suv", "ute" -> service.suvPrice
                else -> return@withContext false
            }
            
            // Create a job with the validated information
            val mutation = """
                mutation CreateJob(${'$'}input: JobCreateInput!) {
                    jobCreate(input: ${'$'}input) {
                        job {
                            id
                            status
                        }
                    }
                }
            """.trimIndent()
            
            val variables = JSONObject().apply {
                put("input", JSONObject().apply {
                    put("clientName", clientName)
                    put("clientPhone", clientPhone)
                    put("scheduledDate", scheduledDate)
                    put("scheduledTime", scheduledTime)
                    put("description", "Service: ${service.name} ($vehicleType)")
                    
                    // Add the service details in a way Jobber can recognize them
                    if (priceCents != null) {
                        put("lineItems", listOf(
                            JSONObject().apply {
                                put("description", "${service.name} - $vehicleType")
                                put("unitCost", priceCents)
                            }
                        ))
                    }
                })
            }
            
            val request = createGraphQLRequest(mutation, variables.toString())
            val response = client.newCall(request).execute()
            response.isSuccessful 
        } catch (e: Exception) {
            false
        }
    }

    private fun createGraphQLRequest(query: String, variables: String = "{}"): Request {
        return Request.Builder()
            .url(JOBBER_API_URL)
            .post(
                JSONObject().apply {
                    put("query", query)
                    put("variables", JSONObject(variables))
                }.toString().toRequestBody("application/json".toMediaType())
            )
            .addHeader("Authorization", "Bearer $JOBBER_API_TOKEN")
            .addHeader("X-JOBBER-GRAPHQL-VERSION", "2026-05-12")
            .build()
    }
}