package Classes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static int tiempoDeEspera = 5;
    private static int cantidadDeInterceptores = 3;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        if (!menuPrincipal()) {
            System.out.println("Saliendo del sistema...");
            return;
        }

        String estrategia = elegirEstrategia();

        ColaDePrioridad cola = new ColaDePrioridad(estrategia);
        RelojGlobal reloj = new RelojGlobal(200);
        Estadisticas estadisticas = new Estadisticas();
        ExportadorResultados.inicializarArchivo();

        CreadorDeEnemigos creador = new CreadorDeEnemigos(
                reloj, "ProyectoDeSO/src/Archivos/Misiles25.txt", estadisticas);
        creador.start();

        List<Interceptor> interceptores = new ArrayList<>();
        for (int i = 1; i <= cantidadDeInterceptores; i++) {
            Interceptor interceptor = new Interceptor(
                    "Interceptor - " + i, reloj, estadisticas, tiempoDeEspera);
            interceptores.add(interceptor);
            interceptor.start();
        }

        ControladorDeAliados controlador = new ControladorDeAliados(
                reloj, estadisticas, interceptores, creador.getMisiles(), cola);
        controlador.start();

        System.out.println("Simulación iniciada. Reloj global corriendo...");
        reloj.start();

        try {
            reloj.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Simulación interrumpida.");
        }

        estadisticas.mostrarEstadisticas();
        RegistroDeEventos.cerrar();
        ExportadorResultados.guardarResultado(estadisticas);

        sc.close();
    }

    private static boolean menuPrincipal() {
        while (true) {
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE INTERCEPCIÓN DE AMENAZAS AÉREAS ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  1 - Iniciar simulación                      ║");
            System.out.println("║  2 - Salir                                   ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Opción (1/2): ");

            int opcion = leerOpcion(1);

            if (opcion == 1) {
                if (menuInterceptores()) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    private static boolean menuInterceptores() {
        while (true) {
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║   SISTEMA DE INTERCEPCIÓN DE AMENAZAS AÉREAS ║");
            System.out.println("╠══════════════════════════════════════════════╣");
            System.out.println("║  Interceptores: " + cantidadDeInterceptores
                    + " | Tiempo de recarga: " + tiempoDeEspera + "s      ║");
            System.out.println("║  1 - Cambiar cantidad de interceptores       ║");
            System.out.println("║  2 - Cambiar tiempo de recarga               ║");
            System.out.println("║  3 - Configurar tiempo de las zonas          ║");
            System.out.println("║  4 - Continuar                               ║");
            System.out.println("║  5 - Volver al menú principal                ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Opción (1/2/3/4/5): ");

            int opcion = leerOpcion(4);

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese la nueva cantidad de interceptores disponibles: ");
                    int nuevaCantidad = leerOpcion(cantidadDeInterceptores);
                    if (nuevaCantidad > 0) {
                        cantidadDeInterceptores = nuevaCantidad;
                        System.out.println("Cantidad de interceptores actualizada a " + cantidadDeInterceptores + ".");
                    } else {
                        System.out.println("Valor inválido, se mantiene la cantidad actual (" + cantidadDeInterceptores + ").");
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el nuevo tiempo de recarga (segundos): ");
                    int nuevoTiempo = leerOpcion(tiempoDeEspera);
                    if (nuevoTiempo > 0) {
                        tiempoDeEspera = nuevoTiempo;
                        System.out.println("Tiempo de recarga actualizado a " + tiempoDeEspera + "s.");
                    } else {
                        System.out.println("Valor inválido, se mantiene el tiempo actual (" + tiempoDeEspera + "s).");
                    }
                    break;
                case 3:
                    menuZonas();
                    break;
                case 4:
                    return true;
                case 5:
                    return false;
                default:
                    System.out.println("Saliendo del sistema...");
                    System.exit(0);
            }
        }
    }

    private static void menuZonas() {
        while (true) {
            System.out.println("╔══════════════════════════════════════════════╗");
            System.out.println("║   CONFIGURACIÓN DE TICKS DESDE BASE POR ZONA ║");
            System.out.println("╠══════════════════════════════════════════════╣");

            List<String> nombres = new ArrayList<>(RegistroDeZonas.ZONAS.keySet());
            for (int i = 0; i < nombres.size(); i++) {
                Zona z = RegistroDeZonas.ZONAS.get(nombres.get(i));
                System.out.println("║  " + (i + 1) + " - " + z.getNombre()
                        + " (ticks actuales: " + z.getTicksDesdeBase() + ")");
            }
            System.out.println("║  0 - Volver                                  ║");
            System.out.println("╚══════════════════════════════════════════════╝");
            System.out.print("Seleccione la zona a modificar: ");

            int opcion = leerOpcion(0);
            if (opcion == 0) {
                return;
            }
            if (opcion < 1 || opcion > nombres.size()) {
                System.out.println("Opción inválida.");
                continue;
            }

            Zona zonaElegida = RegistroDeZonas.ZONAS.get(nombres.get(opcion - 1));
            System.out.print("Ingrese los nuevos ticks desde base para " + zonaElegida.getNombre() + ": ");
            int nuevosTicks = leerOpcion(zonaElegida.getTicksDesdeBase());
            if (nuevosTicks > 0) {
                zonaElegida.setTicksDesdeBase(nuevosTicks);
                System.out.println("Ticks de " + zonaElegida.getNombre() + " actualizados a " + nuevosTicks + ".");
            } else {
                System.out.println("Valor inválido, se mantienen los ticks actuales.");
            }
        }
    }

    private static String elegirEstrategia() {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE INTERCEPCIÓN DE AMENAZAS AÉREAS ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Seleccione estrategia de planificación:     ║");
        System.out.println("║  1 - Menor tiempo hasta impacto              ║");
        System.out.println("║  2 - Mayor criticidad de zona                ║");
        System.out.println("║  3 - Prioridad combinada (criticidad/tiempo) ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.print("Opción (1/2/3): ");

        int opcion = leerOpcion(3);

        switch (opcion) {
            case 1:  return "MENOR_TIEMPO";
            case 2:  return "MAYOR_CRITICIDAD";
            default: return "PRIORIDAD_COMBINADA";
        }
    }

    private static int leerOpcion(int valorPorDefecto) {
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida, usando valor por defecto (" + valorPorDefecto + ").");
            return valorPorDefecto;
        }
    }
}