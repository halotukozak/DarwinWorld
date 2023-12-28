import config.Config

fun main() {
  val config = Config()
  val simulation = Simulation(config)
  simulation.run()
}
