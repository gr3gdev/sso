package com.github.gr3gdev.sso.db

import com.github.gr3gdev.jserver.security.user.UserData
import com.github.gr3gdev.sso.db.meta.Column

interface DAO<T : UserData> {

    fun createTable(name: String, columns: List<Column>) {
        var metaData = columns.map { it.line() }
        // Primary keys
        val pk = columns.filter { it.primaryKey }
        if (pk.isNotEmpty()) {
            metaData = metaData.plus("PRIMARY KEY (${pk.joinToString(",")})")
        }
        // Foreign keys
        val fk = columns.filter { it.foreignKey != null }.map { it.foreignKey!! }
        if (fk.isNotEmpty()) {
            fk.forEach {
                metaData =
                    metaData.plus("CONSTRAINT fk_${name}_${it.targetTableName}_${it.targetColumnName}\n FOREIGN KEY(${it.column})\n  REFERENCES ${it.targetTableName}(${it.targetColumnName})")
            }
        }
        DBManager.execute("CREATE TABLE IF NOT EXISTS $name (\n${metaData.joinToString(",\n")}\n)")
    }

    fun save(elt: T): Int

    fun find(): Set<T>

    fun delete(elt: T)

}