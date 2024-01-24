package backend.statistics

import backend.config.Config
import backend.model.Animal
import backend.model.Gen
import backend.model.Genome
import javafx.scene.chart.PieChart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import metrics.Day
import metrics.collector.Collector
import metrics.collector.MutableCollector
import metrics.counter.ACounter
import metrics.counter.Counter
import metrics.counter.MutableACounter
import metrics.counter.MutableCounter
import metrics.math.*
import kotlin.collections.component1
import kotlin.collections.component2


class StatisticsService(simulationConfig: Config) {

  private val simulationExporter by lazy { SimulationExporter(simulationConfig) }
  private val isCsvExportEnabled = simulationConfig.csvExportEnabled

  val range = 20

  val isBirthsMetricsEnabled = simulationConfig.births
  private val _birthMetrics by lazy { MutableACounter<Int>(range) }
  private val _minBirthMetrics by lazy(::MutableAMinimumMetrics)
  private val _maxBirthMetrics by lazy(::MutableAMaximumMetrics)
  private val _avgBirthMetrics by lazy(::MutableAAverageMetrics)
  val birthMetrics: ACounter<Int> by lazy { _birthMetrics }
  val birthTripleMetrics by lazy {
    combine(_minBirthMetrics, _maxBirthMetrics, _avgBirthMetrics, ::Triple)
  }

  val isDeathsMetricsEnabled = simulationConfig.deaths
  private val _deathMetrics by lazy { MutableACounter<Int>(range) }
  private val _minDeathMetrics by lazy(::MutableAMinimumMetrics)
  private val _maxDeathMetrics by lazy(::MutableAMaximumMetrics)
  private val _avgDeathMetrics by lazy(::MutableAAverageMetrics)
  val deathMetrics: ACounter<Int> by lazy { _deathMetrics }
  val deathTripleMetrics by lazy {
    combine(_minDeathMetrics, _maxDeathMetrics, _avgDeathMetrics, ::Triple)
  }

  val isPopulationMetricsEnabled = simulationConfig.population
  private val _populationMetrics by lazy { MutableCounter<Int>(range) }
  private val _minPopulationMetrics by lazy(::MutableMinimumMetrics)
  private val _maxPopulationMetrics by lazy(::MutableMaximumMetrics)
  private val _avgPopulationMetrics by lazy(::MutableAverageMetrics)
  val populationMetrics: Counter<Int> by lazy { _populationMetrics }
  val populationTripleMetrics: Flow<MinMaxAvgTriple> by lazy {
    combine(_minPopulationMetrics, _maxPopulationMetrics, _avgPopulationMetrics, ::Triple)
  }

  val isPlantDensityMetricsEnabled = simulationConfig.plantDensity
  private val _plantDensityMetrics by lazy { MutableCounter<Int>(range) }
  private val _minPlantDensityMetrics by lazy(::MutableMinimumMetrics)
  private val _maxPlantDensityMetrics by lazy(::MutableMaximumMetrics)
  private val _avgPlantDensityMetrics by lazy(::MutableAverageMetrics)
  val plantDensityMetrics: Counter<Int> by lazy { _plantDensityMetrics }
  val plantDensityTriple: Flow<MinMaxAvgTriple> by lazy {
    combine(_minPlantDensityMetrics, _maxPlantDensityMetrics, _avgPlantDensityMetrics, ::Triple)
  }

  val isDailyAverageAgeMetricsEnabled = simulationConfig.dailyAverageAge
  private val _dailyAverageAgeMetrics by lazy { MutableCounter<Double>(range) }
  private val _minDailyAverageAgeMetrics by lazy(::MutableMinimumMetrics)
  private val _maxDailyAverageAgeMetrics by lazy(::MutableMaximumMetrics)
  private val _avgDailyAverageAgeMetrics by lazy(::MutableAverageMetrics)
  val dailyAverageAgeMetrics: Counter<Double> by lazy { _dailyAverageAgeMetrics }
  val dailyAverageAgeTriple: Flow<MinMaxAvgTriple> by lazy {
    combine(_minDailyAverageAgeMetrics, _maxDailyAverageAgeMetrics, _avgDailyAverageAgeMetrics, ::Triple)
  }

