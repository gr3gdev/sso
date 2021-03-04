package com.github.gr3gdev.sso.pages

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.gr3gdev.jserver.http.Request
import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.route.Response
import com.github.gr3gdev.jserver.route.RouteListener
import com.github.gr3gdev.jserver.security.TokenManager
import com.github.gr3gdev.sso.bean.http.ApiResponse
import com.github.gr3gdev.sso.service.AdminService
import com.github.gr3gdev.sso.service.ClientService
import com.github.gr3gdev.sso.service.UserService

object SSOAdminPage {

    private val jsonMapper = jacksonObjectMapper()
    private val tokenManager = TokenManager.issuer("GR3Gdev_MySSO").generateSecretKey(256)
    private val adminService = AdminService(jsonMapper, tokenManager)

    fun getClients(): RouteListener {
        return adminAction {
            Response(
                HttpStatus.OK, "application/json",
                jsonMapper.writeValueAsBytes(ClientService.findAll())
            )
        }
    }

    fun getUsers(): RouteListener {
        return adminAction {
            Response(
                HttpStatus.OK, "application/json",
                jsonMapper.writeValueAsBytes(UserService.findAll())
            )
        }
    }

    fun saveClient(): RouteListener {
        return adminAction { req ->
            val apiResponse = ApiResponse()
            req.params("body", {
                adminService.saveClient(it, apiResponse)
            }, {
                adminService.errorSaveClient(apiResponse)
            })
            Response(
                apiResponse.status, "application/json",
                jsonMapper.writeValueAsBytes(apiResponse)
            )
        }
    }

    fun saveUser(): RouteListener {
        return adminAction { req ->
            val apiResponse = ApiResponse()
            req.params("body", {
                adminService.saveUser(it, apiResponse)
            }, {
                adminService.errorSaveUser(apiResponse)
            })
            Response(
                apiResponse.status, "application/json",
                jsonMapper.writeValueAsBytes(apiResponse)
            )
        }
    }

    private fun adminAction(action: (req: Request) -> Response): RouteListener {
        return RouteListener().process { req ->
            adminService.checkUserAccess(req, {
                action(req)
            }, {
                Response(HttpStatus.FORBIDDEN)
            }, {
                Response(HttpStatus.UNAUTHORIZED)
            })
        }
    }

    fun auth(): RouteListener {
        return RouteListener().process { req ->
            req.params("body", { json ->
                adminService.accessAdmin(json, {
                    Response(
                        HttpStatus.OK, "application/json",
                        jsonMapper.writeValueAsBytes(it)
                    )
                }, {
                    Response(HttpStatus.UNAUTHORIZED)
                })
            }, {
                Response(HttpStatus.UNAUTHORIZED)
            })
        }
    }

}