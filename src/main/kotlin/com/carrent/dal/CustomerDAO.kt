package com.carrent.dal

import kotlinx.coroutines.Dispatchers
import com.carrent.model.Customer
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class CustomerDAO(private val database: Database) {
    object CustomersTable : Table() {
        val name = varchar("name", 255).uniqueIndex()
        val email = varchar("email", 255).uniqueIndex()
        val phone = varchar("phone", 255).uniqueIndex()
        val passhash = varchar("passhash", 255)

        override val primaryKey = PrimaryKey(email)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) {
            SchemaUtils.setSchema(Schema("software_design"))
            block()
        }

    suspend fun createCustomer(customer: Customer) {
        dbQuery {
            CustomersTable.insert {
                it[name] = customer.name
                it[email] = customer.email
                it[phone] = customer.phone
                it[passhash] = customer.passHash
            }
        }
    }

    suspend fun selectByEmail(email: String): Customer? {
        return dbQuery {
            CustomersTable.select { CustomersTable.email eq email }
                .map { Customer(
                    it[CustomersTable.name],
                    it[CustomersTable.email],
                    it[CustomersTable.phone],
                    it[CustomersTable.passhash]
                ) }
                .singleOrNull()
        }
    }

    suspend fun selectByIndex(index: Int): Customer? {
        return dbQuery {
            CustomersTable.selectAll()
                .map { Customer(
                    it[CustomersTable.name],
                    it[CustomersTable.email],
                    it[CustomersTable.phone],
                    it[CustomersTable.passhash]
                ) }
                .get(index)
        }
    }

    suspend fun selectAll(): List<Customer> {
        return dbQuery {
            CustomersTable.selectAll()
                .map { Customer(
                    it[CustomersTable.name],
                    it[CustomersTable.email],
                    it[CustomersTable.phone],
                    it[CustomersTable.passhash]
                ) }
        }
    }

    suspend fun updateCustomer(customer: Customer) {
        dbQuery {
            CustomersTable.update({ CustomersTable.email eq customer.email }) {
                it[name] = customer.name
                it[phone] = customer.phone
                it[passhash] = customer.passHash
            }
        }
    }

    suspend fun deleteCustomer(customer: Customer) {
        dbQuery {
            CustomersTable.deleteWhere { email.eq(customer.email) }
        }
    }

    suspend fun deleteCustomerByEmail(email: String) {
        dbQuery {
            CustomersTable.deleteWhere { CustomersTable.email.eq(email) }
        }
    }
}