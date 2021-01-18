package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.sso.bean.User
import com.github.gr3gdev.sso.db.UserDAO
import java.util.*

class UserService {

    private val userDAO = UserDAO()
    private val passwordManager = BCryptPasswordManager(10)

    fun find(username: String?, password: String?): Optional<User> {
        return Optional.ofNullable(userDAO.find()
            .firstOrNull {
                it.username == username && passwordManager.matches(password ?: "", it.password)
            })
    }

}