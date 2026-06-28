package Classes;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    List<Interceptor> listaInterceptores = new ArrayList<>();

    public static void main(String[] args) {
        int TiempoDeEspera = 5;
        int CantidadDeInterceptores = 3;
        ColaDePrioridad cola = new ColaDePrioridad(elegirEstrategia());

        RelojGlobal reloj = new RelojGlobal(105);
        Estadisticas estadisticas = new Estadisticas();
        ExportadorResultados.inicializarArchivo();
        CreadorDeEnemigos creador = new CreadorDeEnemigos(reloj, "ProyectoDeSO/src/Archivos/Misiles25.txt", estadisticas);
        creador.start();
        List<Interceptor> interceptores = new ArrayList<>();
        for (int i = 1; i <= CantidadDeInterceptores; i++) {
            Interceptor interceptor = new Interceptor("Interceptor - " + i, reloj, estadisticas,TiempoDeEspera );
            interceptores.add(interceptor);
            interceptor.start();
        }
        ControladorDeAliados controlador = new ControladorDeAliados(reloj, estadisticas, interceptores, creador.getMisiles(), cola);
        controlador.start();
        System.out.println("Simulación iniciada. Reloj global corriendo...");
        reloj.start();
        try {
            reloj.join(); // esperar a que el reloj termine
        } catch (InterruptedException e) {}
        estadisticas.mostrarEstadisticas();
        RegistroDeEventos.cerrar();
        ExportadorResultados.guardarResultado(estadisticas);

    }

    private static String elegirEstrategia() {
        Scanner sc = new Scanner(System.in);
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE INTERCEPCIÓN DE AMENAZAS AÉREAS ║");
        System.out.println("╠══════════════════════════════════════════════╣");
        System.out.println("║  Seleccione estrategia de planificación:     ║");
        System.out.println("║  1 - Menor tiempo hasta impacto              ║");
        System.out.println("║  2 - Mayor criticidad de zona                ║");
        System.out.println("║  3 - Prioridad combinada (criticidad/tiempo) ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.print("Opción (1/2/3): ");

        int opcion = 3; // default
        try {
            opcion = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opción inválida, usando estrategia 3 por defecto.");
        }

        switch (opcion) {
            case 1:  return "MENOR_TIEMPO";
            case 2:  return "MAYOR_CRITICIDAD";
            default: return "PRIORIDAD_COMBINADA";
        }
    }

}
