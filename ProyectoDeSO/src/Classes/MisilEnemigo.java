package Classes;

public class MisilEnemigo extends Thread {

    private String nombre;
    private Zona objetivo;
    private int tickDeAparicion;
    private int ticksHastaImpacto;
    private RelojGlobal reloj;
    private String estado = "Esperando"; // Estados: "Esperando", "Activo", "Destruido", "Impactado"
    private boolean activo = true;
    private Estadisticas estadisticas;

    public MisilEnemigo(String nombre, Zona objetivo, int tickDeAparicion, int ticksHastaImpacto, RelojGlobal reloj, Estadisticas estadisticas) {
        this.nombre = nombre;
        this.tickDeAparicion = tickDeAparicion;
        this.objetivo = objetivo;
        this.ticksHastaImpacto = ticksHastaImpacto;
        this.reloj = reloj;
        this.estadisticas = estadisticas;
        reloj.registrar();
        estadisticas.incrementarMisilesCreados();
    }

    @Override
    public void run() { 
        if (estado.equals("Esperando")) {
            while (reloj.getTick() < tickDeAparicion) {
                reloj.esperarTick();
            }
            RegistroDeEventos.log(reloj.getTick(), nombre + " ha aparecido con objetivo " + objetivo.getNombre());
            estado = "Activo";
        }

        while (ticksHastaImpacto > 0 && (estado.equals("Activo") || estado.equals("Ignorado"))) {
            reloj.esperarTick();
            ticksHastaImpacto--;
        }

        // Si está atendido, esperar a que el interceptor resuelva
        while (estado.equals("Atendido")) {
            reloj.esperarTick();
        }

        // Ahora el estado es definitivo: "Interceptado" o llegó a 0 ticks
        if (estado.equals("Interceptado")) {
            RegistroDeEventos.log(reloj.getTick(), nombre + " fue destruido antes de impactar en " + objetivo.getNombre());
        } else {
            estado = "Impactado";
            estadisticas.incrementarMisilesImpactados();
            RegistroDeEventos.log(reloj.getTick(), nombre + " alcanzó su objetivo " + objetivo.getNombre());
        }
        reloj.darseDeBaja();
    }

    // Getters y setters
    public String getNombre()           { return nombre; }
    public Zona getObjetivo()         { return objetivo; }
    public int getTickDeAparicion()   { return tickDeAparicion; }
    public int getTicksHastaImpacto() { return ticksHastaImpacto; }
    public String getEstado()          { return estado; }

    // Métodos para manejar el estado del misil
    public void atender()          { estado = "Atendido"; }
    public void ignorar()          { estado = "Ignorado"; }
    public void destruir()         { estado = "Interceptado"; }
}
