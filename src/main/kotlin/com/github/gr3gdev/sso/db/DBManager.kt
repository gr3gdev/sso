package com.github.gr3gdev.sso.db

import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement

object DBManager {

    private lateinit var url: String
    private lateinit var username: String
    private lateinit var password: String

    enum class ParameterType(val set: (stm: PreparedStatement, idx: Int, value: Any) -> Unit) {
        INT({ stm: PreparedStatement, idx: Int, value: Any -> stm.setInt(idx, value as Int) }),
        STRING({ stm: PreparedStatement, idx: Int, value: Any -> stm.setString(idx, value as String) })
    }

    class Parameter(val type: ParameterType, val value: Any)

    fun init(url: String, username: String, password: String, initTables: () -> Unit) {
        this.url = url
        this.username = username
        this.password = password
        initTables()
    }

    private fun connection(): Connection {
        return DriverManager.getConnection(url, username, password)
    }

    fun execute(sql: String) {
        connection().use { cnx ->
            cnx.createStatement().use { stm ->
                stm.execute(sql)
            }
        }
    }

    fun insertOrUpdate(sql: String, vararg params: Parameter): Int {
        var id = 0
        connection().use { cnx ->
            cnx.prepareStatement(sql).use { stm ->
                setParameter(params, stm)
                if (sql.toUpperCase().contains("RETURNING")) {
                    stm.executeQuery().use {
                        if (it.next()) {
                            id = it.getInt(1)
                        }
                    }
                } else {
                    id = stm.executeUpdate()
                }
            }
        }
        return id
    }

    fun select(sql: String, vararg params: Parameter): List<Map<String, String>> {
        val results = ArrayList<Map<String, String>>()
        connection().use { cnx ->
            cnx.prepareStatement(sql).use { stm ->
                setParameter(params, stm)
                stm.executeQuery().use { res ->
                    val metadata = res.metaData
                    while (res.next()) {
                        val queryResult = HashMap<String, String>()
                        for (i in 1..metadata.columnCount) {
                            val columnName = metadata.getColumnName(i)
                            queryResult[columnName.toLowerCase()] = res.getString(columnName)
                        }
                        results.add(queryResult)
                    }
                }
            }
        }
        return results
    }

    private fun setParameter(
        params: Array<out Parameter>,
        stm: PreparedStatement
    ) {
        var idx = 1
        params.forEach { p ->
            p.type.set(stm, idx, p.value)
            idx++
        }
    }

}