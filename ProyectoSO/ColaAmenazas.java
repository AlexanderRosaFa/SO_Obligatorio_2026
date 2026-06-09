import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * ColaAmenazas — buffer compartido productor-consumidor.
 *
 * Patrón clásico de Dijkstra con tres semáforos:
 *
 *   mutex  = Semaphore(1)   → exclusión mutua sobre la PriorityQueue
 *   llena  = Semaphore(0)   → cuenta misiles en la cola
 *   vacia  = Semaphore(CAP) → cuenta espacios libres
 *
 * PRODUCTOR (CreadorDeEnemigos): agregar()
 *   P(vacia) → P(mutex) → add() → V(mutex) → V(llena)
 *
 * CONSUMIDOR (ControladorAliado): tomar()
 *   P(llena,timeout) → P(mutex) → poll() → V(mutex) → V(vacia)
 *
 * El reordenamiento dinámico recalcula prioridades antes de cada
 * extracción (equivalente al aging en schedulers de SO).
 */
public class ColaAmenazas {

    private final int capacidad;

    // Cola interna: NO thread-safe → toda op. bajo mutex
    private final PriorityQueue<MisilEnemigo> cola;

    // Los tres semáforos del patrón clásico
    private final Semaphore mutex;  // exclusión mutua
    private final Semaphore llena;  // ítems disponibles
    private final Semaphore vacia;  // espacios libres

    private EstrategiaInterceptacion estrategia;

    // Estadísticas
    private int totalAgregados    = 0;
    private int totalExtraidos    = 0;
    private int totalReordenamientos = 0;

    public ColaAmenazas(int capacidad, EstrategiaInterceptacion estrategia) {
        this.capacidad  = capacidad;
        this.estrategia = estrategia;
        // Mayor prioridad primero
        this.cola  = new PriorityQueue<>(capacidad,
                (a, b) -> Double.compare(b.getPrioridad(), a.getPrioridad()));
        this.mutex = new Semaphore(1);
        this.llena = new Semaphore(0);
        this.vacia = new Semaphore(capacidad);
    }

    /**
     * PRODUCTOR: deposita misil en la cola.
     * P(vacia) → P(mutex) → add() → V(mutex) → V(llena)
     */
    public void agregar(MisilEnemigo misil) throws InterruptedException {
        // Calcular prioridad inicial antes de entrar a sección crítica
        misil.setPrioridad(estrategia.calcularPrioridad(misil));

        vacia.acquire();          // P(vacia) — espera espacio libre
        mutex.acquire();          // P(mutex) — entra a sección crítica
        cola.add(misil);
        totalAgregados++;
        mutex.release();          // V(mutex) — sale de sección crítica
        llena.release();          // V(llena) — señaliza ítem disponible
    }

    /**
     * CONSUMIDOR: toma misil de mayor prioridad.
     * P(llena,timeout) → P(mutex) → poll() → V(mutex) → V(vacia)
     *
     * @return misil de mayor prioridad, o null si timeout
     */
    public MisilEnemigo tomar(long timeoutMs) throws InterruptedException {
        // P(llena,timeout): bloquea si cola vacía hasta que llegue un misil
        if (!llena.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS)) return null;

        mutex.acquire();                   // P(mutex) — entra a sección crítica
        recalcularPrioridades();           // actualizar antes de tomar
        MisilEnemigo misil = cola.poll();
        if (misil != null) totalExtraidos++;
        mutex.release();                   // V(mutex) — sale de sección crítica
        vacia.release();                   // V(vacia) — señaliza espacio libre

        return misil;
    }

    /**
     * Recalcula prioridades de todos los misiles en la cola.
     * Debe llamarse bajo mutex. Descarta misiles ya resueltos.
     */
    private void recalcularPrioridades() {
        if (cola.isEmpty()) return;
        List<MisilEnemigo> tmp = new ArrayList<>(cola);
        cola.clear();
        for (MisilEnemigo m : tmp) {
            if (m.estaActivo()) {
                m.setPrioridad(estrategia.calcularPrioridad(m));
                cola.add(m);
            }
            // misiles ya resueltos se descartan
        }
        totalReordenamientos++;
    }

    /** Limpia misiles inactivos y devuelve cuántos se eliminaron. */
    public int limpiarInactivos() throws InterruptedException {
        mutex.acquire();
        int antes = cola.size();
        cola.removeIf(m -> !m.estaActivo());
        int eliminados = antes - cola.size();
        mutex.release();
        if (eliminados > 0) vacia.release(eliminados);
        return eliminados;
    }

    /** Cambia la estrategia en caliente y recalcula. */
    public void cambiarEstrategia(EstrategiaInterceptacion nueva)
            throws InterruptedException {
        mutex.acquire();
        this.estrategia = nueva;
        recalcularPrioridades();
        mutex.release();
    }

    public int getTamanio() {
        try {
            mutex.acquire();
            int s = cola.size();
            mutex.release();
            return s;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return 0;
        }
    }

    public int getTotalAgregados()       { return totalAgregados; }
    public int getTotalExtraidos()       { return totalExtraidos; }
    public int getTotalReordenamientos() { return totalReordenamientos; }
}
