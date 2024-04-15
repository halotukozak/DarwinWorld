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

<img width="423" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/da71f678-9030-45ce-86e8-7cb39d77bc71">
<img width="425" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/83e629be-b116-4692-bbb2-9b8e4d4f81fb">
<img width="424" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/2a261472-fbbe-4262-857d-abff9e5b19a2">
<img width="802" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/1afd79aa-63c1-4b55-a435-772faaa53edc">
<img width="802" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/d5af1888-979c-4269-9500-300fb54287df">
<img width="1303" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/4b5d86b5-070a-408e-932a-17f3bcff3290">
<img width="1301" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/afe07228-26b0-49ef-bdee-5f205c951692">
<img width="1306" alt="image" src="https://github.com/halotukozak/DarwinWorld/assets/64359735/8d60f7e7-cfb0-4052-91f7-0e876e9a1dfe">

