package store.pengu.server.application

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.util.date.*
import store.pengu.server.BadRequestException
import store.pengu.server.ForbiddenException
import java.time.DayOfWeek
import java.time.Instant
import java.time.Period
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalUnit
import java.util.*

object JWTAuthenticationConfig {

    private const val secret = "zAP5MBA4B4Ijz0MZaS48"
    const val issuer = "pengu.store"
    private const val validityInMs = 3_600_000 * 24 * 2 // 2 dias
    //private const val validityInMs = 10_000 * 1 // 10 seconds
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun makeToken(userId: Long): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(JWTClaims.ID, userId)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

    fun validate(jwtCredential: JWTCredential): Principal {
        if (jwtCredential.payload.getClaim(JWTClaims.ID).isNull) {
            throw BadRequestException("Invalid token")
        }

        return LoggedUser(jwtCredential.payload.getClaim(JWTClaims.ID).asLong())
    }
}

val ApplicationCall.user: LoggedUser
    get() = authentication.principal() ?: throw ForbiddenException("You need to be logged in")

object JWTClaims {
    const val ID = "id"
}

data class LoggedUser(
    val id: Long,
    val token: String = JWTAuthenticationConfig.makeToken(id)
) : Principal
