package src;
import java.util.concurrent.Semaphore;

public class Interceptor extends Thread {

    String id;
    int tiempoNeutralizacionMs;
    int tiempoRecargaMs;
    Semaphore semGlobal;

    // Mutex para tareaActual
    Semaphore mutexTarea = new Semaphore(1);
    // Señalización de nueva tarea (init=0 → sin tarea al inicio)
    Semaphore semTareaLista = new Semaphore(0);

    Amenaza tareaActual = null;
    volatile boolean corriendo = true;

    // Estadísticas
    int intercepcionesRealizadas = 0;

    // Logger externo (referencia simple)
    Logger logger;

    public Interceptor(String id, int tiempoNeutralizacionMs,
                       int tiempoRecargaMs, Semaphore semGlobal, Logger logger) {
        super("Interceptor-" + id);
        this.id = id;
        this.tiempoNeutralizacionMs = tiempoNeutralizacionMs;
        this.tiempoRecargaMs = tiempoRecargaMs;
        this.semGlobal = semGlobal;
        this.logger = logger;
        setDaemon(true);
    }

    public void run() {
        logger.log(getName() + " inicializado [DISPONIBLE]");

        while (corriendo) {
            Amenaza tarea = esperarTarea();
            if (tarea == null) break;

            // ── NEUTRALIZACIÓN ────────────────────────────────────────────
            logger.log(getName() + " INTERCEPTANDO " + tarea.getAmenazaId()
                    + " -> " + tarea.getZona().getNombre()
                    + "  (prio=" + String.format("%.2f", tarea.getPrioridad()) + ")");

            tarea.neutralizar(this.id);   // señaliza al hilo Amenaza

            try { 
                Thread.sleep(tiempoNeutralizacionMs); 
            }
            catch (InterruptedException e) { 
                Thread.currentThread().interrupt();
                return; 
            }

            intercepcionesRealizadas++;

            logger.log(getName() + " RECARGANDO (" + (tiempoRecargaMs/1000) + "s)...");
            try { 
                Thread.sleep(tiempoRecargaMs); 
            }
            catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
                return; 
            }

            logger.log(getName() + " DISPONIBLE  [intercep=" + intercepcionesRealizadas + "]");
            semGlobal.release();          // V(semGlobal) → desbloquea al Planificador
        }
    }

    /**
     * Espera tarea:
     *   A(semTareaLista) → bloquea hasta señal del Planificador
     *   A(mutexTarea)    → entra Seccion Critica
     *   lee tareaActual
     *   R(mutexTarea)    → sale Seccion Critica
     */
    Amenaza esperarTarea() {
        try {
            semTareaLista.acquire();     // A(semTareaLista)
            if (!corriendo) return null;

            mutexTarea.acquire();        // A(mutexTarea)
            Amenaza t = tareaActual;
            tareaActual = null;
            mutexTarea.release();        // R(mutexTarea)
            return t;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Asigna tarea (llamado por Planificador):
     *   A(mutexTarea)    → entra Seccion Critica
     *   escribe tareaActual
     *   R(mutexTarea)    → sale Seccion Critica
     *   R(semTareaLista) → señaliza al interceptor
     */
    public boolean asignar(Amenaza a) {
        if (!estaDisponible()) 
            return false;
        try {
            mutexTarea.acquire();        // A(mutexTarea)
            if (tareaActual != null) { 
                mutexTarea.release(); 
                return false; 
            }
            tareaActual = a;
            mutexTarea.release();        // R(mutexTarea)
            semTareaLista.release();     // R(semTareaLista) → despierta interceptor
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void detener() {
        corriendo = false;
        semTareaLista.release();         // desbloquear si espera
    }

    public boolean estaDisponible() {
        return tareaActual == null
                && !getName().contains("RECARGANDO")
                && semTareaLista.availablePermits() == 0;
    }

    public String getInterceptorId2() { 
        return id; 
    }
    public int getIntercepciones() { 
        return intercepcionesRealizadas; 
    }

    /** Interfaz funcional para logging desacoplado. */
    @FunctionalInterface
    public interface Logger {
        void log(String mensaje);
    }
}
