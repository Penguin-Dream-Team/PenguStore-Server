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
    private const val validityInMs = 36_000_00 * 1 // 1 hours
    private const val refreshTokenValidityInDays = 30
    private val algorithm = Algorithm.HMAC512(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun makeToken(userId: Int): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(JWTClaims.ID, userId)
        .withClaim(JWTClaims.REFRESH, false)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    fun makeRefreshToken(userId: Int): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(JWTClaims.ID, userId)
        .withClaim(JWTClaims.REFRESH, true)
        .withExpiresAt(getRefreshTokenExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
    private fun getRefreshTokenExpiration() = Date.from(
        Instant.now().plus(
            Period.ofDays(
                refreshTokenValidityInDays
            )
        )
    )

    fun validate(jwtCredential: JWTCredential): Principal {
        if (jwtCredential.payload.getClaim(JWTClaims.REFRESH).asBoolean() == true) {
            throw BadRequestException("Invalid token")
        }

        return LoggedUser(jwtCredential.payload.getClaim(JWTClaims.ID).asInt())
    }

    fun validateRefreshToken(jwtCredential: JWTCredential): Principal {
        if (jwtCredential.payload.getClaim(JWTClaims.REFRESH).asBoolean() == false) {
            throw BadRequestException("Invalid refresh token")
        }

        return RefreshToken(jwtCredential.payload.getClaim(JWTClaims.ID).asInt())
    }
}

val ApplicationCall.user: LoggedUser
    get() = authentication.principal() ?: throw ForbiddenException("You need to be logged in")

val ApplicationCall.refresh: RefreshToken
    get() = authentication.principal() ?: throw ForbiddenException("You need to be logged in")

object JWTClaims {
    const val ID = "id"
    const val REFRESH = "refresh"
}

data class RefreshToken(
    val id: Int,
    val token: String = JWTAuthenticationConfig.makeToken(id),
    val refreshToken: String = JWTAuthenticationConfig.makeRefreshToken(id)
) : Principal

data class LoggedUser(
    val id: Int,
    val token: String = JWTAuthenticationConfig.makeToken(id)
) : Principal
