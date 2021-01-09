package `in`.dimigo.dimigoin.data.util

import android.util.Base64
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class AccessToken private constructor(private val jwt: String) {
    private val tokenJson by lazy {
        decodeJWT(jwt)
    }

    private val expirationTime by lazy {
        tokenJson.get("exp").asLong
    }

    fun isTokenExpired() = System.currentTimeMillis() / 1000 >= expirationTime

    private fun decodeJWT(jwt: String): JsonObject {
        val jwtSplits = jwt.split(".")
        return getJson(jwtSplits[1])
    }

    private fun getJson(encodedString: String): JsonObject {
        val decodedString = String(Base64.decode(encodedString, Base64.URL_SAFE))
        return JsonParser().parse(decodedString).asJsonObject
    }

    companion object {
        fun fromJwt(jwt: String) = AccessToken(jwt)
    }
}
