package store.pengu.server.utils

import store.pengu.server.ForbiddenException
import io.ktor.features.*
import io.ktor.request.*
import java.net.InetAddress

object ResourceAccessControl {
    enum class Type {
        IP_WHITELIST,
        API_TOKEN
    }

    private fun isPrivateAddress(address: InetAddress): Boolean {
        return address.isSiteLocalAddress ||
                address.isAnyLocalAddress ||
                address.isLinkLocalAddress ||
                address.isLoopbackAddress ||
                address.isMulticastAddress
    }

    private fun checkIPWhitelist(request: ApplicationRequest) {
        val address = request.origin.remoteHost
        when {
            // address == "SOME IP THAT NEEDS ACCESS" -> return
            isPrivateAddress(InetAddress.getByName(address)) -> return
            else -> throw ForbiddenException("IP ($address) not whitelisted. Please go away!")
        }
    }

    private fun checkAPIToken(request: ApplicationRequest) {
        when (request.headers["X-API-KEY"]) {
            "B5B65B1FBDB32AB359479D861AF2D" -> return
            // COMMENTED FOR TESTING PURPOSES
            //null -> throw UnauthorizedException("No API Key provided")
            //else -> throw ForbiddenException("API Key invalid or unauthorized")
        }
    }

    fun checkShouldHaveAccess(request: ApplicationRequest, type: Type, vararg types: Type) {
        val allTypes = listOf(type) + types
        for (t in allTypes) {
            when (t) {
                Type.IP_WHITELIST -> checkIPWhitelist(request)
                Type.API_TOKEN -> checkAPIToken(request)
            }
        }
    }

}