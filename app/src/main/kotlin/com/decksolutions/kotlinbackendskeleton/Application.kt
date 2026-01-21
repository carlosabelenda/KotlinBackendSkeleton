package com.decksolutions.kotlinbackendskeleton

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.decksolutions.kotlinbackendskeleton.databases.DatabaseConfig
import com.decksolutions.kotlinbackendskeleton.repositories.UserRepository
import com.decksolutions.kotlinbackendskeleton.routes.configureAuthRoutes
import com.decksolutions.kotlinbackendskeleton.routes.configureHealthRoutes
import com.decksolutions.kotlinbackendskeleton.services.AuthService
import com.decksolutions.kotlinbackendskeleton.services.HealthService
import com.decksolutions.kotlinbackendskeleton.services.JWTService
import com.decksolutions.kotlinbackendskeleton.services.PasswordService
import com.decksolutions.kotlinbackendskeleton.services.UserService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

/**
 * Main function of the application
 */
fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Logging configuration
    install(CallLogging) {
        level = Level.INFO
    }

    // CORS settings
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        anyHost()
    }

    // Serialization JSON settings
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Getting settings for the JWT
    val jwtSecret = System.getenv("JWT_SECRET") ?: "your-secret-key-change-in-production"
    val jwtIssuer = System.getenv("JWT_ISSUER") ?: "your-issuer"
    val jwtAudience = System.getenv("JWT_AUDIENCE") ?: "your-audience-target"

    // Configuración de autenticación JWT
    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtAudience
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .withAudience(jwtAudience)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }

        }
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


    // Initialize database
    DatabaseConfig.init(
        jdbcUrl = dbUrl,
        driverClassName = dbDriver,
        username = dbUser,
        password = dbPassword
    )

    // Init services
    val passwordService = PasswordService()
    val jwtService = JWTService(
        secret = jwtSecret,
        issuer = jwtIssuer,
        audience = jwtAudience
    )
    val userRepository = UserRepository()
    val userService = UserService(userRepository, passwordService)
    val authService = AuthService(userRepository, passwordService, jwtService)
    val healthService = HealthService()

    // Configure routes
    configureHealthRoutes(healthService)
    configureAuthRoutes(userService, authService)

    // Hello World configuration
    routing {

        get("/echo") {
            call.respondText("Server running")
        }
    }
}
