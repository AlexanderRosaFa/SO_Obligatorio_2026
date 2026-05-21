import java.util.concurrent.Semaphore;


public class general extends Thread {
    
    static int tiempoGlobal = 0;
    int distancia = 0;
    int nuestraDistancia = 0;
    float indiceCriticidad = 0;
    static Semaphore mutex = new Semaphore(1);
    static Semaphore choqueMisil = new Semaphore(1);
    static Semaphore salioMisil = new Semaphore(0);
    static Semaphore recargandoMisil = new Semaphore(1);
    
    // Primera tarea a realizar: tomando en cuenta ambas variables, si distancia o tiempo, poder calcular el indice de criticidad
    // segun la zona elegida de forma simplificada, con la clase zona  ya esta medio solucionado

    // Segunda tarea: Crear una clase Misil que cree misiles para ambos lados
    // Crear una clase que CargueMisil para leer txt con misiles enemigos

    // patron observador
    public class Zona {
        String nombre;
        int distancia;
        int criticidad;

        public Zona(String nombre, int distancia, int criticidad) {
            this.nombre = nombre;
            this.distancia = distancia;
            this.criticidad = criticidad;
        }

        public String getNombre() { 
            return nombre; 
        }
        
        public int getCriticidad() { 
            return criticidad; 
        }

        public int getDistancia() { 
            return distancia; 
        }

        // Ejemplo de uso:
        // Zona Hospital = new Zona("Hospital", 500, 5);
    }

    public class Misil {
        String nombre;
        int distancia;
        int tiempo;
        float indiceCriticidad;

        public Misil(String nombre, int distancia, int tiempo) {
            this.nombre = nombre;
            this.distancia = distancia;
            this.tiempo = tiempo;
            this.indiceCriticidad = calcularIndiceDeCriticidad(distancia, nuestraDistancia);
        }

        public float calcularIndiceDeCriticidad(int distancia, int nuestraDistancia) {
            return (float) distancia - nuestraDistancia; 
            // pensado en distancia en metros, pero se puede adaptar a tiempo si se prefiere
        }
        // ↑ Como le agrego el tiempo a esto? O lo dejo solo con la distancia?     
        // O hago un indice de criticidad que combine ambos factores?
    

        public String getNombre() { 
            return nombre; 
        }
        
        public int getDistancia() { 
            return distancia; 
        }

        public int getTiempo() { 
            return tiempo; 
        }

        public float getIndiceCriticidad() { 
            return indiceCriticidad; 
        }

        // Ejemplo de uso:
        // Misil misil1 = new Misil("Misil1", 500, 10);
    }

}