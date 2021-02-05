package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.sso.bean.User
import com.github.gr3gdev.sso.db.UserDAO
import java.util.*

object UserService {

    private val userDAO = UserDAO()
    private val passwordManager = BCryptPasswordManager(10)

    fun findAll() = userDAO.find()

    fun <T> find(username: String?, password: String?, ifPresent: (user: User) -> T, orElse: () -> T): T {
        var res: T? = null
        Optional.ofNullable(userDAO.find()
            .firstOrNull {
                it.username == username && passwordManager.matches(password ?: "", it.password)
            }).ifPresentOrElse({
            res = ifPresent(it)
        }, {
            res = orElse()
        })
        return res!!
    }

    fun save(user: User): User {
        user.password = passwordManager.encode(user.password)
        user.id = userDAO.save(user)
        user.clients.forEach { roleByClient ->
            ClientService.findAll().filter { client -> client.clientName == roleByClient.key }.forEach {
                userDAO.saveClient(user, it, roleByClient.value)
            }
        }
        return user
    }

    fun createTables() {
        userDAO.createTable(User.TABLE_NAME, User.COLUMNS)
        userDAO.createTable(User.TABLE_NAME_LINK_CLIENT, User.COLUMNS_LINK_CLIENT)
    }

    fun delete(user: User) {
        userDAO.delete(user)
    }

}