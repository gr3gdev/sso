package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.sso.bean.SsoUser
import com.github.gr3gdev.sso.bean.SsoUserClients
import com.github.gr3gdev.sso.jdbc.JDBCFactory
import java.util.*

object UserService {

    private val userDAO = JDBCFactory.getUserDAO()
    private val userClientsDAO = JDBCFactory.getUserClientsDAO()
    private val passwordManager = BCryptPasswordManager(10)

    fun findAll() = userDAO.select()

    fun findByName(username: String, ifPresent: (user: SsoUser) -> Unit, orElse: () -> Unit) {
        val user = userDAO.findByName(username)
        if (user.id > 0) {
            ifPresent(user)
        } else {
            orElse()
        }
    }

    fun <T> find(username: String?, password: String?, ifPresent: (user: SsoUser) -> T, orElse: () -> T): T {
        var res: T? = null
        Optional.ofNullable(userDAO.select()
            .firstOrNull {
                it.username == username && passwordManager.matches(password ?: "", it.password)
            }).ifPresentOrElse({
            res = ifPresent(it)
        }, {
            res = orElse()
        })
        return res!!
    }

    fun save(user: SsoUser): SsoUser {
        user.password = passwordManager.encode(user.password)
        userDAO.add(user)
        user.clients.forEach { roleByClient ->
            ClientService.findAll().filter { client -> client.clientName == roleByClient.key }.forEach {
                val userClients = SsoUserClients()
                userClients.userId = user.id
                userClients.clientId = it.id
                userClients.role = roleByClient.value.name
                userClientsDAO.add(userClients)
            }
        }
        return user
    }

    fun delete(user: SsoUser) {
        userDAO.delete(user.id)
    }

}