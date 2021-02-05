package com.github.gr3gdev.sso.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.gr3gdev.jserver.http.Request
import com.github.gr3gdev.jserver.logger.Logger
import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.security.TokenManager
import com.github.gr3gdev.sso.bean.*
import com.github.gr3gdev.sso.pages.PageCst

class AdminService(private val jsonMapper: ObjectMapper, private val tokenManager: TokenManager) {

    fun saveClient(json: String, apiResponse: ApiResponse) {
        val client = jsonMapper.readValue<Client>(json)
        val updated = client.id > 0
        val clientSaved = ClientService.save(client)
        if (clientSaved.id > 0) {
            apiResponse.status = if (updated) {
                HttpStatus.OK
            } else {
                HttpStatus.CREATED
            }
            apiResponse.data = clientSaved
        } else {
            apiResponse.status = HttpStatus.INTERNAL_SERVER_ERROR
            apiResponse.message = "Error when created client"
        }
    }

    fun errorSaveClient(apiResponse: ApiResponse) {
        apiResponse.status = HttpStatus.NOT_FOUND
        apiResponse.message = "No client data"
    }

    fun saveUser(json: String, apiResponse: ApiResponse) {
        val user = jsonMapper.readValue<User>(json)
        val updated = user.id > 0
        val userSaved = UserService.save(user)
        if (userSaved.id > 0) {
            apiResponse.status = if (updated) {
                HttpStatus.OK
            } else {
                HttpStatus.CREATED
            }
            apiResponse.data = userSaved
        } else {
            apiResponse.status = HttpStatus.INTERNAL_SERVER_ERROR
            apiResponse.message = "Error when created user"
        }
    }

    fun errorSaveUser(apiResponse: ApiResponse) {
        apiResponse.status = HttpStatus.NOT_FOUND
        apiResponse.message = "No user data"
    }

    fun <T> checkUserAccess(
        req: Request,
        action: () -> T,
        forbidden: () -> T,
        orElse: () -> T
    ): T {
        return tokenManager.getTokenFromHeader(req, { token ->
            Logger.debug("checkUserAccess $token")
            tokenManager.getUserData(token, User::class.java, {
                if (it.role == Role.ADMIN) {
                    action()
                } else {
                    Logger.warn("Access forbidden : ${it.username}")
                    forbidden()
                }
            }, {
                orElse()
            })
        }, {
            orElse()
        })
    }

    fun <T> accessAdmin(json: String, ifPresent: (res: AuthResponse) -> T, orElse: () -> T): T {
        var res: T? = null
        val auth = jsonMapper.readValue<AuthRequest>(json)
        UserService.find(auth.username, auth.password, { user ->
            val authResponse = AuthResponse()
            authResponse.id = user.id
            authResponse.token = tokenManager.createToken(user, PageCst.ONE_HOUR)
            res = ifPresent(authResponse)
        }, {
            res = orElse()
        })
        return res!!
    }

}