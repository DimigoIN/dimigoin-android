package `in`.dimigo.dimigoin.data.usecase.auth

import `in`.dimigo.dimigoin.data.api.DimigoinApi
import `in`.dimigo.dimigoin.data.model.AuthModel
import `in`.dimigo.dimigoin.data.model.LoginRequestModel
import kotlinx.coroutines.delay
import retrofit2.await

class AuthUseCaseImpl(private val api: DimigoinApi) : AuthUseCase {
    override suspend fun login(loginRequestModel: LoginRequestModel): AuthModel {
        return api.login(loginRequestModel).await()
    }

    override suspend fun refreshToken(refreshToken: String): AuthModel {
        return api.refreshToken("Bearer $refreshToken").await()
    }
}
