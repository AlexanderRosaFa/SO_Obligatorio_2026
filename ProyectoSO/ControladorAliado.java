import java.util.ArrayList;
import java.util.List;

public class ControladorAliado extends Thread {

    private final List<MisilEnemigo> enemigos;
    private final RelojGlobal reloj;
    private final List<MisilEnemigo> yaAsignados = new ArrayList<>();

    private static final int VELOCIDAD_ALIADO = 10;
    private static final int VELOCIDAD_ENEMIGO = 5;

    private int asignados = 0;
    private int descartados = 0;

    public ControladorAliado(List<MisilEnemigo> enemigos, RelojGlobal reloj) {
        this.enemigos = enemigos;
        this.reloj = reloj;
    }

    @Override
    public void run() {
        while (true) {
            reloj.esperarTick();

            // Limpiar del registro los que ya fueron destruidos o impactaron
            yaAsignados.removeIf(m -> m.estaDestruido() || !m.isAlive());

            MisilEnemigo objetivo = elegirObjetivo();

            if (objetivo == null) {
                System.out.println("[CONTROL] No hay amenazas nuevas activas.");
                continue;
            }

            if (esFactible(objetivo)) {
                System.out.println("[CONTROL] Lanzando aliado contra " + objetivo.getNombre()
                        + " (posición: " + objetivo.getPosicion() + ")");
                MisilAliado aliado = new MisilAliado(objetivo, reloj);
                aliado.start();
                yaAsignados.add(objetivo); // ✅ marca como asignado
                asignados++;
            } else {
                System.out.println("[CONTROL] No es factible interceptar " + objetivo.getNombre()
                        + " (posición: " + objetivo.getPosicion() + ") - probable impacto");
                descartados++;
            }
        }
    }

    private MisilEnemigo elegirObjetivo() {
        MisilEnemigo masUrgente = null;
        int menorTiempo = Integer.MAX_VALUE;

        for (MisilEnemigo m : enemigos) {
            if (!m.isAlive() || m.estaDestruido()) continue;
            if (yaAsignados.contains(m)) continue; // ✅ saltea los ya asignados

            int ticksRestantes = m.getPosicion() / VELOCIDAD_ENEMIGO;
            if (ticksRestantes < menorTiempo) {
                menorTiempo = ticksRestantes;
                masUrgente = m;
            }
        }

        return masUrgente;
    }

    private boolean esFactible(MisilEnemigo enemigo) {
        int posActual = enemigo.getPosicion();
        int ticksHastaImpacto = posActual / VELOCIDAD_ENEMIGO;
        double ticksHastaColision = (double) posActual / (VELOCIDAD_ALIADO + VELOCIDAD_ENEMIGO);

        System.out.println("[CONTROL] " + enemigo.getNombre()
                + " | impacta en " + ticksHastaImpacto + " ticks"
                + " | colisión estimada en " + String.format("%.1f", ticksHastaColision) + " ticks");

        return ticksHastaColision < ticksHastaImpacto;
    }

    public void imprimirResumen() {
        System.out.println("[CONTROL] Resumen | asignados=" + asignados + " | descartados=" + descartados);
    }
}
