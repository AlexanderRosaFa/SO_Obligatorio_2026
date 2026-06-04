public class MisilAliado extends Thread {

    private int posicion = 0;
    private MisilEnemigo objetivo;
    private RelojGlobal reloj;

    public MisilAliado(MisilEnemigo objetivo, RelojGlobal reloj) {
        this.objetivo = objetivo;
        this.reloj = reloj;
    }

    @Override
    public void run() {
        while (!objetivo.estaDestruido()) {
            reloj.esperarTick();

            posicion += 5;  

            System.out.println("Aliado -> posición: " + posicion);

            int posicionEnemigo = objetivo.getPosicion(); 

            if (posicion >= posicionEnemigo) {
                objetivo.destruir();
                System.out.println(">>> MISIL ENEMIGO INTERCEPTADO en posición: " + posicion + " <<<");
                break;
            }
        }
    }
}
