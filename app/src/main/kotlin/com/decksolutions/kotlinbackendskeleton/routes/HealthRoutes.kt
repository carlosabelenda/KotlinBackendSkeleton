package com.decksolutions.kotlinbackendskeleton.routes

import io.ktor.server.application.Application
import com.decksolutions.kotlinbackendskeleton.services.HealthService
import org.slf4j.LoggerFactory

import io.ktor.server.routing.routing
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Health check routes configuration
 *
 * @param healthService Health check service
 */
fun Application.configureHealthRoutes(healthService: HealthService) {
    val logger = LoggerFactory.getLogger("HealthRoutes")

    routing {
        /**
         * GET /api/health_check
         * Checks the system health status
         *
         * Returns 200 OK with detailed system status information:
         * - Overall status (ok, degraded, down)
         * - Database connection status
         * - Service uptime
         * - Check timestamp
         */
        get("/api/health_check") {
            try {
                val healthResponse = healthService.checkHealth()

                // Determine HTTP status code based on system status
                val statusCode = when (healthResponse.status) {
                    "ok" -> HttpStatusCode.OK
                    "degraded" -> HttpStatusCode.OK // Still working but with warnings
                    "down" -> HttpStatusCode.ServiceUnavailable
                    else -> HttpStatusCode.InternalServerError
                }

                logger.debug("Health check performed: ${healthResponse.status}")
                logger.debug("Health check performed: ${statusCode}")
                call.respond(statusCode, healthResponse)

            } catch (e: Exception) {
                logger.error("Error performing health check", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf(
                        "status" to "error",
                        "message" to "Error checking system status",
                        "error" to (e.message ?: "Unknown error")
                    )
                )
            }
        }
    }
}
