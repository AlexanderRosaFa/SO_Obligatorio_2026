import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * CreadorDeEnemigos — hilo productor de amenazas.
 *
 * CONSERVA la lógica original: lee misiles desde archivo y los
 * lanza en el tick de aparición correspondiente.
 * AGREGA:
 *   - Lectura de zona desde archivo y mapeo a ZonaObjetivo
 *   - Soporte para generación aleatoria (escenario saturado)
 *   - Deposita cada misil en la ColaAmenazas compartida
 *   - Formato del archivo: nombre,tick,zona  (igual al original)
 *
 * SINCRONIZACIÓN: el acceso a la ColaAmenazas es thread-safe
 * por diseño interno de la cola (tres semáforos).
 */
public class CreadorDeEnemigos extends Thread {

    private final RelojGlobal          reloj;
    private final ColaAmenazas         cola;
    private final ZonaObjetivo[]       zonas;
    private final List<MisilEnemigo>   misiles;
    private final boolean              generacionAleatoria;
    private final int                  totalAleatorios;
    private final long                 seed;

    // Para modo aleatorio: misiles adicionales entre tick 1 y 20
    private int contadorAleatorio = 0;

    public CreadorDeEnemigos(RelojGlobal reloj, ColaAmenazas cola,
                             ZonaObjetivo[] zonas, String archivo) {
        super("CreadorDeEnemigos");
        this.reloj               = reloj;
        this.cola                = cola;
        this.zonas               = zonas;
        this.misiles             = leerDesdeArchivo(archivo, zonas, reloj);
        this.generacionAleatoria = false;
        this.totalAleatorios     = 0;
        this.seed                = 42L;
        setDaemon(true);
    }

    /** Constructor para escenario con misiles adicionales aleatorios. */
    public CreadorDeEnemigos(RelojGlobal reloj, ColaAmenazas cola,
                             ZonaObjetivo[] zonas, String archivo,
                             int extraAleatorios, long seed) {
        super("CreadorDeEnemigos");
        this.reloj               = reloj;
        this.cola                = cola;
        this.zonas               = zonas;
        this.misiles             = leerDesdeArchivo(archivo, zonas, reloj);
        this.generacionAleatoria = extraAleatorios > 0;
        this.totalAleatorios     = extraAleatorios;
        this.seed                = seed;
        setDaemon(true);
    }

    @Override
    public void run() {
        Random rng = new Random(seed);

        while (reloj.estaEjecutando()) {
            int tiempoActual = reloj.getTiempo();
            boolean sigue    = reloj.esperarTick();
            if (!sigue) break;

            // Lanzar misiles del archivo en su tick programado
            for (MisilEnemigo m : misiles) {
                if (m.getTiempoLanzamiento() == tiempoActual) {
                    m.start();
                    try {
                        cola.agregar(m);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }

            // Lanzar misiles aleatorios adicionales (escenario saturado)
            if (generacionAleatoria && contadorAleatorio < totalAleatorios) {
                if (rng.nextInt(4) == 0) { // ~25% de probabilidad por tick
                    MisilEnemigo extra = generarAleatorio(rng, tiempoActual + 1);
                    extra.start();
                    try {
                        cola.agregar(extra);
                        contadorAleatorio++;
                        System.out.printf("[T=%d] [GEN] Extra: %s -> %s%n",
                                reloj.getTiempo(), extra.getNombre(),
                                extra.getZona().getNombre());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    private MisilEnemigo generarAleatorio(Random rng, int tick) {
        ZonaObjetivo zona = zonas[rng.nextInt(zonas.length)];
        String nombre = "Extra-" + (++contadorAleatorio);
        return new MisilEnemigo(nombre, tick, zona, reloj);
    }

    public List<MisilEnemigo> getMisiles() { return misiles; }

    // ── Lectura de archivo ───────────────────────────────────────────────

    private static List<MisilEnemigo> leerDesdeArchivo(
            String archivo, ZonaObjetivo[] zonas, RelojGlobal reloj) {

        List<MisilEnemigo> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;

                String[] partes = linea.split(",");
                if (partes.length < 3) continue;

                String nombre  = partes[0].trim();
                int    tick    = Integer.parseInt(partes[1].trim());
                String zonaNom = partes[2].trim();

                ZonaObjetivo zona = buscarZona(zonas, zonaNom);
                lista.add(new MisilEnemigo(nombre, tick, zona, reloj));
            }
        } catch (IOException e) {
            System.err.println("[CREADOR] Error leyendo " + archivo + ": " + e.getMessage());
        }

        return lista;
    }

    private static ZonaObjetivo buscarZona(ZonaObjetivo[] zonas, String nombre) {
        for (ZonaObjetivo z : zonas) {
            if (z.getNombre().equalsIgnoreCase(nombre)) return z;
        }
        // Si no existe, asignar zona con criticidad MEDIA por defecto
        return new ZonaObjetivo(nombre, ZonaObjetivo.Criticidad.MEDIA);
    }
}
