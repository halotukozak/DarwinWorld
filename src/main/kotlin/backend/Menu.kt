package backend

import backend.config.Config

fun main(args: Array<String>) {
  val config = Config.default()
  val simulation = Simulation(config)
  simulation.run()
}
