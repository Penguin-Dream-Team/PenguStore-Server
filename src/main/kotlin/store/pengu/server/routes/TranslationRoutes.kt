package store.pengu.server.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import store.pengu.server.*
import store.pengu.server.daos.TranslationDao
import store.pengu.server.routes.responses.Response

fun Route.translationRoutes(
    translationDao: TranslationDao
) {
    authenticate {
        get<Translation> { param ->
            val translation = withContext(Dispatchers.IO) {
                translationDao.findTranslation(param.string)
            }
            call.respond(Response(translation))
        }
    }
}
