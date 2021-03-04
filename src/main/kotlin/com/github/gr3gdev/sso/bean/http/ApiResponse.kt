package com.github.gr3gdev.sso.bean.http

import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.security.user.UserData

class ApiResponse {
    var status: HttpStatus = HttpStatus.OK
    var message: String = ""
    var data: UserData? = null
}