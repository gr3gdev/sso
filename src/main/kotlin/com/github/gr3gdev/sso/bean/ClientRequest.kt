package com.github.gr3gdev.sso.bean

import com.fasterxml.jackson.annotation.JsonProperty

class ClientRequest {
    @JsonProperty
    lateinit var clientName: String
    @JsonProperty
    lateinit var clientSecret: String
}