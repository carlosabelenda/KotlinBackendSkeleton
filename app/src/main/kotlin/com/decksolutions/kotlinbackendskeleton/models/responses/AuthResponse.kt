package com.decksolutions.kotlinbackendskeleton.models.responses

import kotlinx.serialization.Serializable

/**
 * Response after a successful authentication
 *
 * @property token JWT Token for authentication in subsequent requests
 * @property user Authenticated user information
 */
@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)
