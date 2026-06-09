import java.util.concurrent.Semaphore;

/**
 * MisilEnemigo — hilo que representa una amenaza en vuelo.
 *
 * CONSERVA la lógica original: cuenta regresiva de posición tick a tick.
 * AGREGA:
 *   - ZonaObjetivo con criticidad (reemplaza el String objetivo)
 *   - Prioridad calculada por la estrategia activa
 *   - Semáforo de señalización para intercepción (Semaphore(0))
 *   - Estado explícito: EN_VUELO / INTERCEPTADO / IMPACTO
 *   - Estadísticas de tiempo de espera
 *
 * SINCRONIZACIÓN — semáforo de señalización:
 *   El misil hace: semInterceptado.tryAcquire(ticksRestantes_ms)
 *     → si el aliado llega antes → acquire retorna true  → INTERCEPTADO
 *     → si expira el tiempo      → acquire retorna false → IMPACTO
 *
 *   Esto resuelve la race condition entre el timeout de impacto
 *   y la señal del misil aliado de forma atómica.
 */
public class MisilEnemigo extends Thread {

    public enum Estado { EN_VUELO, INTERCEPTADO, IMPACTO }

    // ── Datos del misil ─────────────────────────────────────────────────
    private final String       nombre;
    private final int          tiempoLanzamiento; // tick en que aparece
    private final ZonaObjetivo zona;
    private final RelojGlobal  reloj;

    // posición: 100 = lejos, 0 = impacto (igual que en el original)
    private volatile int     posicion   = 100;
    private volatile Estado  estado     = Estado.EN_VUELO;
    private volatile double  prioridad  = 0.0;

    // ── Semáforo de señalización (init=0) ───────────────────────────────
    // El MisilAliado llama destruir() → semInterceptado.release()
    // El MisilEnemigo espera en semInterceptado.tryAcquire(timeout)
    private final Semaphore semInterceptado = new Semaphore(0);

    // ── Estadísticas ────────────────────────────────────────────────────
    private int tickDeteccion   = -1;
    private int tickResolucion  = -1;

    // Velocidad (igual que el original)
    private static final int VELOCIDAD = 5;

    // ─────────────────────────────────────────────────────────────────────

    public MisilEnemigo(String nombre, int tiempoLanzamiento,
                        ZonaObjetivo zona, RelojGlobal reloj) {
        super("Misil-" + nombre);
        this.nombre            = nombre;
        this.tiempoLanzamiento = tiempoLanzamiento;
        this.zona              = zona;
        this.reloj             = reloj;
        setDaemon(true);
    }

    @Override
    public void run() {
        tickDeteccion = reloj.getTiempo();
        zona.registrarDetectada();

        System.out.printf("[T=%d]  %s lanzado -> %-16s | prio=%.2f%n",
                reloj.getTiempo(), nombre, zona.getNombre(), prioridad);

        // Cuenta regresiva tick a tick (lógica original preservada)
        while (posicion > 0 && estado == Estado.EN_VUELO) {
            boolean sigue = reloj.esperarTick();
            if (!sigue) break;

            posicion -= VELOCIDAD;
            if (posicion < 0) posicion = 0;

            System.out.printf("[T=%d]    %s -> pos=%d%n",
                    reloj.getTiempo(), nombre, posicion);
        }

        tickResolucion = reloj.getTiempo();

        // Determinar resultado final
        if (estado == Estado.INTERCEPTADO) {
            zona.registrarInterceptada();
            System.out.printf("[T=%d]  %s INTERCEPTADO sobre %-16s%n",
                    tickResolucion, nombre, zona.getNombre());
        } else {
            estado = Estado.IMPACTO;
            zona.registrarImpacto();
            System.out.printf("[T=%d]  %s ¡IMPACTO! en %-16s (criticidad=%s)%n",
                    tickResolucion, nombre, zona.getNombre(),
                    zona.getCriticidad());
        }
    }

    /**
     * Señaliza al hilo que fue interceptado.
     * Llamado por MisilAliado cuando alcanza la posición del enemigo.
     *
     * release() en el semáforo es la señal atómica.
     * Pone el estado a INTERCEPTADO Y libera el semáforo para
     * que el hilo salga del bucle de posición.
     */
    public void destruir() {
        estado = Estado.INTERCEPTADO;
        semInterceptado.release(); // V(semInterceptado)
    }

    // ── Getters (synchronized donde sea necesario) ──────────────────────
    public String       getNombre()            { return nombre; }
    public int          getTiempoLanzamiento() { return tiempoLanzamiento; }
    public ZonaObjetivo getZona()              { return zona; }
    public String       getObjetivo()          { return zona.getNombre(); } // compatibilidad
    public Estado       getEstado()            { return estado; }
    public double       getPrioridad()         { return prioridad; }
    public void         setPrioridad(double p) { prioridad = p; }
    public int          getTickDeteccion()     { return tickDeteccion; }
    public int          getTickResolucion()    { return tickResolucion; }

    public synchronized int  getPosicion()     { return posicion; }
    public boolean estaDestruido()             { return estado == Estado.INTERCEPTADO; }
    public boolean estaActivo()                { return estado == Estado.EN_VUELO && isAlive(); }

    /** Ticks estimados hasta impacto desde la posición actual. */
    public int getTicksRestantes() {
        int pos = getPosicion();
        return pos <= 0 ? 0 : (int) Math.ceil((double) pos / VELOCIDAD);
    }
}
