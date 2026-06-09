import java.util.concurrent.Semaphore;

/**
 * RelojGlobal — tick central de la simulación.
 *
 * SINCRONIZACIÓN:
 *   Conserva el mecanismo original (synchronized + notifyAll) para
 *   todos los hilos que usan esperarTick().
 *
 *   Añade un semáforo mutex binario (Semaphore(1)) para proteger
 *   la lectura/escritura compuesta de 'tiempo' en operaciones
 *   donde otro hilo necesita leer + actuar atómicamente.
 *
 * El reloj se detiene al alcanzar TiempoFinal y notifica a todos
 * los hilos en espera para que puedan terminar limpiamente.
 */
public class RelojGlobal extends Thread {

    private volatile int tiempo   = 0;
    private volatile boolean ejecutando = true;
    private final int tiempoFinal;

    // Semáforo mutex para getTiempo() compuesto (leer + comparar atómico)
    private final Semaphore mutexTiempo = new Semaphore(1);

    public RelojGlobal(int tiempoFinal) {
        super("RelojGlobal");
        this.tiempoFinal = tiempoFinal;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (tiempo < tiempoFinal && ejecutando) {
            try {
                Thread.sleep(1000); // 1 segundo real = 1 tick
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            synchronized (this) {
                tiempo++;
                System.out.println("[T=" + tiempo + "] ─────────────────────────────");
                notifyAll(); // despierta a todos los hilos en esperarTick()
            }
        }

        ejecutando = false;
        synchronized (this) { notifyAll(); } // liberar hilos bloqueados al terminar
        System.out.println("[RELOJ] Simulación finalizada en tick=" + tiempo);
    }

    /**
     * Bloquea el hilo llamante hasta el próximo tick.
     * Devuelve false si el reloj ya terminó (señal de cierre limpio).
     */
    public synchronized boolean esperarTick() {
        if (!ejecutando) return false;
        try {
            wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ejecutando;
    }

    /** Lectura simple del tiempo actual (volatile garantiza visibilidad). */
    public int getTiempo() { return tiempo; }

    /** ¿Sigue corriendo el reloj? */
    public boolean estaEjecutando() { return ejecutando; }

    public void detener() {
        ejecutando = false;
        synchronized (this) { notifyAll(); }
    }
}
