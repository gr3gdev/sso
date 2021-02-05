package com.github.gr3gdev.sso.db.meta

class Column(private val name: String, private val constraints: String, val primaryKey: Boolean = false, val foreignKey: ForeignKey? = null) {

    fun line(): String = "$name $constraints"

    override fun toString(): String {
        return name
    }
}