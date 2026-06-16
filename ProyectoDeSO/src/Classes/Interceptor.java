package Classes;

public class Interceptor extends Thread {

    private String nombre;
    private MisilEnemigo objetivo = null;
    private RelojGlobal reloj;
    private Estadisticas estadisticas;
    private boolean disponible = true; // Indica si el interceptor está disponible para ser asignado a un misil
    private boolean activo = true; // Indica si el interceptor está activo (no destruido)
    private int ticksHastaImpacto; // Ticks restantes para que el misil alcance su objetivo

    public Interceptor(String nombre, RelojGlobal reloj, Estadisticas estadisticas) {
        this.nombre = nombre;
        this.reloj = reloj;
        this.estadisticas = estadisticas;
        reloj.registrar();
    }


    public void run() {
        while (activo && reloj.estaActivo()) {
            reloj.esperarTick();
            if (!disponible && objetivo != null) {
                perseguir();
            }
        }
        reloj.darseDeBaja(); 
    }

    public synchronized boolean asignar(MisilEnemigo amenaza, int tickHastaImpacto) {
        if (!disponible) return false;
        objetivo  = amenaza;
        disponible = false;
        ticksHastaImpacto = tickHastaImpacto;
        return true;
    }

    private void perseguir() {

        if (ticksHastaImpacto > 0) {
            ticksHastaImpacto--;
        }else {
                System.out.println(nombre + " interceptó a " + objetivo.getNombre());
                estadisticas.incrementarMisilesInterceptados();
                RegistroDeEventos.log(reloj.getTick(), nombre + " interceptó a " + objetivo.getNombre());
                objetivo.destruir();
                objetivo = null;
                disponible = true;
        }
        
    }
     

    // Getters y setters
    public String getNombre() { return nombre; }
    public MisilEnemigo getObjetivo() { return objetivo; }
    public int getTicksHastaImpacto() { return ticksHastaImpacto; }

    public boolean isDisponible() { return disponible; }
    public boolean isActivo() { return activo; }

    public void setObjetivo(MisilEnemigo objetivo) { this.objetivo = objetivo; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setActivo(boolean activo) { this.activo = activo; }


}
