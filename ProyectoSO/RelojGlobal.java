public class RelojGlobal extends Thread {

    private int tiempo = 0;
    private boolean ejecutando = true;

    @Override
    public void run() {

        while (ejecutando) {

            try {
                Thread.sleep(1000); // 1 segundo real
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (this) {
                tiempo++;
                System.out.println("Tiempo: " + tiempo);
                notifyAll(); // despierta a todos los hilos
            }
        }
    }

    public synchronized void esperarTick() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getTiempo() {
        return tiempo;
    }

    public void detener() {
        ejecutando = false;
    }
}
