package store.pengu.server

import com.typesafe.config.ConfigFactory
import com.zaxxer.hikari.HikariDataSource
import io.github.config4k.extract
import org.flywaydb.core.Flyway
import org.jooq.Configuration
import org.jooq.SQLDialect
import org.jooq.impl.DefaultConfiguration
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import store.pengu.server.daos.*

object ApiModule {

    private fun Scope.getDatabase() = get<Configuration>(named("database"))

    val module = module {
        single<APIConfig> {
            ConfigFactory.load().extract("api")
        }
        single {
            val config = get<APIConfig>().database
            Flyway.configure()
                .dataSource(get(named("api")))
                .schemas(config.database)
                .load()
        }
        single(named("database")) {
            val config = get<APIConfig>().database
            val dataSource = HikariDataSource(config.toHikariConfig())
            DefaultConfiguration()
                .set(SQLDialect.MYSQL)
                .set(dataSource)
        }
        single {
            UserDao(getDatabase())
        }
        single {
            PantryDao(getDatabase())
        }
        single {
            ProductDao(getDatabase())
        }
        single {
            ShopDao(getDatabase(), get<ProductDao>())
        }
        single {
            ListDao(getDatabase())
        }
        single {
            TranslationDao(getDatabase())
        }
    }
}