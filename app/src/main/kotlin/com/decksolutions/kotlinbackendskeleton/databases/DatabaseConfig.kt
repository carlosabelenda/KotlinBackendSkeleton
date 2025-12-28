package com.decksolutions.kotlinbackendskeleton.databases

import com.decksolutions.kotlinbackendskeleton.models.entities.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Database connection configuration and management
 */
object DatabaseConfig {
    private val logger = LoggerFactory.getLogger(DatabaseConfig::class.java)

    /**
     * Initializes the database connection
     *
     * @param jdbcUrl JDBC connection URL
     * @param driverClassName Database driver name
     * @param username Database username
     * @param password Database password
     * @param maxPoolSize Maximum connection pool size
     */
    fun init(
        jdbcUrl: String,
        driverClassName: String,
        username: String,
        password: String,
        maxPoolSize: Int = 10
    ) {
        logger.info("Initializing database connection...")

        val hikariConfig = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = driverClassName
            this.username = username
            this.password = password
            this.maximumPoolSize = maxPoolSize
            this.minimumIdle = 2
            this.connectionTimeout = 30000
            this.idleTimeout = 600000
            this.maxLifetime = 1800000
        }

        val dataSource = HikariDataSource(hikariConfig)

        Database.connect(dataSource)

        logger.info("Database connection established successfully")

        // Create tables if they do not exist
        createTables()
    }

    /**
     * Creates necessary tables in the database
     */
    private fun createTables() {
        logger.info("Creating tables in the database...")
        transaction {
            SchemaUtils.create(Users)
        }
        logger.info("Tables created successfully")
    }



    /**
     * Closes the database connection
     */
    fun close() {
        logger.info("Closing database connection...")
        // HikariCP handles closing automatically
    }
}
