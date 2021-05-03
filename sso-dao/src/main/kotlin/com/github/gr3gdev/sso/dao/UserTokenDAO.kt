package com.github.gr3gdev.sso.dao

import com.github.gr3gdev.sso.bean.UsersToken
import java.sql.ResultSet

class UserTokenDAO : AbstractDAO<UsersToken>() {

    override fun map(res: ResultSet): UsersToken {
        val usersToken = UsersToken()
        usersToken.username = res.getString("username")
        usersToken.token = res.getString("token")
        usersToken.expireDate = res.getTimestamp("expire_date").toLocalDateTime()
        return usersToken
    }

    override fun tableName() = "users_token"

    override fun columnNameForName() = "username"
}