package com.carrent.dal

import kotlinx.coroutines.Dispatchers
import com.carrent.model.Employee
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class EmployeeDAO(val database: Database) {
    object EmployeesTable : Table() {
        val name = varchar("name", 255).uniqueIndex()
        val email = varchar("email", 255).uniqueIndex()
        val passhash = varchar("passhash", 255)
        val adminflag = bool("adminflag")

        override val primaryKey = PrimaryKey(email)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) {
            SchemaUtils.setSchema(Schema("software_design"))
            block()
        }

    suspend fun createEmployee(employee: Employee) {
        dbQuery {
            EmployeesTable.insert {
                it[name] = employee.name
                it[email] = employee.email
                it[passhash] = employee.passHash
                it[adminflag] = employee.adminFlag
            }
        }
    }

    suspend fun selectByEmail(email: String): Employee? {
        return dbQuery {
            EmployeesTable.select { EmployeesTable.email eq email }
                .map { Employee(
                    it[EmployeesTable.name],
                    it[EmployeesTable.email],
                    it[EmployeesTable.passhash],
                    it[EmployeesTable.adminflag]
                ) }
                .singleOrNull()
        }
    }

    suspend fun selectByIndex(index: Int): Employee? {
        return dbQuery {
            EmployeesTable.selectAll()
                .map { Employee(
                    it[EmployeesTable.name],
                    it[EmployeesTable.email],
                    it[EmployeesTable.passhash],
                    it[EmployeesTable.adminflag]
                ) }
                .get(index)
        }
    }

    suspend fun selectAll(): List<Employee> {
        return dbQuery {
            EmployeesTable.selectAll()
                .map { Employee(
                    it[EmployeesTable.name],
                    it[EmployeesTable.email],
                    it[EmployeesTable.passhash],
                    it[EmployeesTable.adminflag]
                ) }
        }
    }

    suspend fun updateEmployee(employee: Employee) {
        dbQuery {
            EmployeesTable.update({ EmployeesTable.email eq employee.email }) {
                it[name] = employee.name
                it[passhash] = employee.passHash
                it[adminflag] = employee.adminFlag
            }
        }
    }

    suspend fun deleteEmployee(employee: Employee) {
        dbQuery {
            EmployeesTable.deleteWhere { email.eq(employee.email) }
        }
    }

    suspend fun deleteEmployeeByEmail(email: String) {
        dbQuery {
            EmployeesTable.deleteWhere { EmployeesTable.email.eq(email) }
        }
    }
}