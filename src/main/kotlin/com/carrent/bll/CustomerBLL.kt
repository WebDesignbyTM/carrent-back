package com.carrent.bll

import com.carrent.dal.CustomerDAO
import com.carrent.model.Customer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.File

object CustomerBLL {
    var customerDAO: CustomerDAO? = null;

    fun init(newCustomerDAO: CustomerDAO) {
        this.customerDAO = newCustomerDAO
    }

    suspend fun getAllCustomers(): List<Customer> {
        return customerDAO!!.selectAll()
    }

    suspend fun deleteCustomerByEmail(email: String) {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")
        customerDAO!!.deleteCustomerByEmail(email)
    }

    suspend fun registerNewCustomer(name: String, email: String, phone: String, password: String) {

        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")

        if (!PhoneValidator().matches(phone))
            throw IllegalArgumentException("Invalid phone")

        val passHash = StringHasher(password)

        val customer = Customer(name, email, phone, passHash)
        customerDAO!!.createCustomer(customer)
    }

    suspend fun updateCustomerByEmail(name: String, email: String, phone: String) {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")
        val customer = customerDAO!!.selectByEmail(email) ?: throw IllegalArgumentException("No such user")
        val newCustomer = Customer(name, email, phone, customer.passHash)
        customerDAO!!.updateCustomer(newCustomer)
    }

    suspend fun getCustomerByEmail(email: String): Customer {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")
        val customer = customerDAO!!.selectByEmail(email) ?: throw IllegalArgumentException("No such user")
        return customer
    }

    suspend fun loadCustomersFromFile(filename: String) {
        val inputString: String = File(filename).readText()
        val customers = Json.parseToJsonElement(inputString).jsonArray

        for (i in 0 until customers.size) {
            val customer = customers.get(i).jsonObject

            registerNewCustomer(
                customer.get("name")!!.toString().replace("\"", ""),
                customer.get("email")!!.toString(),
                customer.get("phone")!!.toString(),
                customer.get("password")!!.toString()
            )
        }
    }
}