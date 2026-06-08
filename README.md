# SO_Obligatorio_2026

## A Realizar

Primer Avance 

Para el primer avance, deben entregar por escrito: 

- Una descripción en lenguaje natural del problema desde el punto de vista funcional.	
- Una identificación preliminar de los recursos y procesos o hilos involucrados en la solución.
- Una descripción de los distintos criterios de optimización que podrían usarse.
- Una identificación preliminar de las zonas objetivo a considerar y de cómo podría modelarse su criticidad.
- Una explicación preliminar de cómo se definirá la prioridad de atención a partir de criticidad y urgencia.
- Un ordenamiento justificado de los criterios y una selección de los primeros que el equipo decida optimizar. 

Se les informará si se está de acuerdo con la selección, si deben hacer cambios, o si directamente se cancela el proyecto. 


Descripción del problema:

Nos contacto una empresa de sistemas inteligentes de defensa para proteger ciertas zonas criticas, nuestro trabajo es crear una planificación para cuando ocurra una amenaza poder estar listos ante ese imprevisto y saber como utilizar los recursos que tengamos a disposición. Para eso debemos tener en cuenta algunos factores como: distancia de la amenaza, zona donde va a impactar, poder detectar rapidamente esa amenaza.

Identificación preliminar:

Estan las zonas criticas, los misiles de la amenaza, los misiles nuestros para neutralizar la amenaza, el lugar de donde lanzamos nuestros misiles.
Los misiles son hilos, la zona de los misiles son recursos compartidos porque no existen infinitos lugares ni infinitos misiles a la vez, zona critica solo hay 1 (ciudad en general).

Descripción criterio de optimización:

1 - Que haya la mayor cantidad de amenaza neutralizada, que cuando se detecta una amenaza tratar de combatirla lo antes posible.

2 - Detectar la amenaza guiando simplemente por la cola de prioridad de la zona critica que ataca.

3 - Detectar la amenaza por distancia + prioridad de la zona critica que ataca.

Identificación preliminar zonas objetivo:

- Hospital, podemos en este caso poner 2 criterios, "nuestro" hospital con prioridad baja y uno verdadero con prioridad Urgente
- Escuela, Urgente
- Datacenter, bajo
- Depósito militar, medio
- Aereopuerto, Urgente

Explicación preliminar como se define prioridad:

Dependera del criterio de optimizacion que utilizemos, pero en 2 de los 3 se va a preferir que las zonas criticas con prioridad "Urgente" sean las primeras que puedan salvarse de las amenazas, tomamos como criticidad zonas donde hay niños, medicos y en este caso mucha gente en si, como el aereupuerto porque es un cumulo de personas grande.
