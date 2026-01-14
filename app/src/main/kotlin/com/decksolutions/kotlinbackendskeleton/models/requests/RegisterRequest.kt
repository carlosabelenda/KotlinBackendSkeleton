package com.decksolutions.kotlinbackendskeleton.models.requests

import kotlinx.serialization.Serializable

/**
 * New User registry request
 *
 * @property email User email (must be unique)
 * @property username Username (must be unique)
 * @property password Plain text password (it will be hashed before storing)
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String
)
