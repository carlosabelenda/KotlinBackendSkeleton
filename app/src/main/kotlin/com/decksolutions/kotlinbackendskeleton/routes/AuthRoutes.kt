package com.decksolutions.kotlinbackendskeleton.routes

import com.decksolutions.kotlinbackendskeleton.models.requests.LoginRequest
import com.decksolutions.kotlinbackendskeleton.models.requests.RegisterRequest
import com.decksolutions.kotlinbackendskeleton.models.responses.AuthResponse
import com.decksolutions.kotlinbackendskeleton.models.responses.DeleteUserResponse
import com.decksolutions.kotlinbackendskeleton.models.responses.ErrorResponse
import com.decksolutions.kotlinbackendskeleton.models.responses.UserResponse
import com.decksolutions.kotlinbackendskeleton.services.AuthService
import com.decksolutions.kotlinbackendskeleton.services.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory


/**
 * Authentication routes configuration
 *
 * @param userService User service
 * @param authService Authentication service
 */
fun Application.configureAuthRoutes(
    userService: UserService,
    authService: AuthService
) {
    val logger = LoggerFactory.getLogger("AuthRoutes")

    routing {
        route("/api/auth") {
            /**
             * POST /api/auth/register
             * Registers a new user
             */
            post("/register") {
                try {
                    val request = call.receive<RegisterRequest>()

                    logger.info("Registration request received for email: ${request.email}")

                    val user = userService.registerUser(
                        email = request.email,
                        username = request.username,
                        password = request.password
                    )

                    // Generate JWT token for the newly registered user
                    val (authenticatedUser, token) = authService.authenticate(
                        email = request.email,
                        password = request.password
                    )

                    val response = AuthResponse(
                        token = token,
                        user = UserResponse(
                            id = authenticatedUser.id,
                            email = authenticatedUser.email
                        )
                    )

                    logger.info("User registered successfully: ${user.email}")
                    call.respond(HttpStatusCode.Created, response)

                } catch (e: IllegalArgumentException) {
                    logger.warn("Registration error: ${e.message}")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(message = e.message ?: "Error registering user")
                    )
                } catch (e: IllegalStateException) {
                    logger.warn("Registration state error: ${e.message}")
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse(message = e.message ?: "Error registering user")
                    )
                } catch (e: Exception) {
                    logger.error("Unexpected error in registration", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(message = "Internal server error")
                    )
                }
            }

            /**
             * POST /api/auth/login
             * Authenticates a user and returns a JWT token
             */
            post("/login") {
                try {
                    val request = call.receive<LoginRequest>()

                    logger.info("Login request received for email: ${request.email}")

                    val (user, token) = authService.authenticate(
                        email = request.email,
                        password = request.password
                    )

                    val response = AuthResponse(
                        token = token,
                        user = UserResponse(
                            id = user.id,
                            email = user.email
                        )
                    )

                    logger.info("User authenticated successfully: ${user.email}")
                    call.respond(HttpStatusCode.OK, response)

                } catch (e: IllegalArgumentException) {
                    logger.warn("Login error: ${e.message}")
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        ErrorResponse(message = "Invalid credentials")
                    )
                } catch (e: Exception) {
                    logger.error("Unexpected error in login", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse(message = "Internal server error")
                    )
                }
            }

            /**
             * DELETE /api/auth/delete_user
             * Deletes the authenticated user (can only delete themselves)
             * Requires valid JWT authentication
             */
            authenticate("auth-jwt") {
                delete("/delete_user") {
                    try {
                        val principal = call.principal<JWTPrincipal>()
                            ?: throw IllegalStateException("Invalid JWT token")

                        // Extract user ID from token
                        val userId = principal.payload.subject.toLong()

                        logger.info("Deletion request received for user ID: $userId")

                        // Verify user exists
                        val user = userService.getUserById(userId)
                        if (user == null) {
                            logger.warn("Attempt to delete non-existent user: $userId")
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse(message = "User not found")
                            )
                            return@delete
                        }

                        // Delete user (can only delete themselves)
                        val deleted = userService.deleteUser(userId)

                        if (deleted) {
                            logger.info("User deleted successfully: ${user.email}")
                            val response = DeleteUserResponse(
                                message = "User deleted successfully",
                                deletedUser = UserResponse(
                                    id = user.id,
                                    email = user.email
                                )
                            )
                            call.respond(HttpStatusCode.OK, response)
                        } else {
                            logger.warn("Could not delete user: $userId")
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                ErrorResponse(message = "Error deleting user")
                            )
                        }

                    } catch (e: NumberFormatException) {
                        logger.error("Error parsing user ID from token", e)
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ErrorResponse(message = "Invalid JWT token")
                        )
                    } catch (e: IllegalStateException) {
                        logger.warn("Authentication error: ${e.message}")
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            ErrorResponse(message = "Invalid or missing JWT token")
                        )
                    } catch (e: IllegalArgumentException) {
                        logger.warn("Deletion error: ${e.message}")
                        call.respond(
                            HttpStatusCode.NotFound,
                            ErrorResponse(message = e.message ?: "User not found")
                        )
                    } catch (e: Exception) {
                        logger.error("Unexpected error in deletion", e)
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            ErrorResponse(message = "Internal server error")
                        )
                    }
                }
            }
        }
    }
}