  val isDailyAverageEnergyMetricsEnabled = simulationConfig.dailyAverageEnergy
  private val _dailyAverageEnergyMetrics by lazy { MutableCounter<Double>(range) }
  private val _minDailyAverageEnergyMetrics by lazy(::MutableMinimumMetrics)
  private val _maxDailyAverageEnergyMetrics by lazy(::MutableMaximumMetrics)
  private val _avgDailyAverageEnergyMetrics by lazy(::MutableAverageMetrics)
  val dailyAverageEnergyMetrics: Counter<Double> by lazy { _dailyAverageEnergyMetrics }
  val dailyAverageEnergyTriple: Flow<MinMaxAvgTriple> by lazy {
    combine(_minDailyAverageEnergyMetrics, _maxDailyAverageEnergyMetrics, _avgDailyAverageEnergyMetrics, ::Triple)
  }

  val isGenCollectorEnabled = simulationConfig.gens
  private val _genCollector by lazy { MutableCollector<Gen>(range) }
  val genCollector: Collector<Gen> by lazy { _genCollector }

  val presentGens by lazy {
    genCollector.map {
      it.lastOrNull()?.second?.sortedBy { it.first }?.map { (gen, count) ->
        PieChart.Data(gen.name, count.toDouble())
      }
    }
  }

  val isGenomeCollectorEnabled = simulationConfig.genomes
  private val _genomeCollector by lazy { MutableCollector<Genome>(range) }
  val genomeCollector: Collector<Genome> by lazy { _genomeCollector }

  fun registerBirth(day: Day) {
    if (isBirthsMetricsEnabled) {
      _birthMetrics.register(day, 1)
      _minBirthMetrics.register(day, 1)
      _maxBirthMetrics.register(day, 1)
      _avgBirthMetrics.register(day, 1)
    }
  }

  fun registerDeath(day: Day, animals: Int) {
    if (isDeathsMetricsEnabled) {
      _deathMetrics.register(day, animals)
      _minDeathMetrics.register(day, animals)
      _maxDeathMetrics.register(day, animals)
      _avgDeathMetrics.register(day, animals)
    }
  }

  private fun registerPlants(n: Int) {
    if (isPlantDensityMetricsEnabled) {
      _plantDensityMetrics.register(n)
      _minPlantDensityMetrics.register(n)
      _maxPlantDensityMetrics.register(n)
      _avgPlantDensityMetrics.register(n)
    }
  }

  private fun registerAnimals(animals: List<Animal>) {
    if (isPopulationMetricsEnabled) {
      _populationMetrics.register(animals.size)
      _minPopulationMetrics.register(animals.size)
      _maxPopulationMetrics.register(animals.size)
      _avgPopulationMetrics.register(animals.size)
    }
    if (isDailyAverageAgeMetricsEnabled) {
      val avg = animals.map(Animal::age).average()
      _dailyAverageAgeMetrics.register(avg)
      _minDailyAverageAgeMetrics.register(avg)
      _maxDailyAverageAgeMetrics.register(avg)
      _avgDailyAverageAgeMetrics.register(avg)
    }
    if (isDailyAverageEnergyMetricsEnabled) {
      val avg = animals.map(Animal::energy).average()
      _dailyAverageEnergyMetrics.register(avg)
      _minDailyAverageEnergyMetrics.register(avg)
      _maxDailyAverageEnergyMetrics.register(avg)
      _avgDailyAverageEnergyMetrics.register(avg)
    }

    if (isGenCollectorEnabled)
      _genCollector.register(
        animals
          .flatMap { it.genome.frequencyMap.toList() }
          .groupBy({ it.first }, { it.second })
          .map { (key, values) -> key to values.sum() }
      )
    if (isGenomeCollectorEnabled)
      _genomeCollector.register(
        animals
          .map(Animal::genome)
          .groupingBy { it }
          .eachCount()
          .toList()
          .sortedByDescending { it.second })
  }

  fun registerEndOfDay(day: Day, plants: Int, animals: List<Animal>) {
    registerPlants(plants)
    registerAnimals(animals)

    if (isCsvExportEnabled) export(day)
  }

  private fun export(day: Day) = simulationExporter.writeCsv(
    DailyStatistics(
      day = day,
      births = _birthMetrics.value.lastOrNull()?.second,
      deaths = _deathMetrics.value.lastOrNull()?.second,
      population = _populationMetrics.value.lastOrNull()?.second,
      plantDensity = _plantDensityMetrics.value.lastOrNull()?.second,
      averageAge = _dailyAverageAgeMetrics.value.lastOrNull()?.second,
      averageEnergy = _dailyAverageEnergyMetrics.value.lastOrNull()?.second,
      mostPopularGen = _genCollector.value.lastOrNull()?.second?.maxByOrNull { it.second }?.first,
      mostPopularGenome = _genomeCollector.value.lastOrNull()?.second?.maxByOrNull { it.second }?.first,
    )
  )

}

typealias MinMaxAvgTriple = Triple<Double, Double, Double>
