package com.github.gr3gdev.sso.http.beans

class ClientRequest {

    lateinit var clientName: String

    var authorized: Boolean = false

    val remoteAddresses = HashSet<String>()

}