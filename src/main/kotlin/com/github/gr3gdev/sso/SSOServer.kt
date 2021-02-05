package com.github.gr3gdev.sso

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.gr3gdev.jserver.JServer
import com.github.gr3gdev.jserver.logger.Logger
import com.github.gr3gdev.jserver.security.TokenManager
import com.github.gr3gdev.sso.bean.Client
import com.github.gr3gdev.sso.bean.Role
import com.github.gr3gdev.sso.bean.User
import com.github.gr3gdev.sso.db.DBManager
import com.github.gr3gdev.sso.pages.SSOAdminPage
import com.github.gr3gdev.sso.pages.SSOPage
import com.github.gr3gdev.sso.service.ClientService
import com.github.gr3gdev.sso.service.UserService

private val jsonMapper = jacksonObjectMapper()
private val tokenManager = TokenManager.issuer("GR3Gdev_MySSO").generateSecretKey(256)

private val ssoPage = SSOPage(jsonMapper, tokenManager)
private val ssoAdminPage = SSOAdminPage(jsonMapper, tokenManager)

fun main(vararg args: String) {
    Logger.changeLevel(Logger.Level.INFO)
    if (args.size == 3) {
        DBManager.init(args[0], args[1], args[2]) {
            ClientService.createTables()
            UserService.createTables()
        }
        initTests()
        // TODO init admin
        JServer.server().port(3000)
            .get("/static/{file}", ssoPage.static("/static"))
            .post("/access-token", ssoPage.clientAccess())
            .post("/login", ssoPage.login())
            .post("/admin/auth", ssoAdminPage.auth())
            .get("/admin/clients", ssoAdminPage.getClients())
            .put("/admin/clients", ssoAdminPage.saveClient())
            .get("/admin/users", ssoAdminPage.getUsers())
            .put("/admin/users", ssoAdminPage.saveUser())
            .get("/{redirect}", ssoPage.home())
            .start()
    } else {
        Logger.error("Missing database configuration")
    }
}

fun initTests() {

    UserService.findAll().forEach {
        Logger.debug("Remove $it")
        UserService.delete(it)
    }
    ClientService.findAll().forEach {
        Logger.debug("Remove $it")
        ClientService.delete(it)
    }

    val admin = User()
    admin.username = "admin"
    admin.password = "password"
    admin.role = Role.ADMIN
    UserService.save(admin)

    for (i in 0 until 10) {
        val client = Client()
        client.clientName = "client$i"
        client.clientSecret = "abcdefghijklmnopqrstuvwxyz"
        client.addressIP = "127.0.0.1"
        ClientService.save(client)

        val user = User()
        user.username = "user$i"
        user.password = "password"
        user.clients[client.clientName] = Role.USER
        UserService.save(user)
    }

}
