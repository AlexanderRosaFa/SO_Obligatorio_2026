/**
 * EstrategiaHibrida — combina criticidad de zona y urgencia temporal.
 *
 * Fórmula: P = (C × Wc + 1/T × Wu) × 100
 *
 *   C  = criticidad de la zona (CRITICA=10, ALTA=7, MEDIA=4, BAJA=1)
 *   T  = ticks restantes hasta impacto (mín=0.5)
 *   Wc = peso de criticidad  (0.60 por defecto)
 *   Wu = peso de urgencia    (0.40 por defecto)
 *
 * Por qué es la estrategia principal:
 *   - Garantiza que zonas críticas sean atendidas con mayor prioridad.
 *   - El componente 1/T evita que misiles a punto de impactar sean
 *     ignorados por tener zona de baja criticidad (starvation temporal).
 *   - Los pesos son configurables: Wc + Wu = 1.0 siempre.
 */
public class EstrategiaHibrida implements EstrategiaInterceptacion {

    private final double Wc; // peso criticidad
    private final double Wu; // peso urgencia

    /** Constructor con pesos por defecto (60% criticidad, 40% urgencia). */
    public EstrategiaHibrida() {
        this(0.60, 0.40);
    }

    public EstrategiaHibrida(double wc, double wu) {
        double suma = wc + wu;
        this.Wc = wc / suma;
        this.Wu = wu / suma;
    }

    @Override
    public double calcularPrioridad(MisilEnemigo misil) {
        double C = misil.getZona().getCriticidad().getValor() / 10.0; // normalizado [0,1]
        double T = Math.max(0.5, misil.getTicksRestantes());

        return (C * Wc + (1.0 / T) * Wu) * 100.0;
    }

    @Override
    public String getNombre() {
        return String.format("HIBRIDA[Wc=%.2f|Wu=%.2f]", Wc, Wu);
    }

    @Override
    public String getFormula() {
        return String.format("P = (C/10 × %.2f + 1/T × %.2f) × 100", Wc, Wu);
    }
}
