import java.util.concurrent.Semaphore;

/**
 * MisilAliado — interceptor. Hilo de defensa.
 *
 * CONSERVA la lógica original: avanza pos += velocidad cada tick
 * hasta alcanzar al enemigo.
 * AGREGA:
 *   - Tiempo fijo de recarga (TIEMPO_RECARGA_TICKS) antes de
 *     quedar disponible de nuevo.
 *   - Semáforo global de recursos (semRecursos) que libera
 *     al terminar la recarga, desbloqueando al ControladorAliado.
 *   - Identificador único para estadísticas.
 *
 * SINCRONIZACIÓN:
 *   semRecursos = Semaphore(N) global — pool de interceptores.
 *   ControladorAliado hace acquire() antes de lanzar.
 *   MisilAliado hace release() al terminar recarga.
 *
 *   Este es el semáforo de conteo de recursos del sistema:
 *   hay exactamente N permisos = N interceptores disponibles.
 */
public class MisilAliado extends Thread {

    /**
     * Tiempo fijo de recarga en ticks.
     * Definido y justificado en el informe (sección 6).
     * Con 3 ticks de recarga y velocidad enemiga=5, la capacidad
     * máxima es aprox. 1 intercepción cada ~4 ticks por recurso.
     */
    public static final int TIEMPO_RECARGA_TICKS = 3;

    private static final int VELOCIDAD = 10; // más rápido que el enemigo (=5)

    private final String        id;
    private final MisilEnemigo  objetivo;
    private final RelojGlobal   reloj;
    private final Semaphore     semRecursos; // pool global de interceptores

    private int posicion = 0;

    public MisilAliado(String id, MisilEnemigo objetivo,
                       RelojGlobal reloj, Semaphore semRecursos) {
        super("Aliado-" + id);
        this.id          = id;
        this.objetivo    = objetivo;
        this.reloj       = reloj;
        this.semRecursos = semRecursos;
        setDaemon(true);
    }

    @Override
    public void run() {
        System.out.printf("[T=%d]   Aliado-%s lanzado -> %s%n",
                reloj.getTiempo(), id, objetivo.getNombre());

        // ── FASE 1: INTERCEPCIÓN ─────────────────────────────────────
        // Avanza tick a tick hasta alcanzar al enemigo (lógica original)
        while (!objetivo.estaDestruido()) {
            boolean sigue = reloj.esperarTick();
            if (!sigue) break;

            posicion += VELOCIDAD;

            int posEnemigo = objetivo.getPosicion();

            System.out.printf("[T=%d]    Aliado-%s pos=%d | enemigo %s pos=%d%n",
                    reloj.getTiempo(), id, posicion,
                    objetivo.getNombre(), posEnemigo);

            if (posicion >= posEnemigo) {
                objetivo.destruir(); // señaliza al hilo MisilEnemigo
                System.out.printf("[T=%d] ⚡ Aliado-%s interceptó a %s en pos=%d%n",
                        reloj.getTiempo(), id, objetivo.getNombre(), posicion);
                break;
            }
        }

        // ── FASE 2: RECARGA ──────────────────────────────────────────
        // Durante la recarga el recurso NO está disponible.
        // Modela el tiempo real de reposición de munición/combustible.
        System.out.printf("[T=%d]  Aliado-%s recargando (%d ticks)...%n",
                reloj.getTiempo(), id, TIEMPO_RECARGA_TICKS);

        for (int i = 0; i < TIEMPO_RECARGA_TICKS; i++) {
            boolean sigue = reloj.esperarTick();
            if (!sigue) break;
        }

        // ── FASE 3: LIBERAR RECURSO ──────────────────────────────────
        // V(semRecursos): desbloquea al ControladorAliado si estaba
        // esperando un interceptor disponible (acquire bloqueado).
        semRecursos.release();
        System.out.printf("[T=%d]  Aliado-%s disponible%n",
                reloj.getTiempo(), id);
    }

    public String getAliado() { return id; }
}
