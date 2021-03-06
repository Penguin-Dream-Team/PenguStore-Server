@file:Suppress("PropertyName")

import org.flywaydb.gradle.task.FlywayMigrateTask
import nu.studer.gradle.jooq.JooqGenerate
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val logback_version: String by project
val jooq_version: String by project
val mysql_version: String by project
val koin_version: String by project
val mockk_version: String by project
val kotest_version: String by project
val hikari_version: String by project
val config4k_version: String by project
val jackson_datatype_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.31"
    id("nu.studer.jooq") version "5.2.1"
    id("com.bmuschko.docker-java-application") version "6.7.0"
    id("org.flywaydb.flyway") version "7.6.0"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useIR = true
compileKotlin.kotlinOptions.freeCompilerArgs = listOf(
    "-Xuse-experimental=io.ktor.locations.KtorExperimentalLocationsAPI",
    "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes"
)

group = "store.pengu"
version = "0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

docker {
    javaApplication {
        baseImage.set("adoptopenjdk:11")
        maintainer.set("PenguStore")
        ports.add(8080)
        jvmArgs.add("-XX:+UseShenandoahGC")
        jvmArgs.add("-XX:MaxRAMPercentage=90")
    }
}

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://kotlin.bintray.com/ktor") }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-websockets:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.github.config4k", "config4k", config4k_version)

    implementation("org.jooq", "jooq", jooq_version)
    implementation("com.zaxxer", "HikariCP", hikari_version)

    implementation("org.koin", "koin-core", koin_version)
    implementation("io.mockk", "mockk", mockk_version)

    // Jackson data conversion
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jackson_datatype_version)


    jooqGenerator("mysql", "mysql-connector-java", mysql_version)
    implementation("mysql", "mysql-connector-java", mysql_version)

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.kotest:kotest-runner-junit5:${kotest_version}")
    testImplementation("io.kotest:kotest-assertions-core:${kotest_version}")
}


//From here until end, only database configuration

val DatabaseHost = System.getenv("DB_HOST") ?: "localhost"
val DatabasePort = System.getenv("DB_PORT") ?: "3306"
val DatabaseDatabase = System.getenv("DB_DATABASE") ?: "pengustore"
val DatabaseUser = System.getenv("DB_USER") ?: "pengustore"
val DatabasePassword = System.getenv("DB_PASSWORD") ?: ""

sourceSets {
    getByName("main").java.srcDirs("src/main/jooqPengustore")
}

flyway {
    driver = "com.mysql.cj.jdbc.Driver"
    url = "jdbc:mysql://${DatabaseHost}:${DatabasePort}/${DatabaseDatabase}"
    user = DatabaseUser
    password = DatabasePassword
    baselineOnMigrate = true
    locations = arrayOf("filesystem:src/main/resources/db/migrations")
}

jooq {
    version.set(jooq_version)

    configurations {
        create("pengustore") {
            jooqConfiguration.apply {
                jdbc.apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = "jdbc:mysql://${DatabaseHost}:${DatabasePort}/${DatabaseDatabase}"
                    user = DatabaseUser
                    password = DatabasePassword
                }
                generator.apply {
                    name = "org.jooq.codegen.JavaGenerator"
                    database.apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = DatabaseDatabase
                        withOutputSchemaToDefault(true)
                        excludes = "flyway_schema_history"
                    }
                    generate.apply {
                        isDeprecated = false
                        isRecords = true
                        isImmutablePojos = true
                        isFluentSetters = true
                    }
                    target.apply {
                        packageName = "store.pengu.server.db.pengustore"
                        directory = "src/main/jooqPengustore"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

task("migrateDatabase") {
    dependsOn(tasks.named<FlywayMigrateTask>("flywayMigrate"))
    dependsOn(tasks.named<JooqGenerate>("generatePengustoreJooq"))
}
