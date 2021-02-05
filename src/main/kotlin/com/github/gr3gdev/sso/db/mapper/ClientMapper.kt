package com.github.gr3gdev.sso.db.mapper

import com.github.gr3gdev.sso.bean.Client

object ClientMapper {

    fun map(it: Map<String, String>): Client {
        val client = Client()
        client.id = (it[Client.COLUMNS[0].toString().toLowerCase()] ?: "0").toInt()
        client.clientName = it[Client.COLUMNS[1].toString().toLowerCase()] ?: ""
        client.clientSecret = it[Client.COLUMNS[2].toString().toLowerCase()] ?: ""
        client.addressIP = it[Client.COLUMNS[3].toString().toLowerCase()] ?: ""
        return client
    }

}