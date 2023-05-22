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
    EmployeeBLL.init(employeeDAO)
    CarBLL.init(carDAO)
//    val carBLL = CarBLL(carDAO)

    routing {

    }
}
