package com.github.gr3gdev.sso.db

import com.github.gr3gdev.sso.bean.Client
import com.github.gr3gdev.sso.bean.Role
import com.github.gr3gdev.sso.bean.User
import com.github.gr3gdev.sso.db.mapper.UserMapper

class UserDAO : DAO<User> {

    override fun save(elt: User): Int {
        if (elt.id == 0) {
            return DBManager.insertOrUpdate(
                "INSERT INTO ${User.TABLE_NAME} (${User.COLUMNS[1]}, ${User.COLUMNS[2]}, ${User.COLUMNS[3]}) VALUES (?,?,?) " +
                        "RETURNING ${User.COLUMNS[0]}",
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.username),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.password),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.role.name)
            )
        } else {
            return DBManager.insertOrUpdate(
                "UPDATE ${User.TABLE_NAME} SET " +
                        "${User.COLUMNS[1]} = ?, ${User.COLUMNS[2]} = ?, ${User.COLUMNS[3]} = ? " +
                        "WHERE ${User.COLUMNS[0]} = ?",
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.username),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.password),
                DBManager.Parameter(DBManager.ParameterType.STRING, elt.role.name),
                DBManager.Parameter(DBManager.ParameterType.INT, elt.id)
            )
        }
    }

    override fun find(): Set<User> {
        val users = HashSet<User>()
        DBManager.select("SELECT * FROM ${User.TABLE_NAME}").forEach { u ->
            val user = UserMapper.map(u)
            DBManager.select(
                "SELECT c.*, uc.${User.COLUMNS_LINK_CLIENT[2]} FROM ${User.TABLE_NAME_LINK_CLIENT} uc " +
                        "INNER JOIN ${Client.TABLE_NAME} c ON c.${Client.COLUMNS[0]} = uc.${User.COLUMNS_LINK_CLIENT[1]} " +
                        "WHERE uc.${User.COLUMNS_LINK_CLIENT[0]}=?",
                DBManager.Parameter(DBManager.ParameterType.INT, user.id)
            ).forEach { uc ->
                user.clients[uc.getValue(Client.COLUMNS[1].toString().toLowerCase())] =
                    Role.valueOf(uc.getValue(User.COLUMNS_LINK_CLIENT[2].toString().toLowerCase()))
            }
            users.add(user)
        }
        return users
    }

    fun saveClient(user: User, client: Client, role: Role): Int {
        return DBManager.insertOrUpdate(
            "INSERT INTO ${User.TABLE_NAME_LINK_CLIENT} " +
                    "(${User.COLUMNS_LINK_CLIENT[0]}, ${User.COLUMNS_LINK_CLIENT[1]}, ${User.COLUMNS_LINK_CLIENT[2]}) " +
                    "VALUES (?,?,?)",
            DBManager.Parameter(DBManager.ParameterType.INT, user.id),
            DBManager.Parameter(DBManager.ParameterType.INT, client.id),
            DBManager.Parameter(DBManager.ParameterType.STRING, role.name)
        )
    }

    override fun delete(elt: User) {
        DBManager.insertOrUpdate(
            "DELETE FROM ${User.TABLE_NAME_LINK_CLIENT} WHERE ${User.COLUMNS_LINK_CLIENT[0]} = ?",
            DBManager.Parameter(DBManager.ParameterType.INT, elt.id)
        )
        DBManager.insertOrUpdate(
            "DELETE FROM ${User.TABLE_NAME} WHERE ${User.COLUMNS[0]} = ?",
            DBManager.Parameter(DBManager.ParameterType.INT, elt.id)
        )
    }

}