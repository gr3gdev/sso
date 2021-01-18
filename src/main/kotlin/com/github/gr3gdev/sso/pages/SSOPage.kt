package com.github.gr3gdev.sso.pages

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gr3gdev.jserver.http.Request
import com.github.gr3gdev.jserver.logger.Logger
import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.route.ResponseData
import com.github.gr3gdev.jserver.route.RouteListener
import com.github.gr3gdev.jserver.security.TokenManager
import com.github.gr3gdev.sso.bean.Client
import com.github.gr3gdev.sso.bean.ClientRequest
import com.github.gr3gdev.sso.bean.ClientResponse
import com.github.gr3gdev.sso.bean.User
import com.github.gr3gdev.sso.service.ClientService
import com.github.gr3gdev.sso.service.UserService
import java.nio.charset.StandardCharsets

object SSOPage {

    private val userService = UserService()
    private val clientService = ClientService()

    private const val ONE_HOUR: Long = 60 * 60 * 1000
    private const val ONE_DAY: Long = 24 * ONE_HOUR

    private const val COOKIE_USER = "MySSO_JWT"

    private val jsonMapper = jacksonObjectMapper()
    private val tokenManager = TokenManager.issuer("GR3Gdev_MySSO").generateSecretKey(256)

    fun css() = RouteListener(HttpStatus.OK, ResponseData.File("/css/sso.css", "text/css"))

    fun home(): RouteListener {
        return RouteListener().process { req, res ->
            if (applicationAuthorized(req)) {
                req.params("redirect").ifPresentOrElse({redirect ->
                    tokenManager.getTokenFromCookie(req, COOKIE_USER).ifPresent { token ->
                        tokenManager.getUserData(token, User::class.java).ifPresentOrElse({
                            // Utilisateur déjà connecté
                            res.redirect = redirect
                        }, {
                            res.contentType = "text/html"
                            res.content = content(redirect, null)
                        })
                    }
                }, {
                    res.contentType = "text/html"
                    res.content = content(null, "Missing url to redirect")
                })
            } else {
                res.status = HttpStatus.FORBIDDEN
            }
        }
    }

    fun login(): RouteListener {
        return RouteListener().process { req, res ->
            if (applicationAuthorized(req)) {
                req.params("redirect").ifPresentOrElse({ redirect ->
                    req.params("username").ifPresentOrElse({ username ->
                        req.params("password").ifPresentOrElse({ password ->
                            userService.find(username, password).ifPresentOrElse({
                                // Validité du token : 1h
                                val token = tokenManager.createToken(it, ONE_HOUR)
                                res.cookies[COOKIE_USER] = token
                                res.redirect = redirect
                            }, {
                                res.contentType = "text/html"
                                res.content = content(redirect, "Username or password incorrect !")
                            })
                        }, {
                            res.status = HttpStatus.FORBIDDEN
                            res.contentType = "text/html"
                            res.content = content(redirect, "Missing password !")
                        })
                    }, {
                        res.status = HttpStatus.FORBIDDEN
                        res.contentType = "text/html"
                        res.content = content(redirect, "Missing username !")
                    })
                }, {
                    res.contentType = "text/html"
                    res.content = content(null, "Missing url to redirect")
                })
            } else {
                res.status = HttpStatus.FORBIDDEN
            }
        }
    }

    fun access(): RouteListener {
        return RouteListener().process { req, res ->
            req.params("body").ifPresentOrElse({ json ->
                val clientRequest = jsonMapper.readValue<ClientRequest>(json)
                val ip = extractAddressIP(req)
                clientService.find(clientRequest.clientName, clientRequest.clientSecret, ip).ifPresentOrElse({
                    val clientResponse = ClientResponse()
                    clientResponse.client = it.clientName
                    // Validité du token : 1j
                    clientResponse.token = tokenManager.createToken(it, ONE_DAY)
                    res.status = HttpStatus.OK
                    res.contentType = "application/json"
                    res.content = jsonMapper.writeValueAsBytes(clientResponse)
                }, {
                    res.status = HttpStatus.UNAUTHORIZED
                })
            }, {
                Logger.info("JSON's content is missing")
                res.status = HttpStatus.INTERNAL_SERVER_ERROR
            })
        }
    }

    private fun applicationAuthorized(req: Request): Boolean {
        val ip = extractAddressIP(req)
        var app: Client? = null
        tokenManager.getTokenFromHeader(req).ifPresent { token ->
            tokenManager.getUserData(token, Client::class.java).ifPresent {
                app = it
            }
        }
        return app != null && app?.addressIP == ip
    }

    private fun content(redirectUrl: String?, errorMessage: String?): ByteArray {
        var html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <meta charset='UTF-8' />" +
                "    <title>My SSO</title>" +
                "    <link href='/css/sso.css' rel='stylesheet'>" +
                "</head>" +
                "<body>" +
                "<div class='sso'>" +
                "    <form method='POST' action='/login/$redirectUrl'>" +
                "        <div class='field'>" +
                "            <input name='username' type='text' placeholder='Username' autofocus='true' autocomplete='off'/>" +
                "        </div>" +
                "        <div class='field'>" +
                "            <input name='password' type='password' placeholder='Password' autocomplete='off'/>" +
                "        </div>"
        if (errorMessage != null) {
            html += "        <div class='error'>$errorMessage</div>"
        }
        html += "        <div class='field'>" +
                "            <button type='submit'>Login</button>" +
                "        </div>" +
                "    </form>" +
                "</div>" +
                "</body>" +
                "</html>"
        return html.toByteArray(StandardCharsets.UTF_8)
    }

    private fun extractAddressIP(req: Request): String? {
        return req.remoteAddress().ip()
    }

}