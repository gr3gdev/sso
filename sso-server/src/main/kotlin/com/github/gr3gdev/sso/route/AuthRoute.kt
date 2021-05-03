package com.github.gr3gdev.sso.route

import com.github.gr3gdev.jserver.http.Request
import com.github.gr3gdev.jserver.logger.Logger
import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.route.Response
import com.github.gr3gdev.jserver.route.Route
import com.github.gr3gdev.jserver.route.RouteListener
import com.github.gr3gdev.jserver.security.TokenClientPlugin
import com.github.gr3gdev.jserver.security.TokenServerPlugin
import com.github.gr3gdev.jserver.security.http.TokenRequest
import com.github.gr3gdev.jserver.thymeleaf.ThymeleafPlugin
import com.github.gr3gdev.sso.http.beans.UserRequest
import com.github.gr3gdev.sso.service.ClientService
import com.github.gr3gdev.sso.service.UserService
import java.nio.charset.StandardCharsets
import java.util.*

open class AuthRoute(protected val cookieName: String = "MySSO", private val tokenExpiration: Long = 3600L * 1000L) {

    private val userService = UserService()
    private val clientService = ClientService()

    open fun get() = RouteListener().process { route ->
        val thymeleafPlugin = route.plugin(ThymeleafPlugin::class.java)
        val redirect = getParam(route, "redirect")
        validateClientAccess(route.request, thymeleafPlugin, redirect)
    }

    open fun post() = RouteListener().process { route ->
        val thymeleafPlugin = route.plugin(ThymeleafPlugin::class.java)
        val redirect = getParam(route, "redirect")
        val clientName = getParam(route, "clientName")
        var res = redirectLogin(thymeleafPlugin, redirect, clientName)
        TokenRequest.getTokenFromHeader(route.request).ifPresentOrElse({ token ->
            // Already connected
            route.plugin(TokenClientPlugin::class.java).getUserData(token, UserRequest::class.java).ifPresent {
                if (userService.isTokenValid(token, it)) {
                    res = Response(redirect)
                }
            }
        }, {
            val username = getParam(route, "username")
            val password = getParam(route, "password")
            res = if (username.isNotBlank() && password.isNotBlank()) {
                authenticateUser(
                    thymeleafPlugin,
                    route.plugin(TokenServerPlugin::class.java),
                    username,
                    password,
                    redirect,
                    clientName
                )
            } else {
                validateClientAccess(route.request, thymeleafPlugin, redirect)
            }
        })
        res
    }

    private fun authenticateUser(
        thymeleafPlugin: ThymeleafPlugin,
        tokenServerPlugin: TokenServerPlugin,
        username: String,
        password: String,
        redirect: String,
        clientName: String
    ): Response {
        var res = redirectLogin(thymeleafPlugin, redirect, clientName)
        userService.findByAuthorizedUser(clientName, username, password).ifPresent { user ->
            if (user.authorized) {
                val token = tokenServerPlugin.createToken(user, tokenExpiration)
                val cookie = Response.Cookie(cookieName, token, 300)
                res = Response(redirect).cookie(cookie)
            } else {
                Logger.warn("User $username not authorized on $clientName or username/password invalid")
            }
        }
        return res
    }

    protected fun getParam(route: Route, name: String): String {
        var param = ""
        route.request.params(name).ifPresent {
            param = it
        }
        return param
    }

    private fun validateClientAccess(request: Request, thymeleafPlugin: ThymeleafPlugin, redirect: String): Response {
        var response = Response(HttpStatus.UNAUTHORIZED)
        request.headers("Authorization").ifPresent {
            if (it.startsWith("Basic", true)) {
                val credentialsEncoded = it.substring("Basic".length).trim()
                val credentialsDecoded = Base64.getDecoder().decode(credentialsEncoded)
                val credentials = String(credentialsDecoded, StandardCharsets.UTF_8).split(Regex(":"), 2)
                if (clientService.isAuthorized(credentials[0], credentials[1], request.remoteAddress().ip())) {
                    response = redirectLogin(thymeleafPlugin, redirect, credentials[0])
                }
            }
        }
        return response
    }

    private fun redirectLogin(thymeleafPlugin: ThymeleafPlugin, redirect: String, clientName: String) =
        thymeleafPlugin.process(
            "login",
            mapOf(
                "action" to "/auth?redirect=$redirect",
                "client" to clientName
            )
        )
}