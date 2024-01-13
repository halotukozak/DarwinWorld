package backend

import backend.model.Animal
import backend.model.Gen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class StatisticsCollector {
  private val _animalsCounter = MutableStateFlow(0L)
  private val _genomeCounter = MutableStateFlow(Gen.entries.associateWith { 0L })
  val animalsCounter: StateFlow<Long> get() = _animalsCounter
  val genomeCounter: StateFlow<Map<Gen, Long>> get() = _genomeCounter

  fun bornAnimal(animal: Animal) {
    _animalsCounter.update { it + 1 }
    _genomeCounter.update { counter ->
      val change = animal.genome.count()
      counter.mapValues { (gen, count) -> count + change[gen]!! }
    }
  }

  fun dieAnimal(animal: Animal) {
    _animalsCounter.update { it - 1 }
    _genomeCounter.update { counter ->
      val change = animal.genome.count()
      counter.mapValues { (gen, count) -> count - change[gen]!! }
    }
  }

}

