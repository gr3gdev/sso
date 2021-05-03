package com.github.gr3gdev.sso.dao

import com.github.gr3gdev.sso.bean.Clients
import java.sql.ResultSet

class ClientDAO : AbstractDAO<Clients>() {

    override fun map(res: ResultSet): Clients {
        val client = Clients()
        client.clientName = res.getString("client_name")
        client.clientSecret = res.getString("client_secret")
        client.remoteAddresses.addAll(res.getString("remote_addresses").split(","))
        return client
    }

    override fun tableName() = "clients"

    override fun columnNameForName() = "client_name"

}