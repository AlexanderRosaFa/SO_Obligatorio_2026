import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * ControladorAliado — planificador y asignador de recursos de defensa.
 *
 * CONSERVA la estructura general del original (espera tick, elige objetivo,
 * evalúa factibilidad, lanza aliado).
 * AGREGA:
 *   - Estrategia de planificación intercambiable (EstrategiaInterceptacion)
 *   - ColaAmenazas con prioridad dinámica (reemplaza la lista lineal)
 *   - Semáforo de recursos disponibles (Semaphore(N))
 *   - Tiempo de recarga fijo gestionado por MisilAliado
 *   - Contador de aliados para IDs únicos
 *   - Estadísticas completas
 *
 * SINCRONIZACIÓN — semáforo de pool de recursos:
 *   semRecursos = Semaphore(N_INTERCEPTORES)
 *
 *   tryAcquire(timeout): adquiere permiso si hay interceptor libre.
 *     → Si hay libre: lanza el aliado de inmediato.
 *     → Si todos ocupados: espera hasta que uno termine la recarga
 *       (el MisilAliado hace release() al final de la recarga).
 *
 * RACE CONDITION RESUELTA:
 *   Entre la extracción de la cola y la asignación, el misil podría
 *   haber impactado ya. Se verifica estaActivo() antes de asignar.
 */
public class ControladorAliado extends Thread {

    // Número de interceptores disponibles simultáneamente.
    // No trivializa el problema: con 2 interceptores y carga alta
    // se produce saturación real.
    private static final int N_INTERCEPTORES = 2;

    private final ColaAmenazas            cola;
    private final RelojGlobal             reloj;
    private final EstrategiaInterceptacion estrategia;

    // Pool de recursos: N permisos = N interceptores disponibles
    // El ControladorAliado hace acquire() antes de lanzar
    // El MisilAliado hace release() al terminar recarga
    private final Semaphore semRecursos = new Semaphore(N_INTERCEPTORES);

    private volatile boolean corriendo     = true;
    private int              contadorAliad = 0;

    // ── Estadísticas ────────────────────────────────────────────────────
    private int asignados      = 0;
    private int descartados    = 0;  // misil ya inactivo cuando se quiso asignar
    private int sinRecursos    = 0;  // no había interceptor libre a tiempo

    public ControladorAliado(ColaAmenazas cola, RelojGlobal reloj,
                             EstrategiaInterceptacion estrategia) {
        super("ControladorAliado");
        this.cola       = cola;
        this.reloj      = reloj;
        this.estrategia = estrategia;
        setDaemon(true);
    }

    @Override
    public void run() {
        System.out.printf("[T=%d] [CTRL] Iniciado | estrategia=%s | recursos=%d | recarga=%d ticks%n",
                reloj.getTiempo(), estrategia.getNombre(),
                N_INTERCEPTORES, MisilAliado.TIEMPO_RECARGA_TICKS);
        System.out.printf("[T=%d] [CTRL] Fórmula: %s%n",
                reloj.getTiempo(), estrategia.getFormula());

        while (corriendo && reloj.estaEjecutando()) {
            try {
                // Limpiar misiles inactivos de la cola
                int eliminados = cola.limpiarInactivos();
                if (eliminados > 0)
                    System.out.printf("[T=%d] [CTRL] Limpieza: %d misiles inactivos removidos%n",
                            reloj.getTiempo(), eliminados);

                // Tomar misil de mayor prioridad (espera hasta 500ms)
                MisilEnemigo objetivo = cola.tomar(500);

                if (objetivo == null) {
                    if (!corriendo || !reloj.estaEjecutando()) break;
                    continue;
                }

                // Verificar que sigue activo (puede haber impactado mientras esperaba)
                if (!objetivo.estaActivo()) {
                    descartados++;
                    System.out.printf("[T=%d] [CTRL] %s ya no activo, descartado%n",
                            reloj.getTiempo(), objetivo.getNombre());
                    continue;
                }

                System.out.printf("[T=%d] [CTRL] Procesando %s → %s | prio=%.2f | libres≈%d%n",
                        reloj.getTiempo(), objetivo.getNombre(),
                        objetivo.getZona().getNombre(),
                        objetivo.getPrioridad(),
                        semRecursos.availablePermits());

                // Esperar interceptor con timeout = tiempo restante del misil
                long timeoutMs = (long) objetivo.getTicksRestantes() * 1000L;
                boolean hayRecurso = semRecursos.tryAcquire(
                        Math.max(500, timeoutMs), TimeUnit.MILLISECONDS);

                if (!hayRecurso) {
                    sinRecursos++;
                    System.out.printf("[T=%d] [CTRL] ⚠ Sin interceptores para %s → probable impacto%n",
                            reloj.getTiempo(), objetivo.getNombre());
                    continue;
                }

                // Doble verificación post-acquire
                if (!objetivo.estaActivo()) {
                    descartados++;
                    semRecursos.release(); // devolver permiso
                    continue;
                }

                // Lanzar misil aliado
                contadorAliad++;
                MisilAliado aliado = new MisilAliado(
                        String.valueOf(contadorAliad),
                        objetivo, reloj, semRecursos
                );
                aliado.start();
                asignados++;

                System.out.printf("[T=%d] [CTRL] Aliado-%d asignado a %s | cola=%d%n",
                        reloj.getTiempo(), contadorAliad,
                        objetivo.getNombre(), cola.getTamanio());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.printf("[T=%d] [CTRL] Finalizado | asignados=%d | descartados=%d | sinRecursos=%d%n",
                reloj.getTiempo(), asignados, descartados, sinRecursos);
    }

    public void detener() { corriendo = false; }

    // ── Estadísticas ────────────────────────────────────────────────────
    public int getAsignados()   { return asignados; }
    public int getDescartados() { return descartados; }
    public int getSinRecursos() { return sinRecursos; }
    public int getNInterceptores() { return N_INTERCEPTORES; }
}
