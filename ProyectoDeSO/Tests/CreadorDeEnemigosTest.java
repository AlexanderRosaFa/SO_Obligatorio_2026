package ProyectoDeSO.Tests;

import Classes.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CreadorDeEnemigosTest {

    private static int pruebasEjecutadas = 0;
    private static int pruebasCorrectas = 0;

    public static void main(String[] args) throws IOException {

        testLecturaArchivo();
        testCantidadMisiles();
        testDatosPrimerMisil();

        System.out.println("\n====================================");
        System.out.println("Pruebas ejecutadas: " + pruebasEjecutadas);
        System.out.println("Pruebas correctas : " + pruebasCorrectas);
        System.out.println("Pruebas fallidas  : " + (pruebasEjecutadas - pruebasCorrectas));
        System.out.println("====================================");
    }

    private static CreadorDeEnemigos crearCreador() throws IOException {

        // Crear archivo temporal de prueba
        try (FileWriter fw = new FileWriter("C:\\Facu\\Sistema Operativo\\SO_Obligatorio_2026\\ProyectoDeSo\\Tests\\MisilesTest.txt")) {
            fw.write("M1,Hospital,3,10\n");
            fw.write("M2,Escuela,5,8\n");
        }

        RelojGlobal reloj = new RelojGlobal(100);
        Estadisticas estadisticas = new Estadisticas();

        return new CreadorDeEnemigos(
                reloj,
                "C:\\Facu\\Sistema Operativo\\SO_Obligatorio_2026\\ProyectoDeSo\\Tests\\MisilesTest.txt",
                estadisticas
        );
    }

    private static void testLecturaArchivo() throws IOException {

        CreadorDeEnemigos creador = crearCreador();

        verificar(
                "Lista de misiles creada",
                creador.getMisiles() != null
        );
    }

    private static void testCantidadMisiles() throws IOException {

        CreadorDeEnemigos creador = crearCreador();

        verificar(
                "Cantidad de misiles",
                creador.getMisiles().size() == 2
        );
    }

    private static void testDatosPrimerMisil() throws IOException {

        CreadorDeEnemigos creador = crearCreador();

        List<MisilEnemigo> misiles = creador.getMisiles();

        MisilEnemigo misil = misiles.get(0);

        boolean ok =
                misil.getNombre().equals("M1")
                && misil.getTickDeAparicion() == 3
                && misil.getTicksHastaImpacto() == 10;

        verificar(
                "Datos del primer misil",
                ok
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