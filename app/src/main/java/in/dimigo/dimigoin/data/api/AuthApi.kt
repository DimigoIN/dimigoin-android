package `in`.dimigo.dimigoin.data.api

import `in`.dimigo.dimigoin.data.model.AuthModel
import `in`.dimigo.dimigoin.data.model.LoginRequestModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth")
    fun login(@Body loginRequestModel: LoginRequestModel): Call<AuthModel>

    @POST("/auth/refresh")
    fun refreshToken(@Header("Authorization") refreshToken: String): Call<AuthModel>
}