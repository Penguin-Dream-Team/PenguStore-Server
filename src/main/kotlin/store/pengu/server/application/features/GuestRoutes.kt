package store.pengu.server.application.features

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.util.*
import io.ktor.util.pipeline.*
import store.pengu.server.ForbiddenException
import store.pengu.server.utils.AddressUtils.isPrivateAddress
import store.pengu.server.utils.StringSetBuilder
import java.net.InetAddress

object GuestRoutes : ApplicationFeature<ApplicationCallPipeline, GuestRoutes.Configuration, GuestRoutes> {

    object Configuration

    fun interceptPipeline(pipeline: ApplicationCallPipeline) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.ChallengePhase)
        pipeline.insertPhaseAfter(Authentication.ChallengePhase, AuthorizationPhase)

        pipeline.intercept(AuthorizationPhase) {
            if (!call.request.headers["Authorization"].isNullOrBlank()) {
                val message = "You cannot access this resource whilst logged in."
                call.application.environment.log.error(message)
                throw ForbiddenException(message)
            }
        }
    }

    override val key = AttributeKey<GuestRoutes>("GuestRoutes")

    private val AuthorizationPhase = PipelinePhase("Authorization")

    override fun install(
        pipeline: ApplicationCallPipeline,
        configure: Configuration.() -> Unit
    ): GuestRoutes {
        return GuestRoutes
    }

}

object GuestRoutesRouteSelector :
    RouteSelector(RouteSelectorEvaluation.qualityConstant) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) =
        RouteSelectorEvaluation.Constant

    override fun toString() = "(guest routes)"
}

fun Route.guestOnly(build: Route.() -> Unit): Route {
    val authorizedRoute = createChild(GuestRoutesRouteSelector)
    application.feature(GuestRoutes).interceptPipeline(authorizedRoute)
    authorizedRoute.build()
    return authorizedRoute
}