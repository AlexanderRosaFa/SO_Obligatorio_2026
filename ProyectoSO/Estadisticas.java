import java.util.List;

/**
 * Estadisticas — recolecta y muestra el resumen final de la simulación.
 *
 * No necesita sincronización propia: se invoca solo al final,
 * cuando todos los hilos han terminado.
 */
public class Estadisticas {

    public static void imprimir(String escenario, String estrategia,
                                 ZonaObjetivo[] zonas,
                                 List<MisilEnemigo> misiles,
                                 ControladorAliado controlador,
                                 ColaAmenazas cola) {
        System.out.println("\n" + "═".repeat(65));
        System.out.printf("  REPORTE FINAL | Escenario: %-10s | Estrategia: %s%n",
                escenario, estrategia);
        System.out.println("═".repeat(65));

        // ── Por misil ───────────────────────────────────────────────────
        int totalGenerados    = misiles.size();
        int totalInterceptados = 0;
        int totalImpactos     = 0;
        int totalEnVuelo      = 0;

        for (MisilEnemigo m : misiles) {
            switch (m.getEstado()) {
                case INTERCEPTADO: totalInterceptados++; break;
                case IMPACTO:      totalImpactos++;      break;
                default:           totalEnVuelo++;       break;
            }
        }

        System.out.printf("  Misiles generados    : %d%n", totalGenerados);
        System.out.printf("  Interceptados      : %d  (%.1f%%)%n",
                totalInterceptados,
                totalGenerados > 0 ? totalInterceptados * 100.0 / totalGenerados : 0);
        System.out.printf("  Impactos           : %d  (%.1f%%)%n",
                totalImpactos,
                totalGenerados > 0 ? totalImpactos * 100.0 / totalGenerados : 0);
        System.out.printf("  En vuelo al cierre   : %d%n", totalEnVuelo);
        System.out.printf("  Sin recurso asignado : %d%n", controlador.getSinRecursos());
        System.out.printf("  Reordenamientos cola : %d%n", cola.getTotalReordenamientos());

        // ── Por zona ────────────────────────────────────────────────────
        System.out.println("\n  " + "─".repeat(63));
        System.out.printf("  %-18s %-10s %-8s %-8s %-8s %-8s%n",
                "Zona", "Criticidad", "Detect.", "Interc.", "Impact.", "Defensa%");
        System.out.println("  " + "─".repeat(63));

        for (ZonaObjetivo z : zonas) {
            if (z.getDetectadas() == 0) continue;
            System.out.printf("  %-18s %-10s %-8d %-8d %-8d %.1f%%%n",
                    z.getNombre(),
                    z.getCriticidad(),
                    z.getDetectadas(),
                    z.getInterceptadas(),
                    z.getImpactos(),
                    z.getTasaDefensa() * 100);
        }

        // ── Evaluación ──────────────────────────────────────────────────
        double exito = totalGenerados > 0
                ? totalInterceptados * 100.0 / totalGenerados : 100.0;
        System.out.println("\n  " + "─".repeat(63));
        System.out.printf("  Tasa de éxito global : %.1f%%%n", exito);
        String eval = exito >= 80 ? "EXCELENTE" : exito >= 60 ? "ACEPTABLE" : "SATURADO";
        System.out.printf("  Evaluación           : %s%n", eval);
        System.out.println("═".repeat(65) + "\n");
    }
}
