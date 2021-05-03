package com.github.gr3gdev.sso.dao

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Connection
import java.util.*

object DataSource {

    private lateinit var ds: HikariDataSource

    fun init(configFile: String) {
        FileInputStream(configFile).use {
            val props = Properties()
            props.load(it)
            val config = HikariConfig(props)
            ds = HikariDataSource(config)
        }
        val sql = Files.readString(Paths.get(DataSource::class.java.getResource("/database/create_tables.sql").toURI()))
        ds.connection.use { cnx ->
            cnx.createStatement().use { stm ->
                stm.execute(sql)
            }
        }
    }

    fun getConnection(): Connection = ds.connection

}