package store.pengu.server

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import store.pengu.server.application.loadRoutes
import store.pengu.server.utils.CommaSeparatedList
import io.ktor.application.*
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
import io.ktor.websocket.*
import org.koin.core.context.startKoin
import org.slf4j.event.Level
import store.pengu.server.application.features.ResourceAccessControl
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
        exception<ForbiddenException> { cause ->
            call.respond(message = cause.content, status = HttpStatusCode.Forbidden)
        }
        exception<UnauthorizedException> { cause ->
            call.respond(message = cause.content, status = HttpStatusCode.Unauthorized)
        }
        exception<BadRequestException> { cause ->
            call.respond(message = cause.content, status = HttpStatusCode.BadRequest)
        }
        exception<NotFoundException> { cause ->
            call.respond(message = cause.content, status = HttpStatusCode.NotFound)
        }
        exception<ConflictException> { cause ->
            call.respond(message = cause.content, status = HttpStatusCode.Conflict)
        }
        exception<NotAcceptableException> { cause ->
            call.respond(message = cause.content, status = HttpStatusCode.NotAcceptable)
        }
        exception<InternalServerErrorException> { cause ->
            call.respond(message = cause.content, status = HttpStatusCode.InternalServerError)
        }
        exception<TooManyRequestsException> { cause ->
            cause.retryAfter?.let {
                call.response.header("Retry-After", it)
            }
            call.respond(message = cause.content, status = HttpStatusCode.TooManyRequests)
        }
        exception<Exception> { cause ->
            cause.printStackTrace()
            call.respond(
                message = "Oops, something went wrong with your request.\nPlease try again later.",
                status = HttpStatusCode.InternalServerError
            )
        }
    }

    install(ResourceAccessControl) {
        whitelistAddresses {
            +"8.8.8.8"
            -"8.8.8.8"
        }

        apiKeys {
            +"B5B65B1FBDB32AB359479D861AF2D"
        }
    }


    routing {
        loadRoutes(koin)

        /* LANDING PAGE */
        get("/") {
            call.respondText("Welcome to the PenguStore API!!")
        }

        /* STATIC FILES */
        static("/static") {
            resources("static")
        }
        resource("/favicon.ico", "static/favicon.ico")
    }
}

data class ForbiddenException(val content: Any) : RuntimeException("")
data class UnauthorizedException(val content: Any) : RuntimeException("")
data class BadRequestException(val content: Any) : RuntimeException("")
data class NotFoundException(val content: Any) : RuntimeException("")
data class ConflictException(val content: Any) : RuntimeException("")
data class NotAcceptableException(val content: Any) : RuntimeException("")
data class InternalServerErrorException(val content: Any) : RuntimeException("")
data class TooManyRequestsException(val content: Any, val retryAfter: Instant? = null) : RuntimeException("")