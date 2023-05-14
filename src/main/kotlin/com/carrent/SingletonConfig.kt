package com.carrent

import io.ktor.server.config.*

object SingletonConfig {
    var jwtSecret: String = ""
    var jwtIssuer: String = ""
    var jwtAudience: String = ""
    var jwtRealm: String = ""

    fun init(config: ApplicationConfig) {
        jwtSecret = config.property("jwt.secret").getString()
        jwtIssuer = config.property("jwt.issuer").getString()
        jwtAudience = config.property("jwt.audience").getString()
        jwtRealm = config.property("jwt.realm").getString()
    }
}