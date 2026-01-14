package com.decksolutions.kotlinbackendskeleton.models.responses

import kotlinx.serialization.Serializable

/**
 * After user elimination response
 *
 * @property message Confirmation message
 * @property deletedUser Deleted user information.
 */
@Serializable
data class DeleteUserResponse(
    val message: String,
    val deletedUser: UserResponse
)