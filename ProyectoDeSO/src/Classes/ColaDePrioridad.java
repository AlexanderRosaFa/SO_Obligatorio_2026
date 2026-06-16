package Classes;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class ColaDePrioridad {

    public MisilEnemigo siguiente(List<MisilEnemigo> misilesEnemigos) {
        PriorityQueue<MisilEnemigo> cola = new PriorityQueue<>(comparador());
        for (MisilEnemigo m : misilesEnemigos) {
            if ("Activo".equals(m.getEstado()) && m.isAlive()) {
                cola.add(m);
            }
        }
        return cola.poll();
    }

    private Comparator<MisilEnemigo> comparador() {
        return Comparator.comparingDouble((MisilEnemigo m) -> {
            double criticidad = m.getObjetivo().getValorCriticidad(); // normalizar si es necesario
            double urgencia   = 1.0 / m.getTicksHastaImpacto();      // invertir: menos ticks = más urgente
            return 0.6 * criticidad + 0.4 * urgencia;
        }).reversed();
    }
}