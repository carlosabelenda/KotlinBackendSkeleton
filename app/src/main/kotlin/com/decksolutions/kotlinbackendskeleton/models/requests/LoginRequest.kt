package com.decksolutions.kotlinbackendskeleton.models.requests

import kotlinx.serialization.Serializable

/**
 * User authentication request
 *
 * @property email User email.
 * @property password Plain text password.
 */
@Serializable
data class LoginRequest(
    val email: String, // User email.
    val password: String // Plain text password.
)
