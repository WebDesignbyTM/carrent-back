package com.carrent.plugins

import com.carrent.bll.CarBLL
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.netty.handler.codec.http.HttpResponseStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import com.carrent.bll.CustomerBLL
import com.carrent.bll.EmployeeBLL
import com.carrent.dal.CarDAO
import com.carrent.dal.CustomerDAO
import com.carrent.dal.EmployeeDAO
import com.carrent.model.Customer
import com.carrent.model.Employee
import io.ktor.server.auth.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {

    val database = Database.connect(
        url = "jdbc:postgresql://localhost:5432/postgres",
        user = "postgres",
        driver = "org.postgresql.Driver",
        password = "root"
    )
    val customerDAO = CustomerDAO(database)
    val employeeDAO = EmployeeDAO(database)
    val carDAO = CarDAO(database)

    CustomerBLL.init(customerDAO)
    val employeeBLL = EmployeeBLL(employeeDAO)
    val carBLL = CarBLL(carDAO)

    routing {
        get("/customers") {
            val customers = CustomerBLL.getAllCustomers()
            val jsonResponse = Json.encodeToString(customers)
            call.respondText(jsonResponse, ContentType.Application.Json, HttpStatusCode.OK)
        }

        delete("/customers") {
            try {
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                CustomerBLL.deleteCustomerByEmail(email)
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
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

//        authenticate("myauth1") {
//            get("/employees") {
//                val employees = employeeBLL.getAllEmployees()
//                val jsonResponse = Json.encodeToString(employees)
//                call.respondText(jsonResponse, ContentType.Application.Json, HttpStatusCode.OK)
//            }
//        }

        delete("/employees") {
            try {
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                employeeBLL.deleteEmployeeByEmail(email)
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
                employeeBLL.registerNewEmployee(name, email, password, adminFlag)
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
                employeeBLL.updateEmployeeByEmail(name, email, adminFlag)
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        get("/employee") {
            try {
                val email = call.parameters["email"] ?: throw IllegalArgumentException("Null email")
                val employee = employeeBLL.getEmployeeByEmail(email)
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
                val res = employeeBLL.login(email, password)
                print("Login: ${res}")
                if (!res)
                    call.respondText("Invalid login", status = HttpStatusCode.Unauthorized)
                else
                    call.respond(HttpResponseStatus.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        post("/car") {
            try {
                val brand = call.parameters["brand"] ?: throw IllegalArgumentException("Null brand")
                val model = call.parameters["model"] ?: throw IllegalArgumentException("Null model")
                carBLL.registerNewCar(brand, model)
                call.respond(HttpStatusCode.OK)
            } catch (e: IllegalArgumentException) {
                call.respondText(e.toString(), status = HttpStatusCode.BadRequest)
            }
        }

        post("/car/file") {
            try {
                carBLL.loadCarsFromFile("C:\\Users\\logoeje\\source\\kotlin\\CarRental\\cars.txt")
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(e.toString(), status = HttpStatusCode.InternalServerError)
            }
        }

        get("/car/graph") {
            try {
                carBLL.generateGraph()
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(e.toString(), status = HttpStatusCode.InternalServerError)
            }
        }
    }
}
