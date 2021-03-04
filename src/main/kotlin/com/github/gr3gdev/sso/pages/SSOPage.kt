package com.github.gr3gdev.sso.pages

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.gr3gdev.jserver.logger.Logger
import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.route.Response
import com.github.gr3gdev.jserver.route.RouteListener
import com.github.gr3gdev.jserver.security.TokenManager
import com.github.gr3gdev.sso.bean.http.AuthResponse
import com.github.gr3gdev.sso.bean.http.ClientResponse
import com.github.gr3gdev.sso.bean.Role
import com.github.gr3gdev.sso.bean.SsoUser
import com.github.gr3gdev.sso.service.SSOService

object SSOPage {

    private val jsonMapper = jacksonObjectMapper()
    private val tokenManager = TokenManager.issuer("GR3Gdev_MySSO").generateSecretKey(256)
    private val ssoService = SSOService(jsonMapper, tokenManager)

    private val mimeTypes = mapOf(
        ".js" to "application/javascript",
        ".css" to "text/css",
        ".ttf" to "font/ttf",
        ".eot" to "application/vnd.ms-fontobject",
        ".svg" to "image/svg+xml",
        ".woff" to "font/woff",
        ".woff2" to "font/woff2",
        ".png" to "image/png",
        ".json" to "application/json",
        ".html" to "text/html",
        ".txt" to "text/plain"
    )

    fun static(path: String) = RouteListener().process { req ->
        req.params("file", { file ->
            Response(
                HttpStatus.OK,
                Response.File("$path/$file", mimeTypes.entries.firstOrNull {
                    file.endsWith(it.key)
                }?.value ?: "text/plain")
            )
        }, {
            Response(HttpStatus.NOT_FOUND)
        })
    }

    fun home() = RouteListener(HttpStatus.OK, Response.File("/index.html", "text/html"))

    fun login(): RouteListener {
        return RouteListener().process { req ->
            ssoService.checkUserTokenFromHeader(req, { user, redirectUrl ->
                Logger.info("login() token dans header")
                responseUser(user, redirectUrl)
            }, {
                ssoService.checkUserTokenFromCookie(req, { user, redirectUrl ->
                    Logger.info("login() token dans cookie")
                    responseUser(user, redirectUrl)
                }, {
                    Logger.info("login() utilisateur non connecté")
                    ssoService.checkUserAuthentication(req, { user, redirectUrl ->
                        responseUser(user, redirectUrl)
                    }, {
                        Response(HttpStatus.UNAUTHORIZED)
                    })
                })
            })
        }
    }

    private fun responseUser(
        user: SsoUser,
        redirectUrl: String
    ): Response {
        val authResponse = AuthResponse()
        // Validité du token : 1h
        authResponse.token = tokenManager.createToken(user, PageCst.ONE_HOUR)
        authResponse.id = user.id
        authResponse.mustUpdated = user.temporaryPassword
        return if (user.role == Role.ADMIN.name) {
            Response(
                HttpStatus.OK, "application/json",
                jsonMapper.writeValueAsBytes(authResponse)
            )
        } else {
            Response(HttpStatus.OK)
                .redirect(redirectUrl)
                .cookie(PageCst.COOKIE_USER, authResponse.token)
        }
    }

    fun clientAccess(): RouteListener {
        return RouteListener().process { req ->
            ssoService.checkClientAuthentication(req, { client ->
                val clientResponse = ClientResponse()
                clientResponse.client = client.clientName
                // Validité du token : 1j
                clientResponse.token = tokenManager.createToken(client, PageCst.ONE_DAY)
                Response(
                    HttpStatus.OK, "application/json",
                    jsonMapper.writeValueAsBytes(clientResponse)
                )
            }, {
                Response(HttpStatus.UNAUTHORIZED)
            })
        }
    }

}