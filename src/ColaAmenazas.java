package src;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class ColaAmenazas {

    PriorityQueue<Amenaza> cola;
    EstrategiaHibrida estrategia;

    // Los tres semáforos del patrón clásico
    Semaphore mutex;
    Semaphore llena;
    Semaphore vacia;

    public ColaAmenazas(int capacidad, EstrategiaHibrida estrategia) {
        this.estrategia = estrategia;
        this.cola  = new PriorityQueue<>(capacidad,
                (a, b) -> Double.compare(b.getPrioridad(), a.getPrioridad()));
        this.mutex = new Semaphore(1);
        this.llena = new Semaphore(0);
        this.vacia = new Semaphore(capacidad);
    }

    /** PRODUCTOR: agrega amenaza al buffer. */
    public void agregar(Amenaza a) throws InterruptedException {
        // Calcular prioridad antes de entrar a SC
        a.setPrioridad(estrategia.calcular(a));

        vacia.acquire();          // P(vacia) – espera espacio
        mutex.acquire();          // P(mutex) – entra SC
        cola.add(a);
        mutex.release();          // V(mutex) – sale SC
        llena.release();          // V(llena) – señaliza ítem disponible
    }

    /** CONSUMIDOR: toma amenaza de mayor prioridad con timeout. */
    public Amenaza tomar(long timeoutMs) throws InterruptedException {
        boolean hayItem = llena.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
        if (!hayItem) return null;

        mutex.acquire();          // P(mutex) – entra SC
        recalcularPrioridades();  // prioridades dinámicas
        Amenaza a = cola.poll();
        mutex.release();          // V(mutex) – sale SC
        vacia.release();          // V(vacia) – señaliza espacio libre

        return a;
    }

    /** Recalcula prioridades y reconstruye la cola. Debe llamarse bajo mutex. */
    void recalcularPrioridades() {
        List<Amenaza> tmp = new ArrayList<>(cola);
        cola.clear();
        for (Amenaza a : tmp) {
            if (a.estaActiva()) {
                a.setPrioridad(estrategia.calcular(a));
                cola.add(a);
            }
        }
    }

    /** Limpia amenazas inactivas; ajusta semáforo vacia. */
    public int limpiarInactivas() throws InterruptedException {
        mutex.acquire();
        int antes = cola.size();
        cola.removeIf(a -> !a.estaActiva());
        int eliminadas = antes - cola.size();
        mutex.release();
        if (eliminadas > 0) vacia.release(eliminadas);
        return eliminadas;
    }

    public int  getTamanio() {
        try { mutex.acquire(); int s = cola.size(); mutex.release(); return s; }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); return 0; }
    }
}
