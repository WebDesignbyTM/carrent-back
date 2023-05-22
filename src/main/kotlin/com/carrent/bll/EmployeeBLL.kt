package com.carrent.bll

import com.carrent.dal.EmployeeDAO
import com.carrent.model.Employee

object EmployeeBLL {
    var employeeDAO: EmployeeDAO? = null

    fun init(newEmployeeDAO: EmployeeDAO) {
        employeeDAO = newEmployeeDAO
    }

    suspend fun getAllEmployees(): List<Employee> {
        return employeeDAO!!.selectAll()
    }

    suspend fun deleteEmployeeByEmail(email: String) {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")
        employeeDAO!!.deleteEmployeeByEmail(email)
    }

    suspend fun registerNewEmployee(name: String, email: String, password: String, adminFlag: Boolean) {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")

        val passHash = StringHasher(password)

        val employee = Employee(name, email, passHash, adminFlag)
        employeeDAO!!.createEmployee(employee)
    }

    suspend fun updateEmployeeByEmail(name: String, email: String, adminFlag: Boolean) {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")
        val employee = employeeDAO!!.selectByEmail(email) ?: throw IllegalArgumentException("No such user")
        val newEmployee = Employee(name, email, employee.passHash, adminFlag)
        employeeDAO!!.updateEmployee(newEmployee)
    }

    suspend fun getEmployeeByEmail(email: String): Employee {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")
        return employeeDAO!!.selectByEmail(email) ?: throw IllegalArgumentException("No such user")
    }

    suspend fun login(email: String, password: String): Boolean {
        if (!EmailValidator().matches(email))
            throw IllegalArgumentException("Invalid email")
        val employee = employeeDAO!!.selectByEmail(email)
        return StringHasher(password).equals(employee!!.passHash)
    }
}