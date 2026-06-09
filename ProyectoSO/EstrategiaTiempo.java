/**
 * EstrategiaTiempo — prioridad por tiempo restante (menor tiempo = más urgente).
 *
 * Fórmula: P = 1 / ticksRestantes  (× 100 para valores legibles)
 *
 * Análogo al algoritmo SJF/SRTF de planificación de procesos.
 * Minimiza la cantidad total de impactos cuando los misiles tienen
 * tiempos de vuelo similares, pero ignora la importancia de la zona.
 */
public class EstrategiaTiempo implements EstrategiaInterceptacion {

    @Override
    public double calcularPrioridad(MisilEnemigo misil) {
        double ticks = Math.max(0.5, misil.getTicksRestantes());
        return (1.0 / ticks) * 100.0;
    }

    @Override
    public String getNombre() { return "TIEMPO_RESTANTE"; }

    @Override
    public String getFormula() { return "P = (1 / ticksRestantes) × 100"; }
}
