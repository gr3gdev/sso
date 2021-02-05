package com.github.gr3gdev.sso.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.gr3gdev.jserver.security.user.UserData
import com.github.gr3gdev.sso.db.meta.Column
import com.github.gr3gdev.sso.db.meta.ForeignKey

class User : UserData {

    var id: Int = 0
    var username: String = ""
    var role: Role = Role.USER

    @JsonIgnore
    var password: String = ""
    val clients: HashMap<String, Role> = HashMap()

    override fun toString(): String {
        return "User(id=$id, username='$username', role=$role, clients=$clients)"
    }

    companion object {
        const val TABLE_NAME = "sso_user"
        val COLUMNS = listOf(
            Column("USER_ID", "serial not null", true),
            Column("USER_NAME", "text unique not null"),
            Column("USER_PASSWORD", "text not null"),
            Column("USER_ROLE", "text not null"),
        )
        const val TABLE_NAME_LINK_CLIENT = "sso_user_clients"
        val COLUMNS_LINK_CLIENT = listOf(
            Column(
                "USER_ID",
                "integer not null",
                true,
                ForeignKey("USER_ID", TABLE_NAME, COLUMNS[0])
            ),
            Column(
                "CLIENT_ID",
                "integer not null",
                true,
                ForeignKey("CLIENT_ID", Client.TABLE_NAME, Client.COLUMNS[0])
            ),
            Column("ROLE", "text not null")
        )
    }

}