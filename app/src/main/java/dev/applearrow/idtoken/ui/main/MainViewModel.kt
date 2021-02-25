package dev.applearrow.idtoken.ui.main

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.auth0.jwk.JwkException
import com.auth0.jwk.UrlJwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import dev.applearrow.idtoken.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import java.security.interfaces.RSAPublicKey


/**
 * https://id-shadow.sage.com/.well-known/openid-configuration
 * https://id-shadow.sage.com/.well-known/jwks.json
 */
class MainViewModel(val app: Application) : AndroidViewModel(app) {

    companion object {
        const val TAG = "MainVm"

        val arrays = listOf(
            "https://sage.com/sci/identity_ids",
            "https://sage.com/sci/profiles"
        )
    }

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)
    val intMsg = MutableLiveData(0)
    val strMsg = MutableLiveData<String>()
    val intError = MutableLiveData(0)
    val strError = MutableLiveData<String>()

    val isValidVisible = MutableLiveData<Boolean>(false)
    val intValidIcon = MutableLiveData(0)

    val encodedTokenStr = MutableLiveData<String>()
    val decodedTokenStr = MutableLiveData<String>()

    private lateinit var token: String
    private lateinit var issuer: String
    private var validateAudience = false
    private lateinit var audience: String
    private lateinit var jwt: DecodedJWT
    private lateinit var jwkProvider: UrlJwkProvider

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->
            readConfig()
            decode()
        }

    fun hideError() {
        intError.value = 0
        strError.value = ""
    }

    init {
        readConfig()
        decode()

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun readConfig() {
        token = sharedPreferences.getString(
            app.getString(R.string.jwt_token_pref),
            app.getString(R.string.default_token)
        )!!

        issuer = sharedPreferences.getString(
            app.getString(R.string.issuer_pref),
            app.getString(R.string.default_issuer)
        )!!

        validateAudience = sharedPreferences.getBoolean(
            app.getString(R.string.validate_audience_pref),
            false
        )

        audience = sharedPreferences.getString(
            app.getString(R.string.audience_pref),
            app.getString(R.string.default_audience)
        )!!

        jwkProvider = UrlJwkProvider(
            URL("$issuer.well-known/jwks.json")
        )
    }

    private fun decode() {
        try {
            encodedTokenStr.value = token
            jwt = JWT.decode(token)

            Log.d(TAG, "header=${jwt.header.toString()}")
            Log.d(TAG, "payload=${jwt.payload}")
            Log.d(TAG, "signature=${jwt.signature}")

            val len = 15
            val sb = StringBuilder()
            sb.appendLine("Header")
            sb.appendLine("algorithm:".padStart(len) + jwt.algorithm)
            jwt.type?.let { sb.appendLine("type:".padStart(len) + it) }
            jwt.contentType?.let { sb.appendLine("contentType:".padStart(len) + it) }
            sb.appendLine("keyId:".padStart(len) + jwt.keyId)

            sb.appendLine()
            sb.appendLine("Payload")
            sb.appendLine("issuer:".padStart(len) + jwt.issuer)
            sb.appendLine("subject:".padStart(len) + jwt.subject)
            sb.appendLine("audience:".padStart(len) + jwt.audience)
            sb.appendLine("issuedAt:".padStart(len) + jwt.issuedAt)
            sb.appendLine("expiresAt:".padStart(len) + jwt.expiresAt)
            jwt.notBefore?.let { sb.appendLine("notBefore:".padStart(len) + it) }

            jwt.id?.let { sb.appendLine("id:".padStart(len) + it) }

            jwt.claims.keys.forEach { key ->
                Log.d(TAG, "$key")
                val value = jwt.getClaim(key)
                if (key in arrays) {
                    val ids = value.asList(Object::class.java)
                    ids?.let {
                        sb.appendLine("$key:".padStart(len) + JSONArray(ids).toString(2))
                    }
                } else {
                    val vStr = value.asString()
                    vStr?.let {
                        sb.appendLine("$key:".padStart(len) + vStr)
                    }
                }
            }

            decodedTokenStr.value = sb.toString()
            Log.d(TAG, "${decodedTokenStr.value}")
        } catch (e: JWTDecodeException) {
            Log.e(TAG, "${e.message}")
            postError(e.message)
        }
    }

    fun validateToken() {
        isValidVisible.value = false
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                internalValidateToken()
            }
        }
    }

    @WorkerThread
    private fun postError(message: String?) {
        intValidIcon.postValue(R.drawable.thumb_down)
        strError.postValue(message)
        isValidVisible.postValue(true)
    }

    private suspend fun internalValidateToken() {

        try {
            val jwk = jwkProvider.get(jwt.keyId)
            val publicKey = jwk.publicKey as? RSAPublicKey ?: throw Exception("Invalid public key")
            val algorithm = when (jwk.algorithm) {
                "RS256" -> Algorithm.RSA256(publicKey, null)
                else -> throw Exception("Unsupported Algorithm")
            }

            val verifier = JWT.require(algorithm) // signature
                .withIssuer(issuer) // issuer
                .apply { if (validateAudience) withAudience(audience) }  // audience
                .build()

            verifier.verify(token)
            intValidIcon.postValue(R.drawable.thumb_up)
            isValidVisible.postValue(true)

        } catch (e: JwkException) {
            postError(e.message)
        } catch (e: JWTVerificationException) {
            postError(e.message)
        }
    }
}

