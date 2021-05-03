package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.jserver.security.user.JwtData
import com.github.gr3gdev.sso.bean.Users
import com.github.gr3gdev.sso.dao.UserDAO
import com.github.gr3gdev.sso.dao.UserTokenDAO
import com.github.gr3gdev.sso.http.beans.UserRequest
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class UserService : IService<UserRequest, Users> {

    private val dao = UserDAO()
    private val tokenDao = UserTokenDAO()
    private val passwordManager = BCryptPasswordManager(10)

    override fun getDAO() = dao

    override fun map(dao: Users): UserRequest {
        val userRequest = UserRequest()
        userRequest.username = dao.username
        return userRequest
    }

    fun isTokenValid(token: String, jwtData: JwtData<UserRequest>): Boolean {
        var valid = false
        tokenDao.findByName(jwtData.data.username).ifPresent {
            valid = it.token == token
                    && it.expireDate.isAfter(LocalDateTime.now())
                    && jwtData.expiration.isAfter(Instant.now())
        }
        return valid
    }

    fun findByAuthorizedUser(clientName: String, username: String, password: String): Optional<UserRequest> {
        var res = Optional.empty<UserRequest>()
        dao.findUserAuthorizedOnClient(username, clientName).ifPresent {
            val userRequest = map(it)
            userRequest.authorized = passwordManager.matches(password, it.password)
            res = Optional.of(userRequest)
        }
        return res
    }

    fun findAdminUser(username: String, password: String): Optional<UserRequest> {
        var res = Optional.empty<UserRequest>()
        dao.findUserByRole(username, "ADMIN").ifPresent {
            val userRequest = map(it)
            userRequest.authorized = passwordManager.matches(password, it.password)
            res = Optional.of(userRequest)
        }
        return res
    }

}