package Classes;
import java.util.List;
import java.util.ArrayList;


public class ControladorDeAliados extends Thread {

    private RelojGlobal reloj;
    private Estadisticas estadisticas;
    private MisilEnemigo misilEnemigo; // Variable para almacenar el misil enemigo actual a procesar
    private List<Interceptor> interceptoresDisponibles; 
    private List<MisilEnemigo> misilesEnemigos;
    private ColaDePrioridad cola;
    
    public ControladorDeAliados(RelojGlobal reloj, Estadisticas estadisticas, List<Interceptor> interceptores, List<MisilEnemigo> misilesEnemigos) {
        this.reloj = reloj;
        this.estadisticas = estadisticas;
        this.interceptoresDisponibles = interceptores;
        this.misilesEnemigos = misilesEnemigos;
        this.cola = new ColaDePrioridad();
        reloj.registrar();
    }

    public void run() { // Registrar el controlador de aliados en el reloj para sincronización
        while (reloj.estaActivo()) {
            reloj.esperarTick();
            while((misilEnemigo = cola.siguiente(misilesEnemigos)) != null) {
                AsignarInterceptor(misilEnemigo);
            }
        }
        reloj.darseDeBaja(); // El controlador de aliados se da de baja del reloj al finalizar su ciclo de vida
    }

    private boolean esFactible(MisilEnemigo enemigo) {
        int ticksHastaImpacto = enemigo.getTicksHastaImpacto();
        int tiempoDeRespuesta = calcularRespuesta(enemigo);
        return ticksHastaImpacto > tiempoDeRespuesta;
    }

    private int calcularRespuesta(MisilEnemigo enemigo) {
        int ticksHastaImpacto = enemigo.getTicksHastaImpacto();
        int tiempoDeRespuesta = ticksHastaImpacto/2 + enemigo.getObjetivo().getTicksDesdeBase(); // Supongamos que el interceptor tarda 5 ticks en interceptar, esto podría ser dinámico según la distancia o características del misil
        return tiempoDeRespuesta;
    }

    private Interceptor buscarInterceptorLibre() {
        for (Interceptor i : interceptoresDisponibles) {
            if (i.isDisponible()) return i;
        }
        return null;
    }

    public void AsignarInterceptor(MisilEnemigo enemigo) {
        Interceptor libre = buscarInterceptorLibre();
        if (!esFactible(enemigo)) {
            enemigo.ignorar();
            System.out.println("No es factible interceptar " + enemigo.getNombre() + ". Se ignora el misil.");
            RegistroDeEventos.log(reloj.getTick(), "No es factible interceptar " + enemigo.getNombre() + ". Se ignora el misil.");
            return;
        }else if (libre != null && libre.asignar(enemigo, enemigo.getTicksHastaImpacto())) {
            enemigo.atender();
            System.out.println("Interceptor " + libre.getNombre() + " asignado a " + enemigo.getNombre());
            RegistroDeEventos.log(reloj.getTick(), "Interceptor " + libre.getNombre() + " asignado a " + enemigo.getNombre());
        }
    }
}