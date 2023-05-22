package com.carrent.plugins

import com.carrent.bll.CarBLL
import com.carrent.bll.CustomerBLL
import com.carrent.bll.EmployeeBLL
import com.carrent.model.Customer
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.netty.handler.codec.http.HttpResponseStatus
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
            try {
                print(call)
                @Serializable
                data class SignUpData(val name: String, val email: String, val phone: String, val password: String) {}
                val signUpData = call.receive<SignUpData>()
                val name = signUpData.name
                val email = signUpData.email
                val phone = signUpData.phone
                val password = signUpData.password
                CustomerBLL.registerNewCustomer(name, email, phone, password);
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-jwt") {
            get("/customers") {
                val customers = CustomerBLL.getAllCustomers()
                val jsonResponse = Json.encodeToString(customers)
                call.respondText(jsonResponse, ContentType.Application.Json, HttpStatusCode.OK)
            }

            delete("/customers") {
                try {
                    @Serializable
                    data class DeleteReq(val email: String) {}
                    val deleteReq = call.receive<DeleteReq>()
                    CustomerBLL.deleteCustomerByEmail(deleteReq.email)
                    call.respond(HttpStatusCode.OK)
                } catch (e: IllegalArgumentException) {
                    call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
                }

            }
        }

        post("/customers/register") {
            try {
                val name = call.parameters["name"] ?: throw IllegalArgumentException("Null name")
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val phone = call.parameters["phone"] ?: throw IllegalArgumentException("Null phone")
                val password = call.parameters["password"] ?: throw  IllegalArgumentException("Null password")
                CustomerBLL.registerNewCustomer(name, email, phone, password);
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        post("/customers") {
            try {
                val name = call.parameters["name"] ?: throw IllegalArgumentException("Null name")
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val phone = call.parameters["phone"] ?: throw IllegalArgumentException("Null phone")
                CustomerBLL.updateCustomerByEmail(name, email, phone)
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        post("/customers/file") {
            try {
                CustomerBLL.loadCustomersFromFile("C:\\Users\\logoeje\\source\\kotlin\\CarRental\\customers.txt")
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(e.toString(), status = HttpStatusCode.InternalServerError)
            }
        }

        get("/customer") {
            try {
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val customer = CustomerBLL.getCustomerByEmail(email)
                val jsonResponse = Json.encodeToString(customer)
                call.respondText(jsonResponse, ContentType.Application.Json, HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }


        get("/employees") {
            val employees = EmployeeBLL.getAllEmployees()
            val jsonResponse = Json.encodeToString(employees)
            call.respondText(jsonResponse, ContentType.Application.Json, HttpStatusCode.OK)
        }


        delete("/employees") {
            try {
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                EmployeeBLL.deleteEmployeeByEmail(email)
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }

        }

        post("/employees/register") {
            try {
                val name = call.parameters["name"] ?: throw IllegalArgumentException("Null name")
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val password = call.parameters["password"] ?: throw  IllegalArgumentException("Null password")
                val adminFlag = call.parameters["adminflag"].toBoolean()
                EmployeeBLL.registerNewEmployee(name, email, password, adminFlag)
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        post("/employees") {
            try {
                val name = call.parameters["name"] ?: throw IllegalArgumentException("Null name")
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val adminFlag = call.parameters["adminflag"].toBoolean()
                EmployeeBLL.updateEmployeeByEmail(name, email, adminFlag)
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        get("/employee") {
            try {
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val employee = EmployeeBLL.getEmployeeByEmail(email)
                val jsonResponse = Json.encodeToString(employee)
                call.respondText(jsonResponse, ContentType.Application.Json, HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        post("/employees/login") {
            try {
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val password = call.parameters["password"] ?: throw IllegalArgumentException("Null password")
                val res = EmployeeBLL.login(email, password)
                print("Login: ${res}")
                if (!res)
                    call.respondText("Invalid login", status = HttpStatusCode.Unauthorized)
                else
                    call.respond(HttpResponseStatus.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        authenticate("auth-jwt") {
            post("/car") {
                try {
                    @Serializable
                    data class CarRegistration(val brand: String, val model: String) {}
                    val carRegistration = call.receive<CarRegistration>()
                    CarBLL.registerNewCar(carRegistration.brand, carRegistration.model)
                    call.respond(HttpStatusCode.OK)
                } catch (e: IllegalArgumentException) {
                    call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
                }
            }
        }

        post("/car/file") {
            try {
                CarBLL.loadCarsFromFile("C:\\Users\\logoeje\\source\\kotlin\\CarRental\\cars.txt")
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(e.toString(), status = HttpStatusCode.InternalServerError)
            }
        }

        get("/car/graph") {
            try {
                CarBLL.generateGraph()
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(e.toString(), status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
