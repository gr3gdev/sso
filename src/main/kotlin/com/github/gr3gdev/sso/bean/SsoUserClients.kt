package com.github.gr3gdev.sso.bean

import com.github.gr3gdev.jdbc.metadata.Column
import com.github.gr3gdev.jdbc.metadata.Table

@Table
class SsoUserClients {

    @Column(primaryKey = true, sqlType = "INT")
    var userId: Int = 0

    @Column(primaryKey = true, sqlType = "INT")
    var clientId: Int = 0

    @Column(required = true, sqlType = "TEXT")
    var role: String = Role.USER.name

}