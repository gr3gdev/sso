package com.github.gr3gdev.sso.service

import com.github.gr3gdev.jserver.security.password.BCryptPasswordManager
import com.github.gr3gdev.sso.bean.Clients
import com.github.gr3gdev.sso.dao.ClientDAO
import com.github.gr3gdev.sso.http.beans.ClientRequest

class ClientService : IService<ClientRequest, Clients> {

    private val dao = ClientDAO()
    private val passwordManager = BCryptPasswordManager(10)

    fun isAuthorized(clientName: String, clientSecret: String, ip: String): Boolean {
        var access = false
        dao.findByName(clientName).ifPresent {
            access = passwordManager.matches(clientSecret, it.clientSecret)
                    && it.remoteAddresses.contains(ip)
        }
        return access
    }

    override fun getDAO() = dao

    override fun map(dao: Clients): ClientRequest {
        val clientRequest = ClientRequest()
        clientRequest.clientName = dao.clientName
        clientRequest.authorized = false
        clientRequest.remoteAddresses.addAll(dao.remoteAddresses)
        return clientRequest
    }

}