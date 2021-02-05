package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.sso.bean.Client
import com.github.gr3gdev.sso.db.ClientDAO
import java.util.*

object ClientService {

    private val clientDAO = ClientDAO()
    private val passwordManager = BCryptPasswordManager(10)

    fun findAll() = clientDAO.find()

    fun <T> find(
        clientName: String?,
        clientSecret: String?,
        addressIP: String?,
        ifPresent: (client: Client) -> T,
        orElse: () -> T
    ): T {
        var res: T? = null
        Optional.ofNullable(clientDAO.find()
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

    fun save(client: Client): Client {
        client.id = clientDAO.save(client)
        return client
    }

    fun createTables() {
        clientDAO.createTable(Client.TABLE_NAME, Client.COLUMNS)
    }

    fun delete(client: Client) {
        clientDAO.delete(client)
    }

}