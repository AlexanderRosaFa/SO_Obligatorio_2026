# Sincronización del Tiempo de Simulación (Mejora Propuesta)

## Problema Detectado

En la implementación actual, el RelojGlobal utiliza `notifyAll()` para despertar a los hilos en cada tick de la simulación.

Sin embargo, el reloj no verifica que todos los hilos hayan terminado de procesar el tick actual antes de avanzar al siguiente.

Esto puede provocar que:

* El reloj avance varios ticks mientras algunos hilos aún procesan eventos anteriores.
* Existan diferencias entre el tiempo lógico de la simulación y el estado real de los hilos.
* Aparezcan comportamientos inconsistentes cuando aumenta la cantidad de amenazas o recursos.

---

## Solución Propuesta: Phaser

Se propone utilizar la clase `Phaser` de Java como barrera de sincronización.

El objetivo es garantizar que todos los hilos completen el procesamiento del tick actual antes de que el reloj avance al siguiente.

---

## Funcionamiento

### Registro de participantes

Cada hilo que participa en la simulación se registra en el Phaser al crearse.

Ejemplos:

* RelojGlobal
* ControladorAliado
* MisilesEnemigos
* Interceptores

Cuando un hilo finaliza definitivamente, se elimina del Phaser.

De esta forma el sistema conoce automáticamente la cantidad de participantes activos.

---

## Avance por ticks

El funcionamiento sería:

Tick N

↓

Todos los hilos ejecutan su trabajo correspondiente al Tick N

↓

Todos llegan a la barrera del Phaser

↓

El Phaser libera a todos los participantes

↓

Comienza el Tick N+1

---

## Ventajas

### Consistencia temporal

Ningún hilo puede quedar procesando el Tick 10 mientras el reloj ya se encuentra en el Tick 15.

### Independencia del tiempo real

La simulación deja de depender de `Thread.sleep()` para representar eventos internos.

### Escalabilidad

La cantidad de hilos puede variar dinámicamente sin necesidad de modificar la lógica del reloj.

### Mayor robustez

Reduce errores de sincronización y facilita la defensa teórica de la solución durante la entrega oral.

---

## Cambio de Enfoque

Actualmente:

* `Thread.sleep()` representa tiempo simulado.
* El reloj avanza independientemente del estado de los demás hilos.

Propuesta:

* El reloj controla el tiempo lógico.
* Los hilos ejecutan acciones por tick.
* El Phaser garantiza que todos finalicen el tick actual antes de avanzar.

Esto transforma la simulación en una simulación por pasos discretos, modelo ampliamente utilizado en sistemas concurrentes y simulaciones de eventos.
