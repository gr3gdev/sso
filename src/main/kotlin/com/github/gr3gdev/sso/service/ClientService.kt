package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.sso.bean.SsoClient
import com.github.gr3gdev.sso.jdbc.JDBCFactory
import java.util.*

object ClientService {

    private val clientDAO = JDBCFactory.getClientDAO()
    private val passwordManager = BCryptPasswordManager(10)

    fun findAll() = clientDAO.select()

    fun <T> find(
        clientName: String?,
        clientSecret: String?,
        addressIP: String?,
        ifPresent: (client: SsoClient) -> T,
        orElse: () -> T
    ): T {
        var res: T? = null
        Optional.ofNullable(clientDAO.select()
            .firstOrNull {
                it.clientName == clientName
                        && it.addressIP == addressIP
                        && passwordManager.matches(clientSecret!!, it.clientSecret)
            }).ifPresentOrElse({
            res = ifPresent(it)
        }, {
            res = orElse()
        })
        return res!!
    }

    fun save(client: SsoClient): SsoClient {
        if (client.id > 0) {
            clientDAO.update(client)
        } else {
            clientDAO.add(client)
        }
        return client
    }

    fun delete(client: SsoClient) {
        clientDAO.delete(client.id)
    }

}