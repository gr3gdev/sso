package com.github.gr3gdev.sso.dao

import com.github.gr3gdev.jdbc.dao.Queries
import com.github.gr3gdev.jdbc.dao.Query
import com.github.gr3gdev.jdbc.dao.QueryType
import com.github.gr3gdev.sso.bean.SsoUser

@Queries(mapTo = SsoUser::class)
interface UserDAO {

    @Query(type = QueryType.INSERT)
    fun add(user: SsoUser)

    @Query(type = QueryType.UPDATE, attributes = ["temporaryPassword"])
    fun update(user: SsoUser): Int

    @Query(type = QueryType.DELETE, filters = ["id"])
    fun delete(id: Int): Int

    @Query(type = QueryType.SELECT)
    fun select(): Set<SsoUser>

    @Query(type = QueryType.SELECT, filters = ["username"])
    fun findByName(username: String): SsoUser

}