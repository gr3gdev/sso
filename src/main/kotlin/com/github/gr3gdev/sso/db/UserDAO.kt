package com.github.gr3gdev.sso.db

import com.github.gr3gdev.sso.bean.User

class UserDAO : DAO<User> {

    override fun save(elt: User) {
        TODO("Not yet implemented")
    }

    override fun find(): Set<User> {
        val users = HashSet<User>()
        DBManager.select("SELECT * FROM user").forEach {
            val user = User()
            user.username = it["USER_NAME"] ?: ""
            user.password = it["USER_PASSWORD"] ?: ""
            users.add(user)
        }
        return users
    }

}