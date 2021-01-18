package com.github.gr3gdev.sso.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.gr3gdev.jserver.security.user.UserData

class User : UserData {
    var username: String = ""
    @JsonIgnore
    var password: String = ""
}