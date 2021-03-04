package com.github.gr3gdev.sso.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.gr3gdev.jdbc.metadata.Column
import com.github.gr3gdev.jdbc.metadata.Table
import com.github.gr3gdev.jserver.security.user.UserData

@Table
class SsoClient : UserData {

    @Column(primaryKey = true, sqlType = "SERIAL", autoincrement = true, autoincrementSyntax = "")
    var id: Int = 0

    @Column(required = true, sqlType = "TEXT")
    lateinit var clientName: String

    @JsonIgnore
    @Column(required = true, sqlType = "TEXT")
    lateinit var clientSecret: String

    @JsonIgnore
    @Column(required = true, sqlType = "TEXT")
    lateinit var addressIP: String

    override fun toString(): String {
        return "Client(clientName='$clientName')"
    }

}