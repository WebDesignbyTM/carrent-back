package com.carrent

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carrent.plugins.*
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    val config = HoconApplicationConfig(ConfigFactory.parseMap(mapOf(
        "jwt.secret" to "secret",
        "jwt.issuer" to "http://127.0.0.1:8080",
        "jwt.audience" to "http://127.0.0.1:8080/jwt-hello",
        "jwt.realm" to "Access to 'hello' for jwt"
    )))
    SingletonConfig.init(config)

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureDatabase()
    configureSecurity()
    configureRouting()
}
