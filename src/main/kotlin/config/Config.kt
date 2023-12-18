package config

import config.PlantGrowthVariant.EQUATOR

data class Config(
  val mapWidth: Int = 100,
  val mapHeight: Int = 30,
  val initialPlants: Int = 300, //startowa liczba roślin
  val nutritionScore: Int = 10, //energia zapewniana przez zjedzenie jednej rośliny,
  val plantsPerDay: Int = 50, //liczba roślin wyrastająca każdego dnia,
  val plantGrowthVariant: PlantGrowthVariant = EQUATOR,
  val initialAnimals: Int = 100,
  val initialAnimalEnergy: Int = 20, //startowa energia zwierzaków,
  val satietyEnergy: Int = 10, //energia konieczna, by uznać zwierzaka za najedzonego (i gotowego do rozmnażania),
  val reproductionEnergyRatio: Double = .5, //proporcja energia rodziców zużywana by stworzyć potomka,
  val minMutations: Int = 0, //minimalna liczba mutacji u potomków (może być równa 0),
  val maxMutations: Int = 8, //maksymalna liczba mutacji u potomków (może być równa 0),
  val mutationVariant: Double = 0.0,// szansa, że geny się zamienią, gdy 0 to wariant domyślny
  val genomeLength: Int = 8,
)
