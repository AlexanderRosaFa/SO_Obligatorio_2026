package ProyectoDeSO.Tests;

import Classes.*;

import java.util.ArrayList;
import java.util.List;

public class ControladorDeAliadosTest {

    private static int pruebasEjecutadas = 0;
    private static int pruebasCorrectas = 0;

    public static void main(String[] args) {

        testAsignacionCorrecta();
        testMisilNoFactible();

        System.out.println("\n====================================");
        System.out.println("Pruebas ejecutadas: " + pruebasEjecutadas);
        System.out.println("Pruebas correctas : " + pruebasCorrectas);
        System.out.println("Pruebas fallidas  : " + (pruebasEjecutadas - pruebasCorrectas));

        if (pruebasEjecutadas == pruebasCorrectas) {
            System.out.println("RESULTADO: TODOS LOS TESTS PASARON");
        } else {
            System.out.println("RESULTADO: EXISTEN TESTS FALLIDOS");
        }

        System.out.println("====================================");
    }

    private static void testAsignacionCorrecta() {

        RelojGlobal reloj = new RelojGlobal(100);
        Estadisticas estadisticas = new Estadisticas();

        List<Interceptor> interceptores = new ArrayList<>();
        interceptores.add(
                new Interceptor("I1", reloj, estadisticas, 2)
        );

        List<MisilEnemigo> misiles = new ArrayList<>();

        Zona zona = new Zona("Hospital", "ALTA", 10, 1);

        MisilEnemigo misil = new MisilEnemigo(
                "M1",
                zona,
                1,
                20,
                reloj,
                estadisticas
        );

        ControladorDeAliados controlador =
                new ControladorDeAliados(
                        reloj,
                        estadisticas,
                        interceptores,
                        misiles
                );

        controlador.AsignarInterceptor(misil);

        verificar(
                "Asignación correcta de interceptor",
                misil.getEstado().equals("Atendido")
        );
    }

    private static void testMisilNoFactible() {

        RelojGlobal reloj = new RelojGlobal(100);
        Estadisticas estadisticas = new Estadisticas();

        List<Interceptor> interceptores = new ArrayList<>();
        interceptores.add(
                new Interceptor("I1", reloj, estadisticas, 2)
        );

        List<MisilEnemigo> misiles = new ArrayList<>();

        /*
         * Distancia muy grande y poco tiempo.
         * No debería ser interceptable.
         */
        Zona zona = new Zona("BaseLejana", "BAJA", 5, 50);

        MisilEnemigo misil = new MisilEnemigo(
                "M2",
                zona,
                1,
                2,
                reloj,
                estadisticas
        );

        ControladorDeAliados controlador =
                new ControladorDeAliados(
                        reloj,
                        estadisticas,
                        interceptores,
                        misiles
                );

        controlador.AsignarInterceptor(misil);

        verificar(
                "Misil no factible se ignora",
                misil.getEstado().equals("Ignorado")
        );
    }

    private static void verificar(String nombreTest, boolean resultado) {

        pruebasEjecutadas++;

        if (resultado) {
            pruebasCorrectas++;
            System.out.println("[OK] " + nombreTest);
        } else {
            System.out.println("[ERROR] " + nombreTest);
        }
    }
}