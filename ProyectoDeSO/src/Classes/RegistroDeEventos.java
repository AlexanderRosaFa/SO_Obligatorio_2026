package Classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class RegistroDeEventos {
    private static PrintWriter writer;

    static {
        try {
            writer = new PrintWriter(new BufferedWriter(new FileWriter("src/Archivos/Logs", false)));
        } catch (IOException e) {
            System.err.println("[LOG] No se pudo abrir el archivo de log: " + e.getMessage());
        }
    }

    public static synchronized void log(int tick, String mensaje) {
        if (writer != null) {
            writer.println("[" + tick + "] " + mensaje);
            writer.flush();
        } else {
            System.err.println("[LOG] " + mensaje);
        }
    }

    public static synchronized void cerrar() {
        if (writer != null) writer.close();
    }

}
