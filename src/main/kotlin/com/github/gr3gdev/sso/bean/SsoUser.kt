package com.github.gr3gdev.sso.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.gr3gdev.jdbc.metadata.Column
import com.github.gr3gdev.jdbc.metadata.Table
import com.github.gr3gdev.jserver.security.user.UserData

@Table
class SsoUser : UserData {

    @Column(primaryKey = true, autoincrement = true, autoincrementSyntax = "", sqlType = "SERIAL")
    var id: Int = 0

    @Column(required = true, sqlType = "TEXT")
    lateinit var username: String

    @Column(required = true, sqlType = "TEXT")
    var role: String = Role.USER.name

    @JsonIgnore
    @Column(required = true, sqlType = "TEXT")
    var password: String = ""

    @Column(required = true, sqlType = "BOOLEAN")
    var temporaryPassword: Boolean = false

    val clients: HashMap<String, Role> = HashMap()

    override fun toString(): String {
        return "User(id=$id, username='$username', role=$role, clients=$clients)"
    }

}