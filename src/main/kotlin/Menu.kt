import config.Config

class Menu {
  fun main(args: Array<String>) {

    val config = Config()
    val simulation = Simulation(config)
    simulation.run()
  }
}