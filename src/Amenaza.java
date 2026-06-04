package src;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Amenaza extends Thread implements Comparable<Amenaza> {

    public enum Estado { 
        EN_VUELO, 
        NEUTRALIZADA, 
        IMPACTADA 
    }

    String id;
    Zona   zona;
    int    tiempoVidaMs;    // ms hasta impacto
    long   creadaEnMs;

    // Semáforo de señalización: init=0 → interceptor hace release()
    // para "avisar" que la amenaza fue neutralizada
    Semaphore semSenal = new Semaphore(0);

    volatile Estado  estado   = Estado.EN_VUELO;
    volatile String  interceptorId = "-";
    volatile double  prioridad = 0.0;

    public Amenaza(String id, Zona zona, int tiempoVidaMs) {
        super("Amenaza-" + id);
        this.id           = id;
        this.zona         = zona;
        this.tiempoVidaMs = tiempoVidaMs;
        this.creadaEnMs   = System.currentTimeMillis();
        setDaemon(true);
    }

    @Override
    public void run() {
        zona.registrarDetectada();

        try {
            // tryAcquire(timeout): atomiza "¿llegó la señal antes del impacto?"
            boolean interceptada = semSenal.tryAcquire(tiempoVidaMs, TimeUnit.MILLISECONDS);

            if (interceptada) {
                estado = Estado.NEUTRALIZADA;
                zona.registrarNeutralizada();
            } else {
                estado = Estado.IMPACTADA;
                zona.registrarImpacto();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Llamado por el Interceptor para neutralizar esta amenaza.
    public void neutralizar(String idInterceptor) {
        this.interceptorId = idInterceptor;
        semSenal.release();
    }

    // Tiempo restante estimado en ms. 
    public long getTiempoRestanteMs() {
        long transcurrido = System.currentTimeMillis() - creadaEnMs;
        return Math.max(0, tiempoVidaMs - transcurrido);
    }

    public boolean estaActiva() { 
        return estado == Estado.EN_VUELO; 
    }

    public String getAmenazaId() { 
        return id; 
    }
    public Zona getZona() { 
        return zona; 
    }
    public int getTiempoVidaMs() { 
        return tiempoVidaMs; 
    }
    public Estado getEstado() { 
        return estado; 
    }
    public String getInterceptorId() { 
        return interceptorId; 
    }
    public double getPrioridad() { 
        return prioridad; 
    }
    public void setPrioridad(double p) { 
        this.prioridad = p; 
    }

    @Override
    public int compareTo(Amenaza o) {
        // mayor prioridad primero
        return Double.compare(o.prioridad, this.prioridad);
    }
}
