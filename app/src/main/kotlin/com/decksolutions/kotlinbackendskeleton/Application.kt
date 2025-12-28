package com.decksolutions.kotlinbackendskeleton

import com.decksolutions.kotlinbackendskeleton.databases.DatabaseConfig
import com.decksolutions.kotlinbackendskeleton.routes.configureHealthRoutes
import com.decksolutions.kotlinbackendskeleton.services.HealthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

/**
 * Main function of the application
 */
fun main() {
    embeddedServer(Netty, port = 1234, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Logging configuration
    install(CallLogging) {
        level = Level.INFO
    }

    // Content negotiation configuration (JSON)
    install(ContentNegotiation) {
        json()
    }

    // Error handling configuration
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("message" to (cause.message ?: "Internal server error"))
            )
        }
    }

    // Get environment configuration
    val dbUrl = System.getenv("DB_URL") ?: throw IllegalArgumentException("DB_URL environment variable not set")
    val dbUser = System.getenv("DB_USER") ?: throw IllegalArgumentException("DB_USER environment variable not set")
    val dbPassword = System.getenv("DB_PASSWORD") ?: throw IllegalArgumentException("DB_PASSWORD environment variable not set")
    val dbDriver = System.getenv("DB_DRIVER") ?: throw IllegalArgumentException("DB_DRIVER environment variable not set")

    val logger = LoggerFactory.getLogger("ApplicationModule")
    logger.info("Starting application with DB Driver: $dbDriver")

    /*
    val dbUrl = System.getenv("DB_URL") ?: "jdbc:postgresql://localhost:5432/sportsmanager"
    val dbUser = System.getenv("DB_USER") ?: "postgres"
    val dbPassword = System.getenv("DB_PASSWORD") ?: "postgres"
    val dbDriver = System.getenv("DB_DRIVER") ?: "org.postgresql.Driver"
     */
    // Initialize database
    DatabaseConfig.init(
        jdbcUrl = dbUrl,
        driverClassName = dbDriver,
        username = dbUser,
        password = dbPassword
    )


    val healthService = HealthService()
    // Configure routes
    configureHealthRoutes(healthService)


    // Hello World configuration
    routing {


        get("/echo") {
            call.respondText("Server running")
        }
    }
}
