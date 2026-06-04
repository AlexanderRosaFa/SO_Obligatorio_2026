package src;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Planificador extends Thread {

    private static final long TIMEOUT_MS = 400;

    private final ColaAmenazas cola;
    private final List<Interceptor> interceptores;
    private final Semaphore semInterceptoresDisp;
    private final Interceptor.Logger logger;
    private volatile boolean corriendo = true;

    // Métricas
    private int asignadas = 0;
    private int descartadas = 0;
    private int sinRecursos = 0;

    public Planificador(ColaAmenazas cola, List<Interceptor> interceptores,
                        Semaphore semInterceptoresDisp, Interceptor.Logger logger) {
        super("Planificador");
        this.cola = cola;
        this.interceptores = interceptores;
        this.semInterceptoresDisp = semInterceptoresDisp;
        this.logger = logger;
        setDaemon(true);
        setPriority(Thread.NORM_PRIORITY + 1);
    }

    @Override
    public void run() {
        while (corriendo) {
            try {
                // Limpiar inactivas periódicamente
                int eliminadas = cola.limpiarInactivas();
                if (eliminadas > 0)
                    logger.log("[PLAN] Limpieza: " + eliminadas + " amenazas inactivas removidas");

                // Tomar amenaza de mayor prioridad (bloquea con timeout si vacía)
                Amenaza amenaza = cola.tomar(TIMEOUT_MS);
                if (amenaza == null) { 
                    if (!corriendo) 
                        break; 
                    continue; 
                }

                // Verificar que sigue activa (puede haber impactado mientras esperaba)
                if (!amenaza.estaActiva()) {
                    descartadas++;
                    logger.log("[PLAN] " + amenaza.getAmenazaId() + " ya no activa, descartada");
                    continue;
                }

                logger.log(String.format("[PLAN] Procesando %s -> %s | prio=%.2f | libre = %d",
                        amenaza.getAmenazaId(),
                        amenaza.getZona().getNombre(),
                        amenaza.getPrioridad(),
                        semInterceptoresDisp.availablePermits()));

                // Esperar interceptor disponible (con timeout = tiempo restante de la amenaza)
                long tRestante = amenaza.getTiempoRestanteMs();
                boolean adquirido = semInterceptoresDisp.tryAcquire(tRestante, TimeUnit.MILLISECONDS);

                if (!adquirido) {
                    sinRecursos++;
                    logger.log("[PLAN] Sin interceptores para " + amenaza.getAmenazaId()
                            + " - probable impacto");
                    continue;
                }

                // Doble verificación post-acquire
                if (!amenaza.estaActiva()) {
                    descartadas++;
                    semInterceptoresDisp.release();
                    continue;
                }

                // Asignar al primer interceptor disponible
                Interceptor interceptor = encontrarDisponible();
                if (interceptor == null || !interceptor.asignar(amenaza)) {
                    semInterceptoresDisp.release();
                    logger.log("[PLAN] Fallo al asignar " + amenaza.getAmenazaId());
                    continue;
                }

                asignadas++;
                logger.log("[PLAN] " + amenaza.getAmenazaId() + " -> " + interceptor.getName());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        logger.log("[PLAN] Finalizado | asignadas=" + asignadas
                + " | descartadas=" + descartadas + " | sinRecursos=" + sinRecursos);
    }

    private Interceptor encontrarDisponible() {
        for (Interceptor i : interceptores) {
            if (i.estaDisponible()) return i;
        }
        return null;
    }

    public void detener() { 
        corriendo = false; 
        interrupt(); 
    }
    
    public int getAsignadas() { 
        return asignadas; 
    }
    public int getSinRecursos() { 
        return sinRecursos; 
    }
}
