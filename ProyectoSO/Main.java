public class Main {

    public static void main(String[] args) {

        RelojGlobal reloj = new RelojGlobal();

        // Inicia el reloj global
        reloj.start();

        // CreadorDeEnemigos lee el archivo y lanza los misiles en el tick correcto
        CreadorDeEnemigos creador = new CreadorDeEnemigos(reloj, "Misiles.txt");
        creador.start();

        // Después de 3 ticks, lanzamos un misil aliado contra el primer enemigo activo
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Busca el primer misil enemigo ya lanzado y lo intercepta
        for (MisilEnemigo enemigo : creador.getMisiles()) {
            if (enemigo.isAlive() && !enemigo.estaDestruido()) {
                MisilAliado aliado = new MisilAliado(enemigo, reloj);
                aliado.start();
                System.out.println("Misil aliado lanzado contra " + enemigo.getNombre());
                break;
            }
        }
    }
}