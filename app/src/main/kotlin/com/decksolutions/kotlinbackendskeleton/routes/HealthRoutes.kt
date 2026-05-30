package com.decksolutions.kotlinbackendskeleton.routes

import com.decksolutions.kotlinbackendskeleton.models.responses.HealthResponse
import com.decksolutions.kotlinbackendskeleton.services.HealthService
import io.github.smiley4.ktoropenapi.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Application.configureHealthRoutes(healthService: HealthService) {
    val logger = LoggerFactory.getLogger("HealthRoutes")

    routing {
        get("/api/health_check", {
            tags = listOf("Health")
            summary = "Check system health status"
            description = "Returns the system health status including database connection and uptime"
            response {
                HttpStatusCode.OK to {
                    description = "System running correctly"
                    body<HealthResponse> {
                        description = "Detailed system status information"
                    }
                }
                HttpStatusCode.ServiceUnavailable to {
                    description = "System unavailable"
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server error"
                }
            }
        }) {
            try {
                val healthResponse = healthService.checkHealth()

                val statusCode = when (healthResponse.status) {
                    "ok" -> HttpStatusCode.OK
                    "degraded" -> HttpStatusCode.OK
                    "down" -> HttpStatusCode.ServiceUnavailable
                    else -> HttpStatusCode.InternalServerError
                }

                logger.debug("Health check performed: ${healthResponse.status}")
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
