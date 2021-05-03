package com.github.gr3gdev.sso.dao

import java.sql.ResultSet
import java.util.*

abstract class AbstractDAO<D> {

    abstract fun map(res: ResultSet): D

    abstract fun tableName(): String

    abstract fun columnNameForName(): String

    fun findAll(): Set<D> {
        DataSource.getConnection().use { cnx ->
            cnx.createStatement().use { stm ->
                stm.executeQuery("SELECT * FROM ${tableName()}").use { res ->
                    val list = LinkedHashSet<D>()
                    while (res.next()) {
                        list.add(map(res))
                    }
                    return list
                }
            }
        }
    }

    fun findByName(name: String): Optional<D> {
        DataSource.getConnection().use { cnx ->
            cnx.prepareStatement("SELECT * FROM ${tableName()} WHERE ${columnNameForName()} = ?").use { stm ->
                stm.setString(1, name)
                stm.executeQuery().use { res ->
                    return if (res.next()) {
                        Optional.of(map(res))
                    } else {
                        Optional.empty()
                    }
                }
            }
        }
    }
}