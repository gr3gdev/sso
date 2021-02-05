package com.github.gr3gdev.sso.db

import com.github.gr3gdev.sso.bean.Client
import com.github.gr3gdev.sso.db.mapper.ClientMapper

class ClientDAO : DAO<Client> {

    override fun save(elt: Client): Int {
        if (elt.id == 0) {
            return DBManager.insertOrUpdate(
                "INSERT INTO ${Client.TABLE_NAME} " +
                        "(${Client.COLUMNS[1]}, ${Client.COLUMNS[2]}, ${Client.COLUMNS[3]}) " +
                        "VALUES (?,?,?) RETURNING ${Client.COLUMNS[0]}",
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.clientName),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.clientSecret),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.addressIP)
            )
        } else {
            return DBManager.insertOrUpdate(
                "UPDATE ${Client.TABLE_NAME} SET " +
                        "${Client.COLUMNS[1]} = ?, ${Client.COLUMNS[2]} = ?, ${Client.COLUMNS[3]} = ? " +
                        "WHERE ${Client.COLUMNS[0]} = ?",
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.clientName),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.clientSecret),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.addressIP),
                DBManager.Parameter(DBManager.ParameterType.INT, elt.id)
            )
        }
    }

    override fun find(): Set<Client> {
        val clients = HashSet<Client>()
        DBManager.select("SELECT * FROM ${Client.TABLE_NAME}").forEach {
            clients.add(ClientMapper.map(it))
        }
        return clients
    }

    override fun delete(elt: Client) {
        DBManager.insertOrUpdate(
            "DELETE FROM ${Client.TABLE_NAME} WHERE ${Client.COLUMNS[0]} = ?",
            DBManager.Parameter(DBManager.ParameterType.INT, elt.id)
        )
    }

}