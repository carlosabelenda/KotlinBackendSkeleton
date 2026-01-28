package com.decksolutions.kotlinbackendskeleton.config

import io.github.smiley4.ktoropenapi.OpenApi
import io.github.smiley4.ktoropenapi.openApi
import io.github.smiley4.ktorswaggerui.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureOpenApi() {
    install(OpenApi) {
        info {
            title = "KotlinBackendSkeleton API"
            version = "1.0.0"
            description = "REST API for authentication and user management"
        }

        security {
            securityScheme("JWT-Auth") {
                type = io.github.smiley4.ktoropenapi.config.AuthType.HTTP
                scheme = io.github.smiley4.ktoropenapi.config.AuthScheme.BEARER
                bearerFormat = "jwt"
                description = "Enter the JWT token obtained from the /api/auth/login endpoint"
            }
        }

        tags {
            tag("Health") {
                description = "System health status endpoints"
            }
            tag("Authentication") {
                description = "Authentication and user management endpoints"
            }
        }
    }

    routing {
        route("openapi.json") {
            openApi()
        }
        route("swagger") {
            swaggerUI("/openapi.json")
        }
    }
}
