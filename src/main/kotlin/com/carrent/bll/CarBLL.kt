package com.carrent.bll

import com.carrent.dal.CarDAO
import com.carrent.model.Car
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.jetbrains.letsPlot.export.ggsave
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.letsPlot
import java.io.File

object CarBLL {
    var carDAO: CarDAO? = null

    fun init(newCarDAO: CarDAO) {
        carDAO = newCarDAO
    }

    suspend fun getAllCars(): List<Car> {
        return carDAO!!.selectAll()
    }

    suspend fun deleteCarById(id: Int) {
        carDAO!!.deleteCarById(id)
    }

    suspend fun registerNewCar(brand: String, model: String) {
        carDAO!!.createCar(brand, model)
    }

    suspend fun getCarById(id: Int): Car? {
        return carDAO!!.selectById(id)
    }

    suspend fun getCarByBrand(brand: String): List<Car> {
        return carDAO!!.selectByBrand(brand)
    }

    suspend fun getCarByModel(model: String): List<Car> {
        return carDAO!!.selectByModel(model)
    }

    suspend fun updateCarById(car: Car) {
        carDAO!!.updateCar(car)
    }

    suspend fun loadCarsFromFile(filename: String) {
        val inputString: String = File(filename).readText()
        val cars = Json.parseToJsonElement(inputString).jsonArray

        for (i in 0 until cars.size) {
            val car = cars.get(i).jsonObject

            registerNewCar(
                car["brand"]!!.toString().replace("\"", ""),
                car["model"]!!.toString().replace("\"", "")
            )
        }
    }

    suspend fun generateGraph() {
        val allCars = getAllCars()
        val brands = allCars.groupBy { it.brand }
            .mapValues { (_, cars) -> cars.size }
        val data = mapOf(
            "Brand" to brands.keys.toList(),
            "Count" to brands.values.toList()
        )
        val fig = letsPlot(data) +
                geomPoint() { x = "Brand"; y = "Count" }
        ggsave(fig, "plot.png")
    }
}