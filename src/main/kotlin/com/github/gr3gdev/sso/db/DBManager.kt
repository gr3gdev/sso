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

    class Parameter(val type: ParameterType, val value: String)

    fun init(url: String, username: String, password: String) {
        this.url = url
        this.username = username
        this.password = password
    }

    private fun connection(): Connection {
        return DriverManager.getConnection(url, username, password)
    }

    fun select(sql: String, vararg params: Parameter): List<Map<String, String>> {
        val results = ArrayList<Map<String, String>>()
        connection().use { cnx ->
            cnx.prepareStatement(sql).use { stm ->
                var idx = 0
                params.forEach { p ->
                    p.type.set(stm, idx, p.value)
                    idx++
                }
                stm.executeQuery().use { res ->
                    val metadata = res.metaData
                    while (res.next()) {
                        val queryResult = HashMap<String, String>()
                        for (i in 0 until metadata.columnCount) {
                            val columnName = metadata.getColumnName(i)
                            queryResult[columnName] = res.getString(columnName)
                        }
                        results.add(queryResult)
                    }
                }
            }
        }
        return results
    }

}