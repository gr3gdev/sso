package com.github.gr3gdev.sso.route

import com.github.gr3gdev.jserver.route.HttpStatus
import com.github.gr3gdev.jserver.route.Response
import com.github.gr3gdev.jserver.route.RouteListener

object StaticRoute {

    private val mimeTypes = mapOf(
        ".css" to "text/css",
        ".svg" to "image/svg+xml",
        ".pgn" to "image/png",
        ".eot" to "application/vnd.ms-fontobject",
        ".ttf" to "font/ttf",
        ".woff" to "font/woff",
        ".woff2" to "font/woff2"
    )

    fun css() = RouteListener().process { route ->
        val file = route.request.params("file")
        var res = Response(HttpStatus.NOT_FOUND)
        file.ifPresent {
            val contentType = mimeTypes.entries.firstOrNull { m -> it.endsWith(m.key, true) }?.value ?: "text/plain"
            res = Response(HttpStatus.OK, Response.File("/css/$it", contentType))
        }
        res
    }

}