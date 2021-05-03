package com.github.gr3gdev.sso.bean

import java.time.LocalDateTime

class UsersToken {

    lateinit var username: String

    lateinit var token: String

    lateinit var expireDate: LocalDateTime

}