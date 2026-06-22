package Classes;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ExportadorResultados {

    private static final String ARCHIVO = "ResultadosSimulacion.csv";

    // Crear encabezado solamente la primera vez
    public static void inicializarArchivo() {

        File archivo = new File(ARCHIVO);

        if (!archivo.exists()) {
            try (FileWriter fw = new FileWriter(archivo)) {

                fw.write("MisilesTotales;Efectividad\n");

            } catch (IOException e) {
                System.out.println("Error creando CSV: " + e.getMessage());
            }
        }
    }

    public static void guardarResultado(Estadisticas estadisticas) {

        int total = estadisticas.getMisilesCreados();
        int interceptados = estadisticas.getMisilesInterceptados();
        int impactados = estadisticas.getMisilesImpactados();

        double efectividad = 0;

        if (total > 0) {
            efectividad = ((double) interceptados / total) * 100;
        }

        try (FileWriter fw = new FileWriter(ARCHIVO, true)) {

            fw.write(
                    total + ";" +
                    String.format("%.2f", efectividad)
                            .replace(".", ",") +
                    "\n"
            );

        } catch (IOException e) {
            System.out.println("Error guardando resultado: " + e.getMessage());
        }
    }
}