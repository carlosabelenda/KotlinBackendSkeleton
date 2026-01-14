package com.decksolutions.kotlinbackendskeleton.services

import com.decksolutions.kotlinbackendskeleton.models.entities.User
import com.decksolutions.kotlinbackendskeleton.repositories.UserRepository
import org.slf4j.LoggerFactory

/**
 * Servicio de autenticación
 */
class AuthService(
    private val userRepository: UserRepository,
    private val passwordService: PasswordService,
    private val jwtService: JWTService
) {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    /**
     * Autentica un usuario y genera un token JWT
     *
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     * @return Par con el usuario autenticado y el token JWT
     * @throws IllegalArgumentException Si las credenciales son inválidas
     */
    fun authenticate(email: String, password: String): Pair<User, String> {
        logger.info("Intentando autenticar usuario: $email")

        // Buscar usuario por email
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("Credenciales inválidas")

        // Obtener hash de la contraseña
        val passwordHash = userRepository.getPasswordHashByEmail(email)
            ?: throw IllegalArgumentException("Credenciales inválidas")

        // Verificar contraseña
        if (!passwordService.verifyPassword(password, passwordHash)) {
            throw IllegalArgumentException("Credenciales inválidas")
        }

        // Generar token JWT
        val token = jwtService.generateToken(user.id, user.email)

        logger.info("Usuario autenticado correctamente: ${user.email}")

        return Pair(user, token)
    }
}