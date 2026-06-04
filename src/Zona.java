package src;
import java.util.concurrent.Semaphore;

public class Zona {

    public enum Criticidad {
        URGENTE(10), MEDIA(5), BAJA(2);
        int valor;
        
        Criticidad(int v) { 
            this.valor = v; 
        }

        public int getValor() {
            return valor; 
        }
    }

    String nombre;
    Criticidad criticidad;
    double distanciaKm;

    int impactos = 0;
    int detectadas = 0;
    int neutralizadas = 0;

    Semaphore mutex = new Semaphore(1);

    public Zona(String nombre, Criticidad criticidad, double distanciaKm) {
        this.nombre = nombre;
        this.criticidad = criticidad;
        this.distanciaKm = distanciaKm;
    }

    public void registrarDetectada() {
        try { 
            mutex.acquire(); 
            detectadas++; 
            mutex.release(); 
        }
        catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        }
    }

    public void registrarImpacto() {
        try { 
            mutex.acquire(); 
            impactos++; 
            mutex.release(); 
        }
        catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        }
    }

    public void registrarNeutralizada() {
        try {
            mutex.acquire();
            neutralizadas++; 
            mutex.release(); 
        }
        catch (InterruptedException e) { 
            Thread.currentThread().interrupt(); 
        }
    }

    public String getNombre() {
        return nombre;
    }
    public Criticidad getCriticidad() { 
        return criticidad; 
    }
    public double getDistanciaKm()  { 
        return distanciaKm; 
    }
    public int getImpactos() { 
        return impactos; 
    }
    public int getDetectadas() { 
        return detectadas; 
    }
    public int getNeutralizadas() { 
        return neutralizadas; 
    }
}
