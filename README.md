# DarwinWorld

## Authors

[Bartłomiej Kozak](github.com/halotukozak) & [Bartosz Buczek](https://github.com/Corvette653)

## How to run

```bash
./gradlew run
```

## Modules

### backend

Core module, contains all the logic of the application. Coroutines are used for multithreading.

### frontend

Contains all the code related to the GUI, separated into Views and ViewModels. We use [TornadoFX](https://tornadofx.io/)
as the proxy between required JavaFX library and Kotlin.

### metrics

Custom metrics used for monitoring the application - gathering data about the number of animals, plants, average energy,
etc.

### shared

Common utils which extend the functionality of Kotlin's standard library and Flow.