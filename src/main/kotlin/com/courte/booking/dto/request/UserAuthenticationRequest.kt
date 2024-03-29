package com.courte.booking.dto.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserAuthenticationRequest(
    @field:Email
    @field:NotBlank
    val email: String,
    @field:NotBlank
    val password: String,
)