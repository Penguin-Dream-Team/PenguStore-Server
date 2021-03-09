package store.pengu.server.utils

import java.net.InetAddress

object AddressUtils {
    fun isPrivateAddress(address: InetAddress): Boolean {
        return address.isSiteLocalAddress ||
                address.isAnyLocalAddress ||
                address.isLinkLocalAddress ||
                address.isLoopbackAddress ||
                address.isMulticastAddress
    }
}

