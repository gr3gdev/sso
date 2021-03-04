package com.github.gr3gdev.sso

import com.github.gr3gdev.jdbc.JDBC
import com.github.gr3gdev.jdbc.JdbcConf
import com.github.gr3gdev.jserver.JServer
import com.github.gr3gdev.jserver.logger.Logger
import com.github.gr3gdev.sso.bean.Role
import com.github.gr3gdev.sso.bean.SsoUser
import com.github.gr3gdev.sso.jdbc.JDBCFactory
import com.github.gr3gdev.sso.pages.SSOAdminPage
import com.github.gr3gdev.sso.pages.SSOPage
import com.github.gr3gdev.sso.service.UserService

@JDBC([JdbcConf(configFile = "/database.properties")])
class SSOServer

fun main() {
    Logger.changeLevel(Logger.Level.INFO)
    JDBCFactory.init()
    initAdmin()
    JServer.server().port(3000)
        .get("/static/{file}", SSOPage.static("/static"))
        .post("/access-token", SSOPage.clientAccess())
        .post("/login", SSOPage.login())
        .post("/admin/auth", SSOAdminPage.auth())
        .get("/admin/clients", SSOAdminPage.getClients())
        .put("/admin/clients", SSOAdminPage.saveClient())
        .get("/admin/users", SSOAdminPage.getUsers())
        .put("/admin/users", SSOAdminPage.saveUser())
        .get("/{redirect}", SSOPage.home())
        .start()
}

fun initAdmin() {
    UserService.findByName("admin", {
        if (it.temporaryPassword) {
            Logger.warn("admin password must be changed")
        }
    }, {
        // Add admin account
        val admin = SsoUser()
        admin.username = "admin"
        admin.password = "mySSOp@ssw0rd"
        admin.role = Role.ADMIN.name
        admin.temporaryPassword = true
        UserService.save(admin)
    })
}
