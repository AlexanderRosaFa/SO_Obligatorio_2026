package src;

import java.util.List;
import java.util.Random;

/**
 * Generador de amenazas.
 * Crea misiles a intervalos aleatorios y los deposita en la cola compartida.
 */
public class GeneradorAmenazas extends Thread {

    ColaAmenazas cola;
    List<Zona> zonas;
    int totalAmenazas;
    int intervaloMinMs;
    int intervaloMaxMs;
    int tiempoVidaMinMs;
    int tiempoVidaMaxMs;
    Interceptor.Logger logger;
    Random rng;

    volatile boolean corriendo = true;
    int contador  = 0;

    public GeneradorAmenazas(ColaAmenazas cola, List<Zona> zonas,
                             int totalAmenazas,
                             int intervaloMinMs, int intervaloMaxMs,
                             int tiempoVidaMinMs, int tiempoVidaMaxMs,
                             long seed, Interceptor.Logger logger) {
        super("Generador");
        this.cola = cola;
        this.zonas = zonas;
        this.totalAmenazas = totalAmenazas;
        this.intervaloMinMs = intervaloMinMs;
        this.intervaloMaxMs = intervaloMaxMs;
        this.tiempoVidaMinMs = tiempoVidaMinMs;
        this.tiempoVidaMaxMs = tiempoVidaMaxMs;
        this.logger = logger;
        this.rng = new Random(seed);
        setDaemon(true);
    }

    @Override
    public void run() {
        while (corriendo && contador < totalAmenazas) {
            // Intervalo entre lanzamientos
            int intervalo = intervaloMinMs
                    + rng.nextInt(intervaloMaxMs - intervaloMinMs + 1);
            try { 
                Thread.sleep(intervalo); 
            }
            catch (InterruptedException e) { 
                Thread.currentThread().interrupt(); 
                return; 
            }

            if (!corriendo) 
                break;

            // Crear amenaza
            contador++;
            Zona zona     = seleccionarZona();
            int  vidaMs   = tiempoVidaMinMs
                    + rng.nextInt(tiempoVidaMaxMs - tiempoVidaMinMs + 1);
            String id     = String.format("M-%02d", contador);

            Amenaza amenaza = new Amenaza(id, zona, vidaMs);
            amenaza.start(); // inicia el countdown del misil

            try {
                cola.agregar(amenaza);
                logger.log(String.format(
                    "[GEN] %s detectada -> %-20s | vida=%.1fs | cola=%d",
                    id, zona.getNombre(), vidaMs / 1000.0, cola.getTamanio()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        logger.log("[GEN] Generador finalizado — total=" + contador);
    }

    Zona seleccionarZona() {
        int total = zonas.stream().mapToInt(z -> z.getCriticidad().getValor()).sum();
        int sel   = rng.nextInt(total);
        int acum  = 0;
        for (Zona z : zonas) {
            acum += z.getCriticidad().getValor();
            if (sel < acum) return z;
        }
        return zonas.get(zonas.size() - 1);
    }

    public void detener() { 
        corriendo = false; 
        interrupt(); 
    }
    public int getContador() { 
        return contador; 
    }
}
