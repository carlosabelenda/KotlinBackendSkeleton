package com.decksolutions.kotlinbackendskeleton.services

import com.decksolutions.kotlinbackendskeleton.models.entities.Users
import com.decksolutions.kotlinbackendskeleton.models.responses.DatabaseHealth
import com.decksolutions.kotlinbackendskeleton.models.responses.HealthChecks
import com.decksolutions.kotlinbackendskeleton.models.responses.HealthResponse
import com.decksolutions.kotlinbackendskeleton.repositories.UserRepository
import com.sun.org.slf4j.internal.LoggerFactory
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant

/**
 * Service to check the system health status
 */
class HealthService {
    private val logger = org.slf4j.LoggerFactory.getLogger(com.decksolutions.kotlinbackendskeleton.services.HealthService::class.java)
    private val startTime = System.currentTimeMillis()

    /**
     * Checks the complete system health status
     *
     * @return HealthResponse with the status of all components
     */
    fun checkHealth(): HealthResponse {
        val databaseHealth = checkDatabase()
        val uptime = (System.currentTimeMillis() - startTime) / 1000

        val checks = HealthChecks(
            database = databaseHealth,
            uptime = uptime
        )

        // Determine overall status
        val overallStatus = when {
            databaseHealth.status == "error" -> "down"
            databaseHealth.status == "ok" -> "ok"
            else -> "degraded"
        }


        return HealthResponse(
            status = overallStatus,
            service = "kotlin-backend-skeleton",
            version = "1.0.0",
            timestamp = Instant.now().toString(),
            checks = checks
        )
    }

    /**
     * Checks the database connection status
     *
     * @return DatabaseHealth with the connection status
     */
    private fun checkDatabase(): DatabaseHealth {
        return try {
            val startTime = System.currentTimeMillis()

            // Attempt to execute a simple query to verify connection
            transaction {
                // Simple query: count records in Users table (or simply verify table exists)
                Users.selectAll().limit(1).firstOrNull()
            }

            val responseTime = System.currentTimeMillis() - startTime

            DatabaseHealth(
                status = "ok",
                message = "Database connection established successfully",
                responseTimeMs = responseTime
            )
        } catch (e: Exception) {
            logger.error("Error checking database connection", e)
            DatabaseHealth(
                status = "error",
                message = "Database connection error: ${e.message}",
                responseTimeMs = null
            )
        }
    }
}
