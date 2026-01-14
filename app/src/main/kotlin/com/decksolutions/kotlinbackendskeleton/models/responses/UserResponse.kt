package com.decksolutions.kotlinbackendskeleton.models.responses

import kotlinx.serialization.Serializable

/**
 * Response with user information (without sensitive data)
 *
 * @property id Unique user identifier
 * @property email User email
 */
@Serializable
data class UserResponse(
    val id: Long,
    val email: String
)
