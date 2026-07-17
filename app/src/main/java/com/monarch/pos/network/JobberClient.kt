package com.monarch.pos.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import com.monarch.pos.model.ServiceCatalog
import java.io.File

object JobberClient {
    private val client = OkHttpClient()
    
    // This will be loaded from secure storage or environment variables in the real implementation
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
                response.body?.string()?.contains("\\\"account\\\"") == true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun validateService(serviceId: String, vehicleType: String): Boolean = withContext(Dispatchers.IO) {
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
        vehicleType: String,
        amountCents: Int,
        customerName: String = "Mobile Detailing Customer",
        customerPhone: String = "",
        customerEmail: String = ""
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // First validate the service 
            if (!validateService(serviceId, vehicleType)) return@withContext false
            
            val service = ServiceCatalog.services.find { it.id == serviceId } ?: return@withContext false
            
            // Construct the mutation for creating a job
            val mutation = """
                mutation {
                    createJob(input: {
                        customer: {
                            name: "$customerName"
                            phone: "$customerPhone"  
                            email: "$customerEmail"
                        }
                        description: "Monarch Detailing - ${service.name} ($vehicleType)"
                        serviceId: "${getServiceIdFromService(service)}"
                        status: SCHEDULED
                    }) {
                        job {
                            id
                            number
                        }
                    }
                }
            """.trimIndent()

            val request = createGraphQLRequest(mutation)
            val response = client.newCall(request).execute()
            
            // Check if the API call was successful and returned a valid result 
            response.isSuccessful && 
                response.body?.string()?.contains("\\\"createJob\\\"") == true
        } catch (e: Exception) {
            false
        }
    }

    private fun createGraphQLRequest(query: String): Request {
        val tokenFile = File("/Users/braj/Friday/credentials/jobber-tokens.json")
        val token = if (tokenFile.exists()) {
            tokenFile.readText().substringAfter("\"access_token\": \"").substringBefore("\",")
        } else {
            // Fallback for testing - should be removed in production
            "dummy-token"
        }
        
        return Request.Builder()
            .url(JOBBER_API_URL)
            .post(
                JSONObject()
                    .put("query", query)
                    .toString()
                    .toRequestBody("application/json".toMediaType())
            )
            .addHeader("Authorization", "Bearer $token")
            .addHeader("X-JOBBER-GRAPHQL-VERSION", "2026-05-12") 
            .build()
    }

    private fun getServiceIdFromService(service: com.monarch.pos.model.Service): String {
        // In a real implementation this would map to the actual Jobber service IDs
        return service.id  // Placeholder - in production, map these properly
    }
}