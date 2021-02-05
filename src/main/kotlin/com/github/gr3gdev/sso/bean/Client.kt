package com.github.gr3gdev.sso.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.gr3gdev.jserver.security.user.UserData
import com.github.gr3gdev.sso.db.meta.Column

class Client : UserData {

    var id: Int = 0
    var clientName: String = ""

    @JsonIgnore
    var clientSecret: String = ""

    @JsonIgnore
    var addressIP: String = ""

    override fun toString(): String {
        return "Client(clientName='$clientName')"
    }

    companion object {
        const val TABLE_NAME = "sso_client"
        val COLUMNS = listOf(
            Column("CLIENT_ID", "serial not null", true),
            Column("CLIENT_NAME", "text unique not null"),
            Column("CLIENT_SECRET", "text not null"),
            Column("CLIENT_IP", "text not null")
        )
    }

}