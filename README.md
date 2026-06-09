
# Sistema Concurrente de Intercepción de Amenazas Aéreas

## Resumen General

El sistema simula la detección e intercepción de amenazas aéreas dirigidas a distintas zonas protegidas. Las amenazas aparecen durante la simulación, son evaluadas según su prioridad y pueden ser interceptadas por recursos limitados antes de impactar sobre sus objetivos.

La solución utiliza concurrencia mediante múltiples hilos que representan amenazas, controladores y mecanismos de control temporal.

---

# Arquitectura Actual

## Hilos Existentes

### RelojGlobal

Responsable de avanzar el tiempo de la simulación y sincronizar el comportamiento general del sistema.

### MisilEnemigo

Cada amenaza posee su propio hilo. Sus responsabilidades son:

* Esperar su instante de aparición.
* Activarse cuando corresponda.
* Reducir su tiempo restante hasta el impacto.
* Informar si fue interceptada o impactó.

### ControladorAliado

Actúa como planificador principal del sistema.

Responsabilidades:

* Analizar amenazas activas.
* Seleccionar cuál debe ser atendida.
* Crear los hilos de intercepción.

### MisilAliado

Hilo creado por el controlador para interceptar una amenaza específica.

---

# Recursos Compartidos

## Lista de amenazas

Contiene todas las amenazas activas del sistema.

Es utilizada simultáneamente por:

* Misiles enemigos.
* Controlador.
* Misiles aliados.

## Tiempo global

Valor compartido utilizado por todos los hilos para coordinar sus acciones.

## Estadísticas

Contadores globales utilizados para registrar:

* Amenazas generadas.
* Amenazas interceptadas.
* Amenazas impactadas.
* Otras métricas de la simulación.

---

# Competencia Entre Hilos

## MisilEnemigo vs Controlador

Ambos acceden a la lista de amenazas.

Problema:
Mientras el controlador analiza la lista, una nueva amenaza puede ser agregada simultáneamente.

## MisilEnemigo vs MisilAliado

Ambos modifican el estado de una amenaza.

Problema:
Una amenaza podría impactar al mismo tiempo que un interceptor intenta destruirla.

## Todos los hilos vs Estadísticas

Varios hilos pueden actualizar contadores simultáneamente.

Problema:
Pueden perderse actualizaciones si no existe sincronización.

---

# Requisitos Pendientes Importantes

## 1. Criticidad de zonas

Implementar una clase Zona con niveles de criticidad.

Ejemplo:

* Hospital = 100
* Central Eléctrica = 90
* Aeropuerto = 80
* Escuela = 70
* Zona Industrial = 50

---

## 2. Prioridad combinada

La prioridad debe considerar:

* Criticidad de la zona.
* Tiempo restante hasta el impacto.

La fórmula debe ser definida y justificada.

---

## 3. Múltiples estrategias de planificación

Implementar al menos:

### Estrategia 1

Menor tiempo hasta el impacto.

### Estrategia 2

Mayor criticidad.

### Estrategia 3

Prioridad combinada.

El usuario debe poder elegir cuál utilizar.

---

## 4. Recursos limitados de intercepción

Actualmente los interceptores se crean bajo demanda.

Para cumplir completamente con la letra debería existir una cantidad fija de recursos de intercepción.

Ejemplo:

* Interceptor 1
* Interceptor 2
* Interceptor 3

Cada uno atendiendo una sola amenaza a la vez.

---

## 5. Tiempo de recarga

Después de una intercepción el recurso debe permanecer ocupado durante un tiempo fijo antes de volver a estar disponible.

---

## 6. Estados de las amenazas

Implementar estados explícitos:

* PENDIENTE
* ASIGNADA
* INTERCEPTADA
* IMPACTADA

---

## 7. Cola de prioridad

Reemplazar listas simples por una estructura de prioridad que permita seleccionar amenazas según la estrategia elegida.

---

## 8. Registro de eventos

Generar un archivo de log.

Ejemplos:

* Aparición de amenazas.
* Asignaciones.
* Intercepciones.
* Impactos.

---

## 9. Estadísticas finales

Mostrar al finalizar:

* Amenazas generadas.
* Amenazas interceptadas.
* Amenazas impactadas.
* Tiempo promedio de espera.
* Utilización de recursos.
* Métricas asociadas a criticidad.

---

# Posible Evolución de la Arquitectura

Versión actual:

* RelojGlobal
* ControladorAliado
* MisilesEnemigos
* MisilesAliados creados bajo demanda

Versión recomendada:

* RelojGlobal
* ControladorAliado
* MisilesEnemigos
* Interceptor 1
* Interceptor 2
* Interceptor 3

Esta segunda alternativa representa mejor la limitación real de recursos exigida por el obligatorio.

---

# Aspectos Clave para Defender en la Entrega

* Uso real de concurrencia.
* Existencia de recursos compartidos.
* Necesidad de sincronización.
* Competencia por recursos.
* Planificación de amenazas.
* Uso de prioridades.
* Comparación entre estrategias.
* Medición estadística de resultados.


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
