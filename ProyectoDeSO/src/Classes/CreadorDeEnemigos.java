package Classes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class CreadorDeEnemigos extends Thread {

    private RelojGlobal reloj;
    private List<MisilEnemigo> misiles;
    private Estadisticas estadisticas;

    public CreadorDeEnemigos(RelojGlobal reloj, String nombreArchivo, Estadisticas estadisticas) {
        this.reloj = reloj;
        this.misiles = leerMisilesDesdeArchivo(nombreArchivo, reloj, estadisticas);
        this.estadisticas = estadisticas;
    }

    public void run() {
        for (MisilEnemigo m : misiles) {
            m.start();
        }
    }                             

    static List<MisilEnemigo> leerMisilesDesdeArchivo(String nombreArchivo, RelojGlobal reloj, Estadisticas estadisticas) {
        List<MisilEnemigo> misiles = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",");
                if (partes.length == 4) {                        
                    String nombre = partes[0].trim();
                    String objetivo = partes[1].trim();
                    int tickDeAparicion = Integer.parseInt(partes[2].trim());
                    int ticksHastaImpacto = Integer.parseInt(partes[3].trim());
                    Zona objetivoZona = RegistroDeZonas.obtener(objetivo);
                    misiles.add(new MisilEnemigo(nombre, objetivoZona, tickDeAparicion, ticksHastaImpacto, reloj, estadisticas));
                }
            }

        } catch (IOException e) {
            System.err.println("Error leyendo archivo: " + e.getMessage());
        }

        return misiles;
    }

    public List<MisilEnemigo> getMisiles() { return misiles; }

}