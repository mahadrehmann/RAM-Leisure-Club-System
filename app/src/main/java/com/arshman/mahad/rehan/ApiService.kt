// ApiService.kt
package com.arshman.mahad.rehan

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

data class UploadResponse(val status: String, val image_url: String?, val message: String?)
data class GetResponse(val status: String, val image_url: String?, val message: String?)

interface ApiService {
    @Multipart
    @POST("upload_profile.php")
    suspend fun uploadProfileImage(
        @Part("user_id") userId: RequestBody,
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("get_profile.php")
    suspend fun getProfile(@Query("user_id") userId: String): Response<GetResponse>
}
