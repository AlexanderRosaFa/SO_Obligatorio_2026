package src;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {

    private static final int RECARGA_NORMAL_MS = 4000;
    private static final int RECARGA_SATURADO_MS = 5000;
    private static final int NEUTRALIZACION_MS = 800;

    private static final Semaphore mutexLog = new Semaphore(1);
    private static PrintWriter logFile;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    static void log(String msg) {
        String linea = "[" + LocalDateTime.now().format(FMT) + "] " + msg;
        try {
            mutexLog.acquire();
            System.out.println(linea);
            if (logFile != null) {
                logFile.println(linea);
                logFile.flush();
            }
            mutexLog.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String escenario = args.length > 0 ? args[0].toLowerCase() : "normal";
        boolean saturado = escenario.equals("saturado");

        // Crear archivo de log
        try {
            new java.io.File("logs").mkdirs();
            logFile = new PrintWriter(new FileWriter("logs/sim_" + escenario + ".log"));
        } catch (IOException e) {
            System.err.println("No se pudo crear log: " + e.getMessage());
        }

        // Configuración del escenario
        int numInterceptores = saturado ? 2 : 3;
        int totalAmenazas = saturado ? 20 : 10;
        int intervaloMinMs = saturado ? 500 : 1000;
        int intervaloMaxMs = saturado ? 1200 : 2000;
        int tiempoVidaMinMs = saturado ? 3000 : 5000;
        int tiempoVidaMaxMs = saturado ? 8000 : 12000;
        int tiempoRecargaMs = saturado ? RECARGA_SATURADO_MS : RECARGA_NORMAL_MS;
        int duracionSimMs = saturado ? 30000 : 28000;
        long seed = saturado ? 999L : 42L;

        // Zonas protegidas
        List<Zona> zonas = new ArrayList<>();
        zonas.add(new Zona("Hospital Principal", Zona.Criticidad.URGENTE, 15.0));
        zonas.add(new Zona("Escuela Central", Zona.Criticidad.URGENTE, 22.0));
        zonas.add(new Zona("Aeropuerto Nacional", Zona.Criticidad.URGENTE, 35.0));
        zonas.add(new Zona("Depósito Militar", Zona.Criticidad.MEDIA, 48.0));
        zonas.add(new Zona("Datacenter Norte", Zona.Criticidad.BAJA, 60.0));
        zonas.add(new Zona("Hospital Secundario", Zona.Criticidad.BAJA, 70.0));

        EstrategiaHibrida estrategia = new EstrategiaHibrida();
        ColaAmenazas cola = new ColaAmenazas(50, estrategia);

        // Pool de interceptores
        Semaphore semInterceptores = new Semaphore(numInterceptores);

        // Interceptores
        List<Interceptor> interceptores = new ArrayList<>();
        for (int i = 1; i <= numInterceptores; i++) {
            Interceptor inter = new Interceptor(
                    String.valueOf(i),
                    NEUTRALIZACION_MS,
                    tiempoRecargaMs,
                    semInterceptores,
                    Main::log
            );
            interceptores.add(inter);
            inter.start();
        }

        // Planificador
        Planificador planificador = new Planificador(
                cola, interceptores, semInterceptores, Main::log);
        planificador.start();

        // Generador de amenazas
        GeneradorAmenazas generador = new GeneradorAmenazas(
                cola, zonas,
                totalAmenazas,
                intervaloMinMs, intervaloMaxMs,
                tiempoVidaMinMs, tiempoVidaMaxMs,
                seed, Main::log);
        generador.start();

        log("Simulación en curso...");
        Thread.sleep(duracionSimMs);

        // Detener simulación
        generador.detener();
        planificador.detener();
        for (Interceptor i : interceptores) {
            i.detener();
        }

        generador.join(2000);
        planificador.join(2000);
        for (Interceptor i : interceptores) {
            i.join(3000);
        }

        imprimirReporte(escenario, zonas, interceptores);

        if (logFile != null) {
            logFile.close();
        }
    }
    private static void imprimirReporte(String escenario, List<Zona> zonas,
                                         List<Interceptor> interceptores) {
        log("═".repeat(60));
        log("  REPORTE FINAL – Escenario: " + escenario.toUpperCase());
        log("═".repeat(60));

        int totalDetectadas = 0, totalImpactos = 0, totalNeutr = 0;

        log(String.format("  %-22s %-8s %-8s %-8s %-8s",
                "Zona", "Critic.", "Detect.", "Neutr.", "Impact."));
        log("  " + "─".repeat(58));

        for (Zona z : zonas) {
            totalDetectadas += z.getDetectadas();
            totalImpactos   += z.getImpactos();
            totalNeutr      += z.getNeutralizadas();
            log(String.format("  %-22s %-8s %-8d %-8d %-8d",
                    z.getNombre(),
                    z.getCriticidad().name(),
                    z.getDetectadas(),
                    z.getNeutralizadas(),
                    z.getImpactos()));
        }

        log("  " + "─".repeat(58));
        log(String.format("  %-22s %-8s %-8d %-8d %-8d",
                "TOTALES", "", totalDetectadas, totalNeutr, totalImpactos));

        double exito = totalDetectadas == 0 ? 100.0
                : (totalNeutr * 100.0) / totalDetectadas;
        log(String.format("%n  Tasa de éxito: %.1f%%", exito));
        log(String.format("  Impactos:      %d / %d amenazas", totalImpactos, totalDetectadas));

        log("\n  Interceptores:");
        for (Interceptor i : interceptores) {
            log("    " + i.getName() + " -> " + i.getIntercepciones() + " intercep.");
        }

        log("\n  Logs guardados en: logs/sim_" + escenario + ".log");
        log("═".repeat(60));
    }
}