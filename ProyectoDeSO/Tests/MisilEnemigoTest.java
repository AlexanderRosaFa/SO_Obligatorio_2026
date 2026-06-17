package ProyectoDeSO.Tests;

import Classes.Estadisticas;
import Classes.MisilEnemigo;
import Classes.RelojGlobal;
import Classes.Zona;

public class MisilEnemigoTest {

    private static int pruebasEjecutadas = 0;
    private static int pruebasCorrectas = 0;

    public static void main(String[] args) {

        testEstadoInicial();
        testAtender();
        testIgnorar();
        testDestruir();
        testGetters();
        testIncrementoMisilesCreados();

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

    private static MisilEnemigo crearMisil() {

        Zona zona = new Zona(
                "Hospital",
                "URGENTE",
                10,
                5
        );

        RelojGlobal reloj = new RelojGlobal(100);
        Estadisticas estadisticas = new Estadisticas();

        return new MisilEnemigo(
                "M1",
                zona,
                3,
                10,
                reloj,
                estadisticas
        );
    }

    private static void testEstadoInicial() {

        MisilEnemigo misil = crearMisil();

        verificar(
                "Estado inicial = Esperando",
                misil.getEstado().equals("Esperando")
        );
    }

    private static void testAtender() {

        MisilEnemigo misil = crearMisil();

        misil.atender();

        verificar(
                "Cambio a Atendido",
                misil.getEstado().equals("Atendido")
        );
    }

    private static void testIgnorar() {

        MisilEnemigo misil = crearMisil();

        misil.ignorar();

        verificar(
                "Cambio a Ignorado",
                misil.getEstado().equals("Ignorado")
        );
    }

    private static void testDestruir() {

        MisilEnemigo misil = crearMisil();

        misil.destruir();

        verificar(
                "Cambio a Interceptado",
                misil.getEstado().equals("Interceptado")
        );
    }

    private static void testGetters() {

        MisilEnemigo misil = crearMisil();

        boolean ok =
                misil.getNombre().equals("M1")
                && misil.getObjetivo().getNombre().equals("Hospital")
                && misil.getTickDeAparicion() == 3
                && misil.getTicksHastaImpacto() == 10;

        verificar(
                "Getters",
                ok
        );
    }

    private static void testIncrementoMisilesCreados() {

        Estadisticas estadisticas = new Estadisticas();

        Zona zona = new Zona(
                "Escuela",
                "MEDIA",
                5,
                10
        );

        RelojGlobal reloj = new RelojGlobal(100);

        new MisilEnemigo(
                "M2",
                zona,
                1,
                5,
                reloj,
                estadisticas
        );

        verificar(
                "Incrementa misiles creados",
                estadisticas.getMisilesCreados() == 1
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