import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreadorDeEnemigos extends Thread {

    private RelojGlobal reloj;
    private List<MisilEnemigo> misiles;

    public CreadorDeEnemigos(RelojGlobal reloj, String nombreArchivo) {
        this.reloj = reloj;
        this.misiles = leerMisilesDesdeArchivo(nombreArchivo, reloj);
    }

    public void run() {
        while (true) {
            int tiempoActual = reloj.getTiempo();

            reloj.esperarTick();
            for (MisilEnemigo m : misiles) {
                if (m.getTiempoLanzamiento() == tiempoActual) {
                    m.start();
                    System.out.println(m.getNombre() + " lanzado hacia " + m.getObjetivo());
                }
            }
        }
    }                             

    static List<MisilEnemigo> leerMisilesDesdeArchivo(String nombreArchivo, RelojGlobal reloj) {
        List<MisilEnemigo> misiles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length == 3) {                          // ✅ era 4
                    String nombre = partes[0].trim();
                    int tiempo    = Integer.parseInt(partes[1].trim()); // ✅ era índice 2
                    String objetivo = partes[2].trim();                 // ✅ era índice 3
                    misiles.add(new MisilEnemigo(nombre, tiempo, objetivo, reloj));
                }
            }

        } catch (IOException e) {
            System.err.println("Error leyendo archivo: " + e.getMessage());
        }

        return misiles;
    }

    public List<MisilEnemigo> getMisiles() {
        return misiles;
    }
}
