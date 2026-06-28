package Classes;

public class Zona {

    private String nombre;
    private String criticidad;
    private int valorCriticidad;
    private int TicksDesdeBase;

    public Zona(String nombre, String criticidad, int valorCriticidad, int TicksDesdeBase) {
        this.nombre = nombre;
        this.criticidad = criticidad;
        this.valorCriticidad = valorCriticidad;
        this.TicksDesdeBase = TicksDesdeBase;
    }

    public String getNombre() { return nombre; }
    public String getCriticidad() { return criticidad; }
    public int getValorCriticidad() { return valorCriticidad; }
    public int getTicksDesdeBase() { return TicksDesdeBase; }
    public void setTicksDesdeBase(int ticksDesdeBase) { this.TicksDesdeBase = ticksDesdeBase; }

}
