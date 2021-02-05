package com.github.gr3gdev.sso.pages

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.route.Response
import com.github.gr3gdev.jserver.route.RouteListener
import com.github.gr3gdev.jserver.security.TokenManager
import com.github.gr3gdev.sso.bean.AuthResponse
import com.github.gr3gdev.sso.bean.ClientResponse
import com.github.gr3gdev.sso.bean.Role
import com.github.gr3gdev.sso.service.SSOService

open class SSOPage(private val jsonMapper: ObjectMapper, private val tokenManager: TokenManager) {

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
            val authResponse = AuthResponse()
            ssoService.checkUserTokenFromHeader(req, { redirectUrl ->
                Response(HttpStatus.OK).redirect(redirectUrl)
            }, {
                ssoService.checkUserTokenFromCookie(req, { redirectUrl ->
                    Response(HttpStatus.OK).redirect(redirectUrl)
                }, {
                    ssoService.checkUserAuthentication(req, { user, redirectUrl ->
                        // Validité du token : 1h
                        authResponse.token = tokenManager.createToken(user, PageCst.ONE_HOUR)
                        authResponse.id = user.id
                        if (user.role == Role.ADMIN) {
                            Response(
                                HttpStatus.OK, "application/json",
                                jsonMapper.writeValueAsBytes(authResponse)
                            )
                                .cookie(PageCst.COOKIE_USER, authResponse.token)
                        } else {
                            Response(HttpStatus.OK)
                                .redirect(redirectUrl)
                                .cookie(PageCst.COOKIE_USER, authResponse.token)
                        }
                    }, {
                        Response(HttpStatus.UNAUTHORIZED)
                    })
                })
            })
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