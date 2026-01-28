package com.decksolutions.kotlinbackendskeleton.routes

import com.decksolutions.kotlinbackendskeleton.models.requests.LoginRequest
import com.decksolutions.kotlinbackendskeleton.models.requests.RegisterRequest
import com.decksolutions.kotlinbackendskeleton.models.responses.AuthResponse
import com.decksolutions.kotlinbackendskeleton.models.responses.DeleteUserResponse
import com.decksolutions.kotlinbackendskeleton.models.responses.ErrorResponse
import com.decksolutions.kotlinbackendskeleton.models.responses.UserResponse
import com.decksolutions.kotlinbackendskeleton.services.AuthService
import com.decksolutions.kotlinbackendskeleton.services.UserService
import io.github.smiley4.ktoropenapi.delete
import io.github.smiley4.ktoropenapi.post
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.slf4j.LoggerFactory

fun Application.configureAuthRoutes(
    userService: UserService,
    authService: AuthService
) {
    val logger = LoggerFactory.getLogger("AuthRoutes")

    routing {
        route("/api/auth") {
            post("/register", {
                tags = listOf("Authentication")
                summary = "Register new user"
                description = "Creates a new user account and returns a JWT token"
                request {
                    body<RegisterRequest> {
                        description = "User registration data"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.Created to {
                        description = "User registered successfully"
                        body<AuthResponse> {
                            description = "JWT token and user data"
                        }
                    }
                    HttpStatusCode.BadRequest to {
                        description = "Invalid registration data or user already exists"
                        body<ErrorResponse>()
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Internal server error"
                        body<ErrorResponse>()
                    }
                }
            }) {
                try {
                    val request = call.receive<RegisterRequest>()

                    logger.info("Registration request received for email: ${request.email}")

                    val user = userService.registerUser(
                        email = request.email,
                        username = request.username,
                        password = request.password
                    )

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

            post("/login", {
                tags = listOf("Authentication")
                summary = "User login"
                description = "Authenticates a user and returns a JWT token"
                request {
                    body<LoginRequest> {
                        description = "Login credentials"
                        required = true
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Login successful"
                        body<AuthResponse> {
                            description = "JWT token and user data"
                        }
                    }
                    HttpStatusCode.Unauthorized to {
                        description = "Invalid credentials"
                        body<ErrorResponse>()
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Internal server error"
                        body<ErrorResponse>()
                    }
                }
            }) {
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

            authenticate("auth-jwt") {
                delete("/delete_user", {
                    tags = listOf("Authentication")
                    summary = "Delete user"
                    description = "Deletes the authenticated user account. Requires a valid JWT token."
                    securitySchemeNames = listOf("JWT-Auth")
                    response {
                        HttpStatusCode.OK to {
                            description = "User deleted successfully"
                            body<DeleteUserResponse>()
                        }
                        HttpStatusCode.Unauthorized to {
                            description = "Invalid or expired JWT token"
                            body<ErrorResponse>()
                        }
                        HttpStatusCode.NotFound to {
                            description = "User not found"
                            body<ErrorResponse>()
                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal server error"
                            body<ErrorResponse>()
                        }
                    }
                }) {
                    try {
                        val principal = call.principal<JWTPrincipal>()
                            ?: throw IllegalStateException("Invalid JWT token")

                        val userId = principal.payload.subject.toLong()

                        logger.info("Deletion request received for user ID: $userId")

                        val user = userService.getUserById(userId)
                        if (user == null) {
                            logger.warn("Attempt to delete non-existent user: $userId")
                            call.respond(
                                HttpStatusCode.NotFound,
                                ErrorResponse(message = "User not found")
                            )
                            return@delete
                        }

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
