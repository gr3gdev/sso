package com.github.gr3gdev.sso.dao

import com.github.gr3gdev.jdbc.dao.Queries
import com.github.gr3gdev.jdbc.dao.Query
import com.github.gr3gdev.jdbc.dao.QueryType
import com.github.gr3gdev.sso.bean.SsoUserClients

@Queries(mapTo = SsoUserClients::class)
interface UserClientsDAO {

    @Query(type = QueryType.INSERT)
    fun add(userClients: SsoUserClients)

}