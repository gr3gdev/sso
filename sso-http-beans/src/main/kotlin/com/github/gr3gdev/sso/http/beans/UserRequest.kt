package com.github.gr3gdev.sso.http.beans

import com.github.gr3gdev.jserver.security.user.UserData

class UserRequest : UserData {

    lateinit var username: String

    var authorized: Boolean = false

}