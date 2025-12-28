package com.decksolutions.kotlinbackendskeleton.models.entities

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


/**
 * Users table in the database
 */
object Users : LongIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val updatedAt = datetime("updated_at").default(LocalDateTime.now())
}

/**
 * User entity representing a record in the database
 */
class UserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<UserEntity>(Users)

    var email by Users.email
    var username by Users.username
    var passwordHash by Users.passwordHash
    var createdAt by Users.createdAt
    var updatedAt by Users.updatedAt

    /**
     * Converts the entity to a domain model
     */
    fun toUser(): User {
        return User(
            id = id.value,
            email = email,
            username = username,
            passwordHash = passwordHash,
            createdAt = createdAt,
            updatedAt = updatedAt,

        )
    }
}

/**
 * User domain model (without sensitive information)
 */
data class User(
    val id: Long,
    val email: String,
    val username: String?,
    val passwordHash: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
