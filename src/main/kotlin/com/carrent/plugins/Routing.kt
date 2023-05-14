package com.carrent.plugins

import com.carrent.model.Customer
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureRouting() {

    val customerList: MutableList<Customer> = mutableListOf()
    customerList.add(Customer("John Doe", "johndoe@hotmail.com", "+40112", "15"))
    customerList.add(Customer("Jimothean Slipus", "slippinjimmy@hotmail.com", "+1112", "17"))

    routing {

        // Static plugin. Try to access `/static/index.html`
        static("/static") {
            resources("static")
        }

        get("/customer/register") {

        }

        post("/customer/register") {

        }
    }
}
