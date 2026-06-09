/**
 * EstrategiaInterceptacion — interfaz de la estrategia de planificación.
 *
 * El patrón Strategy permite cambiar el algoritmo de priorización
 * sin modificar el ControladorAliado.
 *
 * Cada implementación calcula la prioridad de un misil enemigo.
 * Mayor valor = mayor urgencia = se atiende primero.
 */
public interface EstrategiaInterceptacion {

    /**
     * Calcula la prioridad de atención de un misil enemigo.
     *
     * @param misil  el misil a evaluar
     * @return valor de prioridad (mayor = más urgente)
     */
    double calcularPrioridad(MisilEnemigo misil);

    /** Nombre descriptivo para reportes. */
    String getNombre();

    /** Descripción de la fórmula para el informe. */
    String getFormula();
}
