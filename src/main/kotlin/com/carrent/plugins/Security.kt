package com.carrent.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.carrent.SingletonConfig
import com.carrent.bll.CustomerBLL
import com.carrent.bll.EmployeeBLL
import com.carrent.bll.StringHasher
import com.carrent.model.Customer
import com.carrent.model.Employee
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.*
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
        allowHeader(HttpHeaders.AccessControlAllowMethods)
        allowHeader(HttpHeaders.XRequestId)
        allowMethod(HttpMethod.Delete)
        allowCredentials = true
        allowSameOrigin = true
        anyHost()
    }

    val jwtRealm = SingletonConfig.jwtRealm
    val jwtSecret = SingletonConfig.jwtSecret
    val jwtIssuer = SingletonConfig.jwtIssuer
    val jwtAudience = SingletonConfig.jwtAudience

    authentication {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(JWT
                .require(Algorithm.HMAC256(jwtSecret))
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .build())
            validate { credential ->
                if (credential.payload.getClaim("email").asString() != "") {
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
        post("/jwt-login") {
            @Serializable
            data class User(val email: String, val password: String) {}

            val user = call.receive<User>()
            val customers = CustomerBLL.getAllCustomers()
            val employees = EmployeeBLL.getAllEmployees()
            var matchedCustomer: Customer? = null
            var matchedEmployee: Employee? = null

            for (employee in employees) {
                if (user.email == employee.email && StringHasher(user.password) == employee.passHash) {
                    matchedEmployee = employee
                    break
                }
            }

            for (customer in customers) {
                if (user.email == customer.email && StringHasher(user.password) == customer.passHash) {
                    matchedCustomer = customer
                    break
                }
            }

            if (matchedCustomer == null && matchedEmployee == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                return@post
            }

            val status = if (matchedEmployee != null) "employee" else "customer"

            val token = JWT.create()
                .withAudience(jwtAudience)
                .withIssuer(jwtIssuer)
                .withClaim("email", user.email)
                .withClaim("status", status)
                .withClaim("admin", matchedEmployee != null && matchedEmployee.adminFlag)
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
                val status = principal.payload.getClaim("status").asString()
                val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
                call.respondText("Hello, $username! Your status is $status (${if (admin) "admin" else "not admin"}). Token is expired at $expiresAt ms.")
            }
        }
    }
}
