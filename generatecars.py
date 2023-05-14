import random as r

carBrands = ['Acura', 'Alfa Romeo', 'Aston Martin', 'Audi', 'Bentley', 'BMW', 'Bugatti', 'Buick', 'Cadillac', 'Chevrolet', 'Chrysler', 'Citroen', 'Dodge', 'Ferrari', 'Fiat', 'Ford', 'Geely', 'Genesis', 'GMC', 'Honda', 'Hyundai', 'Infiniti', 'Jaguar', 'Jeep', 'Kia', 'Koenigsegg', 'Lamborghini', 'Land Rover', 'Lexus', 'Lincoln', 'Lotus', 'Maserati', 'Mazda', 'McLaren', 'Mercedes-Benz', 'Mini', 'Mitsubishi', 'Nissan', 'Pagani', 'Peugeot', 'Polestar', 'Porsche', 'Ram', 'Renault', 'Rolls-Royce', 'Saab', 'Subaru', 'Suzuki', 'Tata Motors', 'Tesla', 'Toyota', 'Volkswagen', 'Volvo', 'Alpine', 'Aviar Motors', 'BAC', 'Bollinger Motors', 'BYD', 'Czinger', 'De Tomaso', 'DeltaWing Racing Cars', 'Faraday Future', 'Fisker Inc', 'Hennessey Performance Engineering', 'Karma Automotive', 'Lucid Motors', 'Mahindra Automotive', 'Mansory', 'NanoFlowcell', 'Nikola Corporation', 'Pininfarina', 'Rivian', 'Ruf Automobile', 'Rimac Automobili', 'Saleen', 'Singer Vehicle Design', 'Sono Motors', 'Spyker', 'TVR', 'Vanderhall Motor Works', 'Venturi', 'Zenvo']
carModels = ['Accord', 'Altima', 'Avalon', 'Camaro', 'Camry', 'Charger', 'Civic', 'Corolla', 'Cruze', 'Civic', 'Elantra', 'Explorer', 'Focus', 'Fusion', 'Impala', 'Jetta', 'Lancer', 'Malibu', 'Maxima', 'Mazda3', 'Model S', 'Model 3', 'Model X', 'Mustang', 'Odyssey', 'Optima', 'Outback', 'Pacifica', 'Passat', 'Pathfinder', 'Prius', 'Q50', 'Q60', 'Ranger', 'Rav4', 'Sentra', 'Sienna', 'Sonata', 'Spark', 'Tacoma', 'Tahoe', 'Taurus', 'Trax', 'Venza', 'Volt', 'Wrangler', 'X5', 'Yaris', 'Z4']

with open("cars.txt", "w") as fo:
	fo.write("[\n")
	for sample in range(200):
		brandIdx = r.randrange(0, len(carBrands))
		modelIdx = r.randrange(0, len(carModels))
		fo.write(f'{{"brand": "{carBrands[brandIdx]}", "model": "{carModels[modelIdx]}"}}')
		if sample != 199:
			fo.write(",")
		else:
			fo.write("]")

