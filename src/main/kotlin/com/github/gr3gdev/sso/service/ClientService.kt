package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.sso.bean.Client
import com.github.gr3gdev.sso.db.ClientDAO
import java.util.*

class ClientService {

    private val clientDAO = ClientDAO()
    private val passwordManager = BCryptPasswordManager(10)

    fun find(clientName: String?, clientSecret: String?, addressIP: String?): Optional<Client> {
        return Optional.ofNullable(clientDAO.find()
            .firstOrNull {
                it.clientName == clientName
                        && it.addressIP == addressIP
                        && passwordManager.matches(clientSecret!!, it.clientSecret)
            })
    }

}