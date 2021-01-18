package com.github.gr3gdev.sso.bean

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.gr3gdev.jserver.security.user.UserData

class Client : UserData {
    var clientName: String = ""
    @JsonIgnore
    var clientSecret: String = ""
    var addressIP: String = ""
}