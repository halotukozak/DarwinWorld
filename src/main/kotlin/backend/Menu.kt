package backend

import backend.config.Config

fun main() {
  val config = Config.default()
  val simulation = Simulation(config)
  simulation.run()
}
