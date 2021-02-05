package com.github.gr3gdev.sso.db.mapper

import com.github.gr3gdev.sso.bean.Role
import com.github.gr3gdev.sso.bean.User

object UserMapper {

    fun map(it: Map<String, String>): User {
        val user = User()
        user.id = (it[User.COLUMNS[0].toString().toLowerCase()] ?: "0").toInt()
        user.username = it[User.COLUMNS[1].toString().toLowerCase()] ?: ""
        user.password = it[User.COLUMNS[2].toString().toLowerCase()] ?: ""
        user.role = Role.valueOf(it[User.COLUMNS[3].toString().toLowerCase()] ?: Role.USER.name)
        return user
    }

}