package Classes;

import java.util.concurrent.Phaser;

public class RelojGlobal extends Thread {

    private Phaser phaserReloj;
    private int tick = 0;
    private boolean ejecutando = true;
    private int tickFinal; // Tick máximo para el reloj

    public RelojGlobal(int tickFinal) {
        this.tickFinal = tickFinal;
        this.phaserReloj = new Phaser(1); // Registrar el hilo del reloj
    }

    @Override
    public void run() {
        while (tick < tickFinal && ejecutando) {
            tick++;
            System.out.println("Tick: " + tick);
            phaserReloj.arriveAndAwaitAdvance();
        }
        ejecutando = false;
        phaserReloj.forceTermination(); // ← agregar esto
    }

    public void registrar() { phaserReloj.register(); }

    public void esperarTick() {
        try {
            if (phaserReloj.isTerminated()) return;
            phaserReloj.arriveAndAwaitAdvance();
        } catch (Exception e) {
            // terminación forzada, salir normalmente
        }
    }

    public void darseDeBaja() { phaserReloj.arriveAndDeregister(); }

    public int getTick() { return tick; }
    public void detener() { ejecutando = false; }
    public boolean estaActivo() { return ejecutando; }
}
