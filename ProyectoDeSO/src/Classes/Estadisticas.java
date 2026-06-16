package Classes;

public class Estadisticas {

    private int misilesCreados;
    private int misilesInterceptados;
    private int misilesImpactados;

    public synchronized void incrementarMisilesCreados() { misilesCreados++; }
    public synchronized void incrementarMisilesInterceptados() { misilesInterceptados++; }
    public synchronized void incrementarMisilesImpactados() { misilesImpactados++; }

    public int getMisilesInterceptados() { return misilesInterceptados; }
    public int getMisilesImpactados() { return misilesImpactados; }
    public int getMisilesCreados() { return misilesCreados; }

    public void mostrarEstadisticas() {
        System.out.println("╔══════════════════════════════════════════╗");
        System.out.println("║         ESTADÍSTICAS FINALES             ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║ Misiles Enemigos Creados: " + misilesCreados + "              ║");
        System.out.println("║ Misiles Enemigos Interceptados: " + misilesInterceptados +"        ║");
        System.out.println("║ Misiles Enemigos Impactados: " + misilesImpactados + "           ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }
}
