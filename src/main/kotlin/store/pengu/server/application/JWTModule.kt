package store.pengu.server.application

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import java.util.*

object JWTAuthenticationConfig {

    private const val secret = "zAP5MBA4B4Ijz0MZaS48"
    const val issuer = "pengu.store"
    private const val validityInMs = 36_000_00 * 1 // 1 hours
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun makeToken(userId: Int): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(JWTClaims.ID, userId)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

    fun validate(jwtCredential: JWTCredential): Principal {
        return LoggedUser(jwtCredential.payload.getClaim(JWTClaims.ID).asInt())
    }
}

val ApplicationCall.user get() = authentication.principal<LoggedUser>()

object JWTClaims {
    const val ID = "id"
}

data class LoggedUser(
    val id: Int,
    val token: String = JWTAuthenticationConfig.makeToken(id)
) : Principal
