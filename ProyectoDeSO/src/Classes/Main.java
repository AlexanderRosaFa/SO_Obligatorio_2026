package Classes;
import java.util.ArrayList;
import java.util.List;

public class Main {
    List<Interceptor> listaInterceptores = new ArrayList<>();

    public static void main(String[] args) {
        int TiempoDeEspera = 5;
        int CantidadDeInterceptores = 3;

        RelojGlobal reloj = new RelojGlobal(100);
        Estadisticas estadisticas = new Estadisticas();
        ExportadorResultados.inicializarArchivo();
        CreadorDeEnemigos creador = new CreadorDeEnemigos(reloj, "ProyectoDeSO/src/Archivos/Misiles200.txt", estadisticas);
        creador.start();
        List<Interceptor> interceptores = new ArrayList<>();
        for (int i = 1; i <= CantidadDeInterceptores; i++) {
            Interceptor interceptor = new Interceptor("Interceptor - " + i, reloj, estadisticas,TiempoDeEspera );
            interceptores.add(interceptor);
            interceptor.start();
        }
        ControladorDeAliados controlador = new ControladorDeAliados(reloj, estadisticas, interceptores, creador.getMisiles());
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

}
