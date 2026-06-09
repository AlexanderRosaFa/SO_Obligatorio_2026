import java.util.List;

/**
 * Escenarios:
 *   normal   — 3 misiles del archivo + reloj de 30 ticks
 *   saturado — misiles del archivo + extras aleatorios + reloj de 50 ticks
 *
 * Estrategias disponibles:
 *   tiempo   — prioridad por menor tiempo restante (STRF)
 *   hibrida  — combinación criticidad + urgencia (RECOMENDADA)
 *   ambas    — ejecuta las dos y compara resultados
 */
public class Main {

    public static void main(String[] args) throws InterruptedException {

        String escenario = args.length > 0 ? args[0].toLowerCase() : "saturado";
        String estratNom = args.length > 1 ? args[1].toLowerCase() : "tiempo";

        if (estratNom.equals("ambas")) {
            // Ejecutar ambas estrategias y mostrar comparativa
            System.out.println("\n>>> ESTRATEGIA: TIEMPO_RESTANTE <<<\n");
            ejecutar(escenario, new EstrategiaTiempo());
            Thread.sleep(1000);
            System.out.println("\n>>> ESTRATEGIA: HÍBRIDA <<<\n");
            ejecutar(escenario, new EstrategiaHibrida());
        } else {
            EstrategiaInterceptacion est = estratNom.equals("tiempo")
                    ? new EstrategiaTiempo()
                    : new EstrategiaHibrida();
            ejecutar(escenario, est);
        }
    }

    // ─────────────────────────────────────────────────────────────────────

    private static void ejecutar(String escenario, EstrategiaInterceptacion estrategia)
            throws InterruptedException {

        boolean saturado      = escenario.equals("saturado");
        int     duracionTicks = saturado ? 50 : 30;
        int     extrasAleatorios = saturado ? 8 : 0;

        // ── Zonas críticas ─────────────────────────────────────────────
        ZonaObjetivo[] zonas = ZonaObjetivo.crearZonas();

        // ── Cola de amenazas (patrón productor-consumidor 3 semáforos) ─
        ColaAmenazas cola = new ColaAmenazas(30, estrategia);

        // ── Reloj global ───────────────────────────────────────────────
        RelojGlobal reloj = new RelojGlobal(duracionTicks);

        // ── Creador de misiles enemigos ────────────────────────────────
        CreadorDeEnemigos creador = saturado
                ? new CreadorDeEnemigos(reloj, cola, zonas, "Misiles.txt", extrasAleatorios, 42L)
                : new CreadorDeEnemigos(reloj, cola, zonas, "Misiles.txt");

        // ── Controlador aliado (planificador + asignador) ──────────────
        ControladorAliado controlador = new ControladorAliado(cola, reloj, estrategia);

        // ── Arrancar hilos ─────────────────────────────────────────────
        // Orden: reloj primero, luego creador, luego controlador
        reloj.start();

        // Esperar un tick para que el reloj esté activo
        Thread.sleep(200);

        creador.start();

        // Esperar un tick para que los misiles del primer tick estén cargados
        Thread.sleep(1200);

        controlador.start();

        // ── Esperar fin de simulación ──────────────────────────────────
        reloj.join(); // bloquea hasta que el reloj termina sus ticks

        controlador.detener();
        Thread.sleep(500); // dar tiempo a hilos en vuelo

        // ── Reporte final ──────────────────────────────────────────────
        List<MisilEnemigo> misiles = creador.getMisiles();
        Estadisticas.imprimir(
                escenario.toUpperCase(), estrategia.getNombre(),
                zonas, misiles, controlador, cola
        );
    }
}
