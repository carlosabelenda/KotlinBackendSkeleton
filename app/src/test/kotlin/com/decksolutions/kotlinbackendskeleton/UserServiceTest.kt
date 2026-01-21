package com.decksolutions.kotlinbackendskeleton

import com.decksolutions.kotlinbackendskeleton.models.entities.User
import com.decksolutions.kotlinbackendskeleton.repositories.UserRepository
import com.decksolutions.kotlinbackendskeleton.services.PasswordService
import com.decksolutions.kotlinbackendskeleton.services.UserService

import io.mockk.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var passwordService: PasswordService
    private lateinit var userService: UserService

    @Before
    fun setup() {
        userRepository = mockk()
        passwordService = PasswordService()
        userService = UserService(userRepository, passwordService)
    }

    @Test
    fun `test registerUser check empty email`() {
        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("", "username", "password123")
        }
    }

    @Test
    fun `test registerUser check invalid email`() {
        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("invalid-email", "username", "password123")
        }
    }

    @Test
    fun `test registerUser check empty username`() {
        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("test@example.com", "", "password123")
        }
    }

    @Test
    fun `test registerUser check short username`() {
        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("test@example.com", "ab", "password123")
        }
    }

    @Test
    fun `test registerUser check invalid characters username`() {
        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("test@example.com", "user-name", "password123")
        }
    }

    @Test
    fun `test registerUser check empty password`() {
        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("test@example.com", "username", "")
        }
    }

    @Test
    fun `test registerUser check short password`() {
        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("test@example.com", "username", "12345")
        }
    }

    @Test
    fun `test registerUser if email exist triggers an exception`() {
        every { userRepository.emailExists("test@example.com") } returns true

        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("test@example.com", "username", "password123")
        }
    }

    @Test
    fun `test registerUser if username already exists then triggers an exception`() {
        every { userRepository.emailExists("test@example.com") } returns false
        every { userRepository.usernameExists("username") } returns true

        assertFailsWith<IllegalArgumentException> {
            userService.registerUser("test@example.com", "username", "password123")
        }
    }

    @Test
    fun `test registerUser user is created successfully`() {
        val email = "test@example.com"
        val username = "testuser"
        val password = "password123"

        every { userRepository.emailExists(email) } returns false
        every { userRepository.usernameExists(username) } returns false
        every {
            userRepository.create(any(), any(), any())
        } returns User(
            id = 1L,
            email = email,
            username = username,
            passwordHash = password,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val user = userService.registerUser(email, username, password)

        assertNotNull(user)
        verify(exactly = 1) { userRepository.create(any(), any(), any()) }
    }
}