/**
 * ZonaObjetivo — zona crítica a proteger.
 *
 * Cada zona tiene un nivel de criticidad que influye directamente
 * en la prioridad de atención de las amenazas dirigidas a ella.
 *
 * El sistema trabaja con exactamente 6 zonas predefinidas
 * (ver ZonaObjetivo.crearZonas()).
 */
public class ZonaObjetivo {

    /**
     * Nivel de criticidad de la zona.
     * El valor numérico se usa en la fórmula de prioridad.
     *
     *   CRITICA  = 10  → hospitales, escuelas: impacto inmediato en vidas
     *   ALTA     = 7   → aeropuerto, central eléctrica: daño grave a infraestructura
     *   MEDIA    = 4   → planta de agua, zona residencial
     *   BAJA     = 1   → depósito militar, zona industrial: menos población directa
     */
    public enum Criticidad {
        CRITICA(10), ALTA(7), MEDIA(4), BAJA(1);

        private final int valor;
        Criticidad(int v) { this.valor = v; }
        public int getValor() { return valor; }
    }

    private final String     nombre;
    private final Criticidad criticidad;

    // Contadores protegidos por synchronized (acceso desde múltiples hilos)
    private int impactos      = 0;
    private int interceptadas = 0;
    private int detectadas    = 0;

    public ZonaObjetivo(String nombre, Criticidad criticidad) {
        this.nombre      = nombre;
        this.criticidad  = criticidad;
    }

    // ── Acceso concurrente a contadores ────────────────────────────────
    // synchronized: múltiples hilos MisilEnemigo pueden registrar
    // eventos en la misma zona simultáneamente.

    public synchronized void registrarDetectada()   { detectadas++;    }
    public synchronized void registrarImpacto()     { impactos++;      }
    public synchronized void registrarInterceptada(){ interceptadas++; }

    public synchronized int getImpactos()       { return impactos; }
    public synchronized int getInterceptadas()  { return interceptadas; }
    public synchronized int getDetectadas()     { return detectadas; }

    public double getTasaDefensa() {
        int d = getDetectadas();
        return d == 0 ? 1.0 : (double) getInterceptadas() / d;
    }

    public String     getNombre()      { return nombre; }
    public Criticidad getCriticidad()  { return criticidad; }

    @Override
    public String toString() { return nombre + "[" + criticidad + "]"; }

    // ── Zonas predefinidas del sistema ──────────────────────────────────
    public static ZonaObjetivo[] crearZonas() {
        return new ZonaObjetivo[]{
            new ZonaObjetivo("Hospital",         Criticidad.CRITICA),
            new ZonaObjetivo("Escuela",          Criticidad.CRITICA),
            new ZonaObjetivo("Aeropuerto",       Criticidad.ALTA),
            new ZonaObjetivo("CentralElectrica", Criticidad.ALTA),
            new ZonaObjetivo("PlantaDeAgua",     Criticidad.MEDIA),
            new ZonaObjetivo("ZonaResidencial",  Criticidad.MEDIA),
            new ZonaObjetivo("DepositoMilitar",  Criticidad.BAJA),
            new ZonaObjetivo("ZonaIndustrial",   Criticidad.BAJA)
        };
    }
}
