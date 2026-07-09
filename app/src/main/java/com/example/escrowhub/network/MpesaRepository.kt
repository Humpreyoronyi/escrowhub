package com.example.escrowhub.network

import android.util.Base64
import com.example.escrowhub.model.STKPushRequest
import com.example.escrowhub.model.STKPushResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MpesaRepository {
    private val mpesaService: MpesaService

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(MpesaConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        mpesaService = retrofit.create(MpesaService::class.java)
    }

    suspend fun initiateSTKPush(phoneNumber: String, amount: Double, accountRef: String): Result<STKPushResponse> {
        return try {
            // 1. Get Access Token
            val keys = "${MpesaConfig.CONSUMER_KEY}:${MpesaConfig.CONSUMER_SECRET}"
            val auth = "Basic " + Base64.encodeToString(keys.toByteArray(), Base64.NO_WRAP)
            
            val authResponse = mpesaService.getAccessToken(auth)
            if (!authResponse.isSuccessful) {
                return Result.failure(Exception("Failed to get access token"))
            }
            
            val accessToken = authResponse.body()?.access_token ?: return Result.failure(Exception("Token is null"))
            val bearerAuth = "Bearer $accessToken"
            
            // 2. Prepare STK Push
            val timestamp = MpesaConfig.getTimestamp()
            val password = MpesaConfig.getPassword(MpesaConfig.BUSINESS_SHORT_CODE, MpesaConfig.PASSKEY, timestamp)

            // Normalize phone number to 254... format
            val normalizedPhone = when {
                phoneNumber.startsWith("0") -> "254" + phoneNumber.substring(1)
                phoneNumber.startsWith("+") -> phoneNumber.substring(1)
                phoneNumber.startsWith("254") -> phoneNumber
                else -> phoneNumber
            }
            
            val request = STKPushRequest(
                BusinessShortCode = MpesaConfig.BUSINESS_SHORT_CODE,
                Password = password,
                Timestamp = timestamp,
                TransactionType = "CustomerPayBillOnline",
                Amount = amount.toInt(),
                PartyA = normalizedPhone, // User's phone
                PartyB = MpesaConfig.BUSINESS_SHORT_CODE,
                PhoneNumber = normalizedPhone,
                CallBackURL = MpesaConfig.CALLBACK_URL,
                AccountReference = accountRef,
                TransactionDesc = "Escrow Payment"
            )
            
            val response = mpesaService.sendSTKPush(bearerAuth, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "STK Push failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
