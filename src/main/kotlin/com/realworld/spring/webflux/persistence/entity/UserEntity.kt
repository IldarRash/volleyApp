package com.realworld.spring.webflux.persistence.entity

import com.realworld.spring.webflux.dto.User
import org.apache.commons.lang3.StringUtils
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import reactor.core.publisher.Mono
import java.util.*

enum class Role {
    User, Admin
}

enum class Gender {
    Male, Female, Other
}

@Document("app_user")
data class UserEntity(

        @Id val id: UUID?,
        val username: String,
        val encodedPassword: String,
        val email: String,
        val bio: String? = null,
        val image: String? = null,
        val roles: List<Role> = listOf(Role.User),
        val player: Player,
        val gender: Gender
) {
    fun toUser() = User(
       id = this.id,
       username = this.username,
       encodedPassword = this.encodedPassword,
       email = this.email,
       bio = this.bio,
       image = this.image,
       gender = gender,
       player = player
    )
}
