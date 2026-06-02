public class MisilEnemigo extends Thread {

    private String nombre;
    private int tiempoLanzamiento;
    private String objetivo;

    private volatile int posicion = 100;
    private volatile boolean destruido = false;
    private RelojGlobal reloj;

    public MisilEnemigo(String nombre, int tiempoLanzamiento, String objetivo, RelojGlobal reloj) {
        this.nombre = nombre;
        this.tiempoLanzamiento = tiempoLanzamiento;
        this.objetivo = objetivo;
        this.reloj = reloj;
    }

    @Override
    public void run() {
        while (posicion > 0 && !destruido) {
            reloj.esperarTick();
            posicion -= 5;
            System.out.println(nombre + " -> posición: " + posicion);
        }

        if (destruido) {
            System.out.println("Fue impactado por misil aliado en posición: " + posicion);
        } else if (posicion <= 0) {
            System.out.println("¡¡" + nombre + " alcanzó su objetivo " + objetivo + "!!");
        }
    }

    public String getNombre()           { return nombre; }
    public int getTiempoLanzamiento()   { return tiempoLanzamiento; }
    public String getObjetivo()         { return objetivo; }

    public synchronized int getPosicion()       { return posicion; }
    public synchronized void destruir()         { destruido = true; }
    public synchronized boolean estaDestruido() { return destruido; }
}