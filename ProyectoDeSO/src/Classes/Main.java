package Classes;
import java.util.ArrayList;
import java.util.List;

public class Main {
    List<Interceptor> listaInterceptores = new ArrayList<>();

    public static void main(String[] args) {
        RelojGlobal reloj = new RelojGlobal(30);
        Estadisticas estadisticas = new Estadisticas();
        CreadorDeEnemigos creador = new CreadorDeEnemigos(reloj, "src/Archivos/MisilesTest1.txt", estadisticas);
        creador.start();
        List<Interceptor> interceptores = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Interceptor interceptor = new Interceptor("Interceptor" + i, reloj, estadisticas);
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

    }

}
