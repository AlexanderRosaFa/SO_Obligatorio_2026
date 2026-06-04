public class Main {

    public static void main(String[] args) {

        RelojGlobal reloj = new RelojGlobal(100); // el reloj se detendrá después de 100 ticks
        CreadorDeEnemigos creador = new CreadorDeEnemigos(reloj, "Misiles.txt"); 

        reloj.start();
        creador.start();

        // Espera un tick para que el creador cargue los misiles
        try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }

        ControladorAliado controlador = new ControladorAliado(creador.getMisiles(), reloj);
        controlador.start();
    }
}