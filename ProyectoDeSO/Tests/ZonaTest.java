package ProyectoDeSO.Tests;

import Classes.Zona;

public class ZonaTest {

    public static void main(String[] args) {

        int pruebas = 0;
        int errores = 0;

        // ===== TEST 1 =====
        pruebas++;

        Zona hospital = new Zona(
                "Hospital Central",
                "URGENTE",
                10,
                5
        );

        if (!hospital.getNombre().equals("Hospital Central")) {
            System.out.println("ERROR TEST 1: nombre incorrecto");
            errores++;
        }

        if (!hospital.getCriticidad().equals("URGENTE")) {
            System.out.println("ERROR TEST 1: criticidad incorrecta");
            errores++;
        }

        if (hospital.getValorCriticidad() != 10) {
            System.out.println("ERROR TEST 1: valorCriticidad incorrecto");
            errores++;
        }

        if (hospital.getTicksDesdeBase() != 5) {
            System.out.println("ERROR TEST 1: ticks incorrectos");
            errores++;
        }

        // ===== TEST 2 =====
        pruebas++;

        Zona aeropuerto = new Zona(
                "Aeropuerto",
                "MEDIA",
                7,
                12
        );

        if (!aeropuerto.getNombre().equals("Aeropuerto")) {
            System.out.println("ERROR TEST 2: nombre incorrecto");
            errores++;
        }

        if (!aeropuerto.getCriticidad().equals("MEDIA")) {
            System.out.println("ERROR TEST 2: criticidad incorrecta");
            errores++;
        }

        if (aeropuerto.getValorCriticidad() != 7) {
            System.out.println("ERROR TEST 2: valorCriticidad incorrecto");
            errores++;
        }

        if (aeropuerto.getTicksDesdeBase() != 12) {
            System.out.println("ERROR TEST 2: ticks incorrectos");
            errores++;
        }

        // ===== TEST 3 =====
        pruebas++;

        Zona deposito = new Zona(
                "Deposito Militar",
                "BAJA",
                3,
                20
        );

        if (!deposito.getNombre().equals("Deposito Militar")) {
            System.out.println("ERROR TEST 3: nombre incorrecto");
            errores++;
        }

        if (!deposito.getCriticidad().equals("BAJA")) {
            System.out.println("ERROR TEST 3: criticidad incorrecta");
            errores++;
        }

        if (deposito.getValorCriticidad() != 3) {
            System.out.println("ERROR TEST 3: valorCriticidad incorrecto");
            errores++;
        }

        if (deposito.getTicksDesdeBase() != 20) {
            System.out.println("ERROR TEST 3: ticks incorrectos");
            errores++;
        }

        // ===== RESULTADO FINAL =====

        System.out.println("\n=================================");
        System.out.println("PRUEBAS EJECUTADAS: " + pruebas);
        System.out.println("ERRORES ENCONTRADOS: " + errores);

        if (errores == 0) {
            System.out.println("TODOS LOS TESTS PASARON CORRECTAMENTE");
        } else {
            System.out.println("ALGUNOS TESTS FALLARON");
        }
        System.out.println("=================================");
    }
}