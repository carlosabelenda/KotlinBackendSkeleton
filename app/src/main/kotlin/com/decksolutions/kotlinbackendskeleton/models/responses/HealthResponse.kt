package com.decksolutions.kotlinbackendskeleton.models.responses


import kotlinx.serialization.Serializable

/**
 * System health check response
 *
 * @property status Overall system status (ok, degraded, down)
 * @property service Service name
 * @property version Service version
 * @property timestamp Check timestamp
 * @property checks Individual component checks
 */
@Serializable
data class HealthResponse(
    val status: String,
    val service: String,
    val version: String,
    val timestamp: String,
    val checks: HealthChecks
)

/**
 * Individual system component checks
 *
 * @property database Database connection status
 * @property uptime Service uptime in seconds
 */
@Serializable
data class HealthChecks(
    val database: DatabaseHealth,
    val uptime: Long
)

/**
 * Database connection status
 *
 * @property status Connection status (ok, error)
 * @property message Descriptive message
 * @property responseTimeMs Response time in milliseconds (if available)
 */
@Serializable
data class DatabaseHealth(
    val status: String,
    val message: String,
    val responseTimeMs: Long? = null
)
