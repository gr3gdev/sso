package com.github.gr3gdev.sso.db

import com.github.gr3gdev.jserver.security.user.UserData

interface DAO<T : UserData> {

    fun save(elt: T)

    fun find(): Set<T>

}