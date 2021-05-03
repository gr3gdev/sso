package com.github.gr3gdev.sso.route

import com.github.gr3gdev.jserver.route.Response
import com.github.gr3gdev.jserver.route.RouteListener
import com.github.gr3gdev.jserver.security.TokenClientPlugin
import com.github.gr3gdev.jserver.security.http.TokenRequest
import com.github.gr3gdev.jserver.thymeleaf.ThymeleafPlugin
import com.github.gr3gdev.sso.http.beans.UserRequest
import com.github.gr3gdev.sso.service.ClientService
import com.github.gr3gdev.sso.service.UserService

class AdminRoute : AuthRoute("MySSOAdmin", 3600L * 500L) {

    private val userService = UserService()
    private val clientService = ClientService()

    override fun get() = RouteListener().process { route ->
        val thymeleafPlugin = route.plugin(ThymeleafPlugin::class.java)
        var res = redirectLogin(thymeleafPlugin)
        TokenRequest.getTokenFromCookie(route.request, cookieName).ifPresent { token ->
            // Already connected
            route.plugin(TokenClientPlugin::class.java).getUserData(token, UserRequest::class.java).ifPresent { jwt ->
                if (userService.isTokenValid(token, jwt)) {
                    res = admin(thymeleafPlugin)
                }
            }
        }
        res
    }

    override fun post() = RouteListener().process { route ->
        val thymeleafPlugin = route.plugin(ThymeleafPlugin::class.java)
        var res = redirectLogin(thymeleafPlugin)
        val username = getParam(route, "username")
        val password = getParam(route, "password")
        if (username.isNotBlank() && password.isNotBlank()) {
            userService.findAdminUser(username, password).ifPresent {
                if (it.authorized) {
                    res = admin(thymeleafPlugin)
                }
            }
        }
        res
    }

    private fun admin(thymeleafPlugin: ThymeleafPlugin): Response {
        val users = userService.findAll()
        val clients = clientService.findAll()
        return thymeleafPlugin.process(
            "admin",
            mapOf(
                "users" to users,
                "clients" to clients
            )
        )
    }

    private fun redirectLogin(thymeleafPlugin: ThymeleafPlugin) =
        thymeleafPlugin.process(
            "login",
            mapOf("action" to "/admin")
        )
}