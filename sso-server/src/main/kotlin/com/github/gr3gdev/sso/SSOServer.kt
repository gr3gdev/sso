package com.github.gr3gdev.sso

import com.github.gr3gdev.jserver.JServer
import com.github.gr3gdev.jserver.security.TokenClientPlugin
import com.github.gr3gdev.jserver.security.TokenServerPlugin
import com.github.gr3gdev.jserver.thymeleaf.ThymeleafPlugin
import com.github.gr3gdev.sso.dao.DataSource
import com.github.gr3gdev.sso.route.AdminRoute
import com.github.gr3gdev.sso.route.AuthRoute
import com.github.gr3gdev.sso.route.StaticRoute
import java.io.FileInputStream
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import java.util.*

class SSOServer

fun main() {
    val ssoConf =
        System.getenv("mysso.config.file") ?: SSOServer::class.java.getResource("/database/sso.properties").path
    val keystoreAlias =
        System.getenv("mysso.keystore.alias") ?: throw RuntimeException("mysso.keystore.alias must be defined")
    val keystoreFile =
        System.getenv("mysso.keystore.path") ?: throw RuntimeException("mysso.keystore.path must be defined")
    val keystorePassword =
        System.getenv("mysso.keystore.password") ?: throw RuntimeException("mysso.keystore.password must be defined")
    val publicKeyFile =
        System.getenv("mysso.keystore.publicKey") ?: throw RuntimeException("mysso.keystore.publicKey must be defined")

    val publicFile = String(FileInputStream(publicKeyFile).readAllBytes())
        .replace("\n", "")
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
    val spec = X509EncodedKeySpec(Base64.getDecoder().decode(publicFile))

    DataSource.init(ssoConf)

    val authRoute = AuthRoute()
    val adminRoute = AdminRoute()

    JServer.server()
        .plugins(
            TokenServerPlugin(keystoreAlias, FileInputStream(keystoreFile), keystorePassword.toCharArray()),
            TokenClientPlugin(KeyFactory.getInstance("RSA").generatePublic(spec)),
            ThymeleafPlugin()
        )
        .port(9000)
        .get("/css/{file}", StaticRoute.css())
        .get("/auth", authRoute.get())
        .post("/auth", authRoute.post())
        .get("/admin", adminRoute.get())
        .post("/admin", adminRoute.post())
        .start()
}
