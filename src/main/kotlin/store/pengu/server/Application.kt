package store.pengu.server

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import store.pengu.server.utils.CommaSeparatedList
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.http.content.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import io.ktor.websocket.*
import org.flywaydb.core.Flyway
import org.jooq.exception.DataAccessException
import org.koin.core.context.startKoin
import org.slf4j.event.Level
import store.pengu.server.application.*
import store.pengu.server.application.features.GuestRoutes
import store.pengu.server.application.features.ResourceAccessControl
import store.pengu.server.application.features.guestOnly
import java.io.File
import java.time.Duration
import java.time.Instant

fun main(args: Array<String>): Unit =
        io.ktor.server.netty.EngineMain.main(args)

/**
 * Please note that you can use any other name instead of *module*.
 * Also note that you can have more then one modules in your application.
 * */
@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val koin = startKoin {
        modules(ApiModule.module)
    }.koin

    val flyway = koin.get<Flyway>()
    flyway.migrate()


    install(AutoHeadResponse)
    install(CachingHeaders) {
        options { outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS -> CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))
                else -> null
            }
        }
    }
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }
    install(Compression) {
        gzip {
            priority = 1.0
        }
        deflate {
            priority = 10.0
            minimumSize(1024) // condition
        }
    }
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }
    install(ForwardedHeaderSupport)
    install(XForwardedHeaderSupport)
    install(Locations)
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            registerModule(JavaTimeModule())
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
    }

    install(DataConversion) {
        convert<CommaSeparatedList> {
            decode { values, _ ->
                CommaSeparatedList(values.flatMap { it.split(",") })
            }

            encode { value ->
                when (value) {
                    null -> listOf()
                    is CommaSeparatedList -> listOf(value.toString())
                    else -> throw DataConversionException("Cannot convert $value as CommaSeparatedList")
                }
            }
        }
        convert<Instant> {
            decode { values, _ ->
                Instant.ofEpochSecond(values.first().toLong())
            }

            encode { value ->
                when (value) {
                    null -> listOf()
                    is Instant -> listOf(value.epochSecond.toString())
                    else -> throw DataConversionException("Cannot convert $value as Instant")
                }
            }
        }
    }

    install(StatusPages) {
        exception<TooManyRequestsException> { cause ->
            cause.retryAfter?.let {
                call.response.header("Retry-After", it)
            }
            call.respond(message = cause.content, status = cause.statusCode)
        }
        exception<PenguStoreException> { cause ->
            call.respond(message = cause.content, status = cause.statusCode)
        }
        exception<DataAccessException> { cause ->
            val sqlMsg = cause.message?.split(";")?.run {
                subList(1, size)
            }?.joinToString(";") { it.trim() } ?: "An internal server error occurred."
            call.respond(message = sqlMsg, status = HttpStatusCode.BadRequest)
        }
        exception<Exception> { cause ->
            cause.printStackTrace()
            call.respond(
                message = "Oops, something went wrong with your request.\nPlease try again later.",
                status = HttpStatusCode.InternalServerError
            )
        }

        status(HttpStatusCode.Unauthorized) { status ->
            call.respond(
                message = "You cannot access the requested resource",
                status = status
            )
        }

        status(HttpStatusCode.NotFound) { status ->
            call.respond(
                message = "The requested resource cannot be found",
                status = status
            )
        }
    }

    install(GuestRoutes)

    install(Authentication) {
        jwt {
            verifier(JWTAuthenticationConfig.verifier)
            realm = JWTAuthenticationConfig.issuer
            validate { JWTAuthenticationConfig.validate(it) }
            challenge { _, _ -> throw UnauthorizedException("You need to be logged in to access this resource") }
        }
    }

    routing {
        loadRoutes(koin)

        /* LANDING PAGE */
        authenticate {
            get<Home> {
                call.respond(call.user)
                //call.respondText("Welcome to the PenguStore API!!")
            }
        }

        /* STATIC FILES */
        static("/uploads") {
            files(File("uploads/"))
        }
        resource("/favicon.ico", "static/favicon.ico")
    }
}

val ApplicationCall.requestUrl: String
    get() = request.origin.run { "${scheme}://${host}:${port}" }

abstract class PenguStoreException(val statusCode: HttpStatusCode, open val content: Any) : RuntimeException("")
data class ForbiddenException(override val content: Any) : PenguStoreException(HttpStatusCode.Forbidden, content)
data class UnauthorizedException(override val content: Any) : PenguStoreException(HttpStatusCode.Unauthorized, content)
data class BadRequestException(override val content: Any) : PenguStoreException(HttpStatusCode.BadRequest, content)
data class NotFoundException(override val content: Any) : PenguStoreException(HttpStatusCode.NotFound, content)
data class ConflictException(override val content: Any) : PenguStoreException(HttpStatusCode.Conflict, content)
data class NotAcceptableException(override val content: Any) : PenguStoreException(HttpStatusCode.NotAcceptable, content)
data class InternalServerErrorException(override val content: Any) : PenguStoreException(HttpStatusCode.InternalServerError, content)
data class TooManyRequestsException(override val content: Any, val retryAfter: Instant? = null) : PenguStoreException(HttpStatusCode.TooManyRequests, content)