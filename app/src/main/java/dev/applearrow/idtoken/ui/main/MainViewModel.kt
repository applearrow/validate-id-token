package dev.applearrow.idtoken.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.InvalidClaimException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import dev.applearrow.idtoken.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.security.interfaces.RSAPublicKey


/**
 * https://id-shadow.sage.com/.well-known/openid-configuration
 * https://id-shadow.sage.com/.well-known/jwks.json
 */
class MainViewModel(val app: Application) : AndroidViewModel(app) {

    companion object {
        const val TAG = "MainVm"
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
    val intMsg = MutableLiveData(R.string.app_name)
    val intError = MutableLiveData(0)
    val strError = MutableLiveData<String>()

    val decodedTokenStr = MutableLiveData<String>()

    private val jwkProvider = UrlJwkProvider(
        URL("https://id-shadow.sage.com/.well-known/jwks.json")
    )
    private val token = sharedPreferences.getString(
        app.getString(R.string.id_token_pref),
        app.getString(R.string.default_id_token)
    )
    private lateinit var jwt: DecodedJWT

    fun hideError() {
        intError.value = 0
        strError.value = ""
    }

    init {
        intMsg.value = R.string.app_name
        decode()
    }

    fun decode() {
        jwt = JWT.decode(token)

        val sp = "    "
        Log.d(TAG, "header=${jwt.header.toString()}")
        Log.d(TAG, "payload=${jwt.payload}")
        Log.d(TAG, "signature=${jwt.signature}")

        val sb = StringBuilder()
        sb.appendLine("header:")
        sb.appendLine("${sp}algorithm: ${jwt.algorithm}")
        jwt.type?.let { sb.appendLine("${sp}type: $it") }
        jwt.contentType?.let { sb.appendLine("${sp}contentType: $it") }
        sb.appendLine("${sp}keyId: ${jwt.keyId}")

        sb.appendLine()
        sb.appendLine("payload:")
        sb.appendLine("${sp}issuer:    ${jwt.issuer}")
        sb.appendLine("${sp}subject:   ${jwt.subject}")
        sb.appendLine("${sp}audience:  ${jwt.audience}")
        sb.appendLine("${sp}issuedAt:  ${jwt.issuedAt}")
        sb.appendLine("${sp}expiresAt: ${jwt.expiresAt}")
        jwt.notBefore?.let { sb.appendLine("${sp}notBefore: $it") }

        jwt.id?.let { sb.appendLine("${sp}id: $it") }

        jwt.claims.keys.forEach { key ->
            val value = jwt.getClaim(key).asString()
            value?.let {
                sb.appendLine("${sp}$key: $value")
            }
        }

        decodedTokenStr.value = sb.toString()
        Log.d(TAG, "${decodedTokenStr.value}")
    }

    fun validateToken() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                internalValidateToken()
            }
        }
    }

    suspend fun internalValidateToken() {
        val jwk = jwkProvider.get(jwt.keyId)
        val publicKey = jwk.publicKey as? RSAPublicKey ?: throw Exception("Invalid public key")
        val algorithm = when (jwk.algorithm) {
            "RS256" -> Algorithm.RSA256(publicKey, null)
            else -> throw Exception("Unsupported Algorithm")
        }
        val verifier = JWT.require(algorithm) // signature
            .withIssuer("https://id-shadow.sage.com/") // iss
            .withAudience("pzKeGoe0xS6r07MGgOG01n78n7e94LGK") // aud
            .build()

        try {
            val decodedJwt = verifier.verify(token)
        } catch (e: InvalidClaimException) {
            strError.postValue(e.message)
        } catch (e: TokenExpiredException) {
            strError.postValue(e.message)
        }

    }
}

