package com.github.gr3gdev.sso.dao

import com.github.gr3gdev.sso.bean.Users
import java.sql.ResultSet
import java.util.*

class UserDAO : AbstractDAO<Users>() {

    override fun map(res: ResultSet): Users {
        val user = Users()
        user.username = res.getString("username")
        user.password = res.getString("password")
        user.enabled = res.getBoolean("enabled")
        return user
    }

    override fun tableName() = "users"

    override fun columnNameForName() = "username"

    fun findUserAuthorizedOnClient(username: String, clientName: String): Optional<Users> {
        DataSource.getConnection().use { cnx ->
            cnx.prepareStatement("SELECT u.* FROM users u INNER JOIN authorities a ON a.username = u.username WHERE u.username = ? AND a.client_name = ?")
                .use { stm ->
                    stm.setString(1, username)
                    stm.setString(2, clientName)
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

    fun findUserByRole(username: String, role: String): Optional<Users> {
        DataSource.getConnection().use { cnx ->
            cnx.prepareStatement("SELECT u.* FROM users u INNER JOIN authorities a ON a.username = u.username WHERE u.username = ? AND a.role = ?")
                .use { stm ->
                    stm.setString(1, username)
                    stm.setString(2, role)
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