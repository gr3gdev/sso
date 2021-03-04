package com.github.gr3gdev.sso.dao

import com.github.gr3gdev.jdbc.dao.Queries
import com.github.gr3gdev.jdbc.dao.Query
import com.github.gr3gdev.jdbc.dao.QueryType
import com.github.gr3gdev.sso.bean.SsoClient

@Queries(mapTo = SsoClient::class)
interface ClientDAO {

    @Query(type = QueryType.INSERT)
    fun add(client: SsoClient)

    @Query(type = QueryType.UPDATE, attributes = [ "clientName", "addressIP" ])
    fun update(client: SsoClient): Int

    @Query(type = QueryType.DELETE, filters = ["id"])
    fun delete(id: Int): Int

    @Query(type = QueryType.SELECT)
    fun select(): Set<SsoClient>
}