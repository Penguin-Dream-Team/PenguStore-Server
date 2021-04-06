package store.pengu.server.utils

import java.security.SecureRandom

object PasswordUtils {

    private const val letters = "abcdefghijklmnopqrstuvwxyz"
    private const val numbers = "0123456789"
    private const val special = "@#=+!£$%&?"
    private const val passwordLength = 20

    fun generatePassword(): String {
        val result = letters + letters.toUpperCase() + numbers + special

        val rnd = SecureRandom.getInstance("SHA1PRNG")
        val sb = StringBuilder(passwordLength)

        repeat(passwordLength) {
            val randomInt: Int = rnd.nextInt(result.length)
            sb.append(result[randomInt])
        }

        return sb.toString()
    }

}