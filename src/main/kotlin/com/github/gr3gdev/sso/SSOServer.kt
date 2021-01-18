package com.github.gr3gdev.sso

import com.github.gr3gdev.jserver.JServer
import com.github.gr3gdev.jserver.logger.Logger
import com.github.gr3gdev.sso.pages.SSOPage

fun main() {
    Logger.changeLevel(Logger.Level.WARN)
    JServer.server().port(3000)
        .get("/css/sso.css", SSOPage.css())
        .post("/access-token", SSOPage.access())
        .post("/login/{redirect}", SSOPage.login())
        .get("/{redirect}", SSOPage.home())
        .start()
}