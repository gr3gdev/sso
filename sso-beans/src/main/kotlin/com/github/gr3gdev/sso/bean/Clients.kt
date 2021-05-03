package com.github.gr3gdev.sso.bean

class Clients {

    lateinit var clientName: String

    lateinit var clientSecret: String

    val remoteAddresses = HashSet<String>()

}