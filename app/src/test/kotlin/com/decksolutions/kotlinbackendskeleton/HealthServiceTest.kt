package com.decksolutions.kotlinbackendskeleton


import com.decksolutions.kotlinbackendskeleton.services.HealthService

import io.mockk.*
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.sql.SQLException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HealthServiceTest {
    private lateinit var healthService: HealthService

    @Before
    fun setup() {
        healthService = HealthService()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test checkHealth returns response with correct structure`() {
        // Without a database configured, the health check will report error
        // This test verifies the response structure is correct
        val healthStatus = healthService.checkHealth()

        assertNotNull(healthStatus)
        assertEquals("kotlin-backend-skeleton", healthStatus.service)
        assertEquals("1.0.0", healthStatus.version)
        assertNotNull(healthStatus.timestamp)
        assertNotNull(healthStatus.checks)
        assertNotNull(healthStatus.checks.database)
        assertTrue(healthStatus.checks.uptime >= 0)
    }

    @Test
    fun `test checkHealth returns down status when database is unavailable`() {
        // Mock TransactionManager to simulate database failure
        mockkObject(TransactionManager.Companion)
        every { TransactionManager.currentOrNull() } returns null
        every { TransactionManager.manager } throws SQLException("Connection refused: database is down")

        val healthStatus = healthService.checkHealth()

        assertNotNull(healthStatus)
        assertEquals("down", healthStatus.status)
        assertEquals("error", healthStatus.checks.database.status)
        assertNotNull(healthStatus.checks.database.message)
        assertTrue(healthStatus.checks.database.message.contains("Connection refused"))
        assertNull(healthStatus.checks.database.responseTimeMs)
    }

    @Test
    fun `test checkHealth handles RuntimeException from database`() {
        // Mock TransactionManager to simulate unexpected error
        mockkObject(TransactionManager.Companion)
        every { TransactionManager.currentOrNull() } returns null
        every { TransactionManager.manager } throws RuntimeException("Unexpected database error")

        val healthStatus = healthService.checkHealth()

        assertNotNull(healthStatus)
        assertEquals("down", healthStatus.status)
        assertEquals("error", healthStatus.checks.database.status)
        assertNotNull(healthStatus.checks.database.message)
        assertTrue(healthStatus.checks.database.message.contains("Unexpected database error"))
        assertNull(healthStatus.checks.database.responseTimeMs)
    }
}