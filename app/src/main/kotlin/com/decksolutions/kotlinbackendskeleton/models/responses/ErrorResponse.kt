package com.decksolutions.kotlinbackendskeleton.models.responses

import kotlinx.serialization.Serializable

/**
 * Standard error response
 *
 * @property message Descriptive error message.
 * @property code Optional error code.
 */
@Serializable
data class ErrorResponse(
    val message: String,
    val code: String? = null
)