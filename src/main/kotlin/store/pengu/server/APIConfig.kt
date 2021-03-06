package store.pengu.server

import com.zaxxer.hikari.HikariConfig

data class DatabaseConfig(
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val password: String,
    val maxPoolSize: Int
) {
    fun toHikariConfig(): HikariConfig {
        val hikariConfig = HikariConfig()
        hikariConfig.jdbcUrl = "jdbc:mysql://${host}:${port}/${database}"
        hikariConfig.username = user
        hikariConfig.password = password
        hikariConfig.maximumPoolSize = maxPoolSize
        return hikariConfig
    }
}

data class APIConfig(
    val database: DatabaseConfig,
)