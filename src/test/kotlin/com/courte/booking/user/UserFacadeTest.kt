package com.courte.booking.user

import com.courte.booking.dto.User
import com.courte.booking.exceptions.InvalidRequestException
import com.courte.booking.persistence.repository.UserDataService
import com.courte.booking.persistence.repository.UserRepository
import com.courte.booking.security.JwtSigner
import com.courte.booking.service.user.PasswordService
import com.courte.booking.service.user.SecuredUserService
import com.courte.booking.service.user.UserService
import com.realworld.spring.webflux.user.UserSession
import helpers.UserSamples
import helpers.coCatchThrowable
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.kotlin.core.publisher.toMono
import java.util.UUID

class UserFacadeTest {
    companion object {
        val passwordService = PasswordService()
        val userRepository = mockk<UserRepository>()
        val userDataService = UserDataService(userRepository)
        val securedUserService = SecuredUserService(
            userDataService = userDataService,
            passwordService = passwordService,
            userTokenProvider = JwtSigner(),
        )
        val userService = UserService(
            userDataService = userDataService,
            securedUserService = securedUserService,
        )

    }

    @Test
    fun `should throw error when signup with duplicate email`() {
        every { userRepository.existsByEmail(any()) } returns true.toMono()
        every { userRepository.existsByUsername(any()) } returns false.toMono()

        val throwable = coCatchThrowable { userService.signup(UserSamples.sampleUserRegistrationRequest()) }

        assertThat(throwable)
            .isInstanceOf(InvalidRequestException::class.java)
            .hasMessage("Email: already in use")
    }


    @Test
    fun `should throw error when signup with duplicate username`() {
        every { userRepository.existsByUsername(any()) } returns true.toMono()
        every { userRepository.existsByEmail(any()) } returns false.toMono()

        val throwable = coCatchThrowable { userService.signup(UserSamples.sampleUserRegistrationRequest()) }

        assertThat(throwable)
            .isInstanceOf(InvalidRequestException::class.java)
            .hasMessage("Username: already in use")
    }

    @Test
    fun `should throw error when login with unregistered user`() {
        every { userRepository.findByEmail(any()) } returns emailNotFoundException().map { it.toUserEntity() }

        val throwable = coCatchThrowable { userService.login(UserSamples.sampleUserAuthenticationRequest()) }

        assertThat(throwable)
            .isInstanceOf(InvalidRequestException::class.java)
            .hasMessage("Email: not found")
    }

    @Test
    fun `should throw error when wrong password`() = runTest{
        every { userRepository.findByEmail(any()) } returns UserSamples.sampleUser().toUserEntity().toMono()

        val authRequest = UserSamples.sampleUserAuthenticationRequest().copy(password = "not default password")
        val throwable = coCatchThrowable { userService.login(authRequest) }

        assertThat(throwable)
            .isInstanceOf(InvalidRequestException::class.java)
            .hasMessage("Password: invalid")
    }

    @Test
    fun `should throw error when update user with duplicate email`() {
        every { userRepository.existsByEmail(any()) } returns true.toMono()
        every { userRepository.existsByUsername(any()) } returns false.toMono()
        val user = UserSamples.sampleUser()
        every { userRepository.findById(any<UUID>()) } returns user.toUserEntity().toMono()

        val updateRequest = UserSamples.sampleUpdateUserRequest()
        val throwable = coCatchThrowable { userService.updateUser(updateRequest, UserSession(user, "token")) }

        assertThat(throwable)
            .isInstanceOf(InvalidRequestException::class.java)
            .hasMessage("Email: already in use")
    }

    @Test
    fun `should throw error when update user with duplicate username`() {
        every { userRepository.existsByEmail(any()) } returns false.toMono()
        every { userRepository.existsByUsername(any()) } returns true.toMono()
        val user = UserSamples.sampleUser()
        every { userRepository.findById(any<UUID>()) } returns user.toUserEntity().toMono()

        val updateRequest = UserSamples.sampleUpdateUserRequest()
        val throwable = coCatchThrowable { userService.updateUser(updateRequest, UserSession(user, "token")) }

        assertThat(throwable)
            .isInstanceOf(InvalidRequestException::class.java)
            .hasMessage("Username: already in use")
    }

    private fun emailNotFoundException() = InvalidRequestException("Email", "not found").toMono<User>()
}