package com.example

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

@JsonClass(generateAdapter = true)
data class KodiRpcRequest(
    @param:Json(name = "jsonrpc") val jsonrpc: String = "2.0",
    @param:Json(name = "method") val method: String,
    @param:Json(name = "params") val params: Any? = null,
    @param:Json(name = "id") val id: Int = 1
)

@JsonClass(generateAdapter = true)
data class KodiRpcResponse(
    @param:Json(name = "id") val id: Int?,
    @param:Json(name = "jsonrpc") val jsonrpc: String?,
    @param:Json(name = "result") val result: Any? = null,
    @param:Json(name = "error") val error: KodiRpcError? = null
)

@JsonClass(generateAdapter = true)
data class KodiRpcError(
    @param:Json(name = "code") val code: Int,
    @param:Json(name = "message") val message: String
)

interface KodiApiService {
    @POST("jsonrpc")
    suspend fun executeCommand(@Body request: KodiRpcRequest): retrofit2.Response<KodiRpcResponse>
}

object KodiClient {
    fun create(baseUrl: String, username: String? = null, password: String? = null): KodiApiService {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            
        val builder = OkHttpClient.Builder()
        
        if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
            builder.addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("Authorization", Credentials.basic(username, password))
                    .build()
                chain.proceed(request)
            }
        }
        
        val okHttpClient = builder.build()
        
        return Retrofit.Builder()
            .baseUrl(if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(KodiApiService::class.java)
    }
}

