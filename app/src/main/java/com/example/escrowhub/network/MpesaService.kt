package com.example.escrowhub.network

import com.example.escrowhub.model.AuthResponse
import com.example.escrowhub.model.STKPushRequest
import com.example.escrowhub.model.STKPushResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MpesaService {
    @GET("oauth/v1/generate?grant_type=client_credentials")
    suspend fun getAccessToken(
        @Header("Authorization") auth: String
    ): Response<AuthResponse>

    @POST("mpesa/stkpush/v1/processrequest")
    suspend fun sendSTKPush(
        @Header("Authorization") auth: String,
        @Body request: STKPushRequest
    ): Response<STKPushResponse>
}
