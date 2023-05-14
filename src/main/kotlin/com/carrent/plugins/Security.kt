package com.carrent.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carrent.SingletonConfig
import com.carrent.bll.CustomerBLL
import com.carrent.bll.StringHasher
import com.carrent.model.Customer
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

fun Application.configureSecurity() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.AccessControlAllowHeaders)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
    }

    val jwtRealm = SingletonConfig.jwtRealm
    val jwtSecret = SingletonConfig.jwtSecret
    val jwtIssuer = SingletonConfig.jwtIssuer
    val jwtAudience = SingletonConfig.jwtAudience

    authentication {
//        basic(name = "myauth1") {
//            realm = "Ktor Server"
//            validate { credentials ->
//                if (credentials.name == credentials.password) {
//                    UserIdPrincipal(credentials.name)
//                } else {
//                    null
//                }
//            }
//        }
//
//        form(name = "myauth2") {
//            userParamName = "user"
//            passwordParamName = "password"
//            challenge {
//                /**/
//            }
//        }

        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(JWT
                .require(Algorithm.HMAC256(jwtSecret))
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .build())
            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
    routing {
//        authenticate("myauth1") {
//            get("/protected/route/basic") {
//                val principal = call.principal<UserIdPrincipal>()!!
//                call.respondText("Hello ${principal.name}")
//            }
//        }
//        authenticate("myauth2") {
//            get("/protected/route/form") {
//                val principal = call.principal<UserIdPrincipal>()!!
//                call.respondText("Hello ${principal.name}")
//            }
//        }

        post("/jwt-login") {
            @Serializable
            data class User(val email: String, val password: String) {}

            val user = call.receive<User>()
            val customers = CustomerBLL.getAllCustomers()
            var matchedCustomer: Customer? = null
            for (customer in customers) {
                if (user.email == customer.email && StringHasher(user.password) == customer.passHash) {
                    matchedCustomer = customer
                    break
                }
            }

            if (matchedCustomer == null)
            {
                call.respond("no")
                return@post
            }

            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withClaim("email", user.email)
                .withClaim("admin", false)
                .withExpiresAt(Date(System.currentTimeMillis() + 86400000))
                .sign(Algorithm.HMAC256(jwtSecret))
            call.respond(hashMapOf("token" to token))
        }

        authenticate("auth-jwt") {
            get("/hello") {
                println(call.authentication.toString())
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("email").asString()
                val admin = principal.payload.getClaim("admin").asBoolean()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! You are an admin ($admin). Token is expired at $expiresAt ms.")
            }
        }
    }
}
