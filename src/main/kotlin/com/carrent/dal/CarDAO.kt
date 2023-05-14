package com.carrent.dal

import kotlinx.coroutines.Dispatchers
import com.carrent.model.Car
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class CarDAO(private val database: Database) {
    object CarsTable : Table() {
        val id = integer("id").autoIncrement()
        val brand = varchar("brand", 255)
        val model = varchar("model", 255)

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) {
            SchemaUtils.setSchema(Schema("software_design"))
            block()
        }

    suspend fun createCar(carBrand: String, carModel: String): Int {
        return dbQuery {
            CarsTable.insert {
                it[brand] = carBrand
                it[model] = carModel
            } get CarsTable.id
        }
    }

    suspend fun selectById(id: Int): Car? {
        return dbQuery {
            CarsTable.select { CarsTable.id eq id }
                .map {
                    Car(
                        it[CarsTable.id],
                        it[CarsTable.brand],
                        it[CarsTable.model]
                    )
                }.singleOrNull()
        }
    }

    suspend fun selectByBrand(brand: String): List<Car> {
        return dbQuery {
            CarsTable.select { CarsTable.brand eq brand }
                .map { Car(
                    it[CarsTable.id],
                    it[CarsTable.brand],
                    it[CarsTable.model]
                ) }
        }
    }

    suspend fun selectByModel(model: String): List<Car> {
        return dbQuery {
            CarsTable.select { CarsTable.model eq model }
                .map {
                    Car(
                        it[CarsTable.id],
                        it[CarsTable.brand],
                        it[CarsTable.model]
                    )
                }
        }
    }

    suspend fun selectAll(): List<Car> {
        return dbQuery {
            CarsTable.selectAll()
                .map {
                    Car(
                        it[CarsTable.id],
                        it[CarsTable.brand],
                        it[CarsTable.model]
                    )
                }
        }
    }

    suspend fun updateCar(car: Car) {
        dbQuery {
            CarsTable.update({ CarsTable.id eq car.id }) {
                it[brand] = car.brand
                it[model] = car.model
            }
        }
    }

    suspend fun deleteCarById(id: Int) {
        dbQuery {
            CarsTable.deleteWhere { CarsTable.id.eq(id) }
        }
    }
}