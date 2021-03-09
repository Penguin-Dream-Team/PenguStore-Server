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

class ResourceAccessControl(config: Configuration) {
    private val whitelistedAddresses = config._whitelistedAddresses
    private val apiKeys = config._apiKeys

    @Suppress("PropertyName")
    class Configuration {
        internal var _whitelistedAddresses: Set<String> = emptySet()
        fun whitelistAddresses(setup: StringSetBuilder.() -> Unit) {
            _whitelistedAddresses = StringSetBuilder().apply(setup).build()
        }

        internal var _apiKeys: Set<String> = emptySet()
        fun apiKeys(setup: StringSetBuilder.() -> Unit) {
            _apiKeys = StringSetBuilder().apply(setup).build()
        }
    }

    enum class Type {
        IP_WHITELIST,
        API_TOKEN
    }

    fun interceptPipeline(
        pipeline: ApplicationCallPipeline, types: Set<Type>
    ) {
        pipeline.insertPhaseAfter(ApplicationCallPipeline.Features, Authentication.ChallengePhase)
        pipeline.insertPhaseAfter(Authentication.ChallengePhase, AuthorizationPhase)

        pipeline.intercept(AuthorizationPhase) {
            val denyReasons = mutableListOf<String>()

            for (type in types) {
                when (type) {
                    Type.IP_WHITELIST -> {
                        val address = call.request.origin.remoteHost
                        when {
                            whitelistedAddresses.contains(address) -> continue
                            isPrivateAddress(InetAddress.getByName(address)) -> continue
                            else -> denyReasons += "IP ($address) not whitelisted. Please go away!"
                        }
                    }
                    Type.API_TOKEN -> {
                        denyReasons += when (call.request.headers["X-API-KEY"]) {
                            in apiKeys -> continue
                            null -> "No API Key provided"
                            else -> "API Key invalid or unauthorized"
                        }
                    }
                }
            }

            if (denyReasons.isNotEmpty()) {
                val message = denyReasons.joinToString(". ")
                call.application.environment.log.error(message)
                throw ForbiddenException(message)
            }
        }
    }

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, ResourceAccessControl> {
        override val key = AttributeKey<ResourceAccessControl>("ResourceAccessControl")

        val AuthorizationPhase = PipelinePhase("Authorization")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): ResourceAccessControl {
            val configuration = Configuration().apply(configure)
            return ResourceAccessControl(configuration)
        }

    }
}

class ResourceAccessControlRouteSelector(private val description: String) :
    RouteSelector(RouteSelectorEvaluation.qualityConstant) {
    override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) =
        RouteSelectorEvaluation.Constant

    override fun toString() = "(symrac $description})"
}

fun Route.controlledAccess(vararg types: ResourceAccessControl.Type, build: Route.() -> Unit): Route {
    val typesSet = types.toSet()
    val description = typesSet.let { "types (${typesSet.joinToString(", ")}" }
    val authorizedRoute = createChild(ResourceAccessControlRouteSelector(description))
    application.feature(ResourceAccessControl).interceptPipeline(authorizedRoute, typesSet)
    authorizedRoute.build()
    return authorizedRoute
}