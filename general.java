import java.util.Map;
import java.util.concurrent.Semaphore;


public class general extends Thread {
    
    static int tiempoGlobal = 0;
    int distancia = 0;
    int nuestraDistancia = 0;
    float indiceCriticidad = 0;
    static Semaphore mutex = new Semaphore(1);
    static Semaphore choque = new Semaphore(1);
    static Semaphore salio = new Semaphore(0);
    static Semaphore recargando = new Semaphore(1);
    Map<String, Integer> mapaDistancias = Map.of(
        "Hospital", 100,
        "Datacenter", 150,
        "Escuela", 200,     //El segundo valor queda distancia(metros o asi) o queda en segundos, a definir
        "Aereopuerto", 250,
        "Deposito Militar", 300
    );

    public float calcularIndiceDeCriticidad(float indiceCriticidad, int distancia, int nuestraDistancia) {
        indiceCriticidad = (float) distancia / nuestraDistancia; // pensado en distancia en metros, pero se puede adaptar a tiempo si se prefiere
        return indiceCriticidad;
    }

    
    /*
    if (indiceCriticidad + mapaDistancias.get("Hospital") < indiceCriticidad + mapaDistancias.get("Datacenter") && indiceCriticidad + mapaDistancias.get("Hospital") < indiceCriticidad + mapaDistancias.get("Escuela") && indiceCriticidad + mapaDistancias.get("Hospital") < indiceCriticidad + mapaDistancias.get("Aereopuerto") && indiceCriticidad + mapaDistancias.get("Hospital") < indiceCriticidad + mapaDistancias.get("Deposito Militar")) {
        // Priorizar Hospital
    } else if (indiceCriticidad + mapaDistancias.get("Datacenter") < indiceCriticidad + mapaDistancias.get("Escuela") && indiceCriticidad + mapaDistancias.get("Datacenter") < indiceCriticidad + mapaDistancias.get("Aereopuerto") && indiceCriticidad + mapaDistancias.get("Datacenter") < indiceCriticidad + mapaDistancias.get("Deposito Militar")) {
        // Priorizar Datacenter
    } else if (indiceCriticidad + mapaDistancias.get("Escuela") < indiceCriticidad + mapaDistancias.get("Aereopuerto") && indiceCriticidad + mapaDistancias.get("Escuela") < indiceCriticidad + mapaDistancias.get("Deposito Militar")) {
        // Priorizar Escuela
    } else if (indiceCriticidad + mapaDistancias.get("Aereopuerto") < indiceCriticidad + mapaDistancias.get("Deposito Militar")) {
        // Priorizar Aereopuerto
    } else {
        // Priorizar Deposito Militar
    }
    */

}