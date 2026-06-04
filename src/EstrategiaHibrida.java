package src;
/**
 * Estrategia híbrida multi-criterio.
 *
 * P = (C/10 × Wc) + (1/T × Wu) + (1/D × Wd)  × 100
 *
 *   C  = criticidad de la zona  (URGENTE=10, MEDIA=5, BAJA=2)
 *   T  = tiempo restante en ms  (mín=1)
 *   D  = distancia en km        (mín=1)
 *   Wc = 0.50 | Wu = 0.30 | Wd = 0.20
 */
public class EstrategiaHibrida {

    private static final double Wc    = 0.50;
    private static final double Wu    = 0.30;
    private static final double Wd    = 0.20;
    private static final double ESCALA = 100.0;

    public double calcular(Amenaza a) {
        double C = a.getZona().getCriticidad().getValor() / 10.0;
        double T = Math.max(1.0, a.getTiempoRestanteMs());
        double D = Math.max(1.0, a.getZona().getDistanciaKm());

        return ((C * Wc) + ((1.0 / T) * Wu) + ((1.0 / D) * Wd)) * ESCALA;
    }

    public String getFormula() {
        return "P = (C/10×0.50) + (1/T×0.30) + (1/D×0.20)  ×100";
    }
}
