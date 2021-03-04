package com.github.gr3gdev.sso.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gr3gdev.jserver.http.Request
import com.github.gr3gdev.jserver.security.TokenManager
import com.github.gr3gdev.sso.bean.http.AuthRequest
import com.github.gr3gdev.sso.bean.SsoClient
import com.github.gr3gdev.sso.bean.http.ClientRequest
import com.github.gr3gdev.sso.bean.SsoUser
import com.github.gr3gdev.sso.pages.PageCst

class SSOService(private val jsonMapper: ObjectMapper, private val tokenManager: TokenManager) {

    private fun getRedirectURL(req: Request): String {
        return req.params("redirect", { url ->
            url
        }, {
            "/"
        })
    }

    fun <T> checkUserTokenFromHeader(
        req: Request,
        userFound: (user: SsoUser, url: String) -> T,
        orElse: () -> T
    ): T {
        return tokenManager.getTokenFromHeader(req, { token ->
            tokenManager.getUserData(token, SsoUser::class.java, {
                userFound(it, getRedirectURL(req))
            }, {
                orElse()
            })
        }, {
            orElse()
        })
    }

    fun <T> checkUserTokenFromCookie(
        req: Request,
        userFound: (user: SsoUser, url: String) -> T,
        orElse: () -> T
    ): T {
        return tokenManager.getTokenFromCookie(req, PageCst.COOKIE_USER, { token ->
            tokenManager.getUserData(token, SsoUser::class.java, {
                userFound(it, getRedirectURL(req))
            }, {
                orElse()
            })
        }, {
            orElse()
        })
    }

    fun <T> checkUserAuthentication(
        req: Request,
        userFound: (user: SsoUser, url: String) -> T,
        orElse: () -> T
    ): T {
        return req.params("body", { json ->
            val auth = jsonMapper.readValue<AuthRequest>(json)
            UserService.find(auth.username, auth.password, { user ->
                userFound(user, getRedirectURL(req))
            }, {
                orElse()
            })
        }, {
            orElse()
        })
    }

    fun <T> checkClientAuthentication(
        req: Request,
        clientFound: (client: SsoClient) -> T,
        orElse: () -> T
    ): T {
        return req.params("body", { json ->
            val clientRequest = jsonMapper.readValue<ClientRequest>(json)
            val ip = extractAddressIP(req)
            ClientService.find(clientRequest.clientName, clientRequest.clientSecret, ip, {
                clientFound(it)
            }, {
                orElse()
            })
        }, {
            orElse()
        })
    }

    private fun extractAddressIP(req: Request): String? {
        return req.remoteAddress().ip()
    }

}