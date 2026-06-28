package Classes;
import java.util.HashMap;
import java.util.Map;

public class RegistroDeZonas {
    public static final Map<String, Zona> ZONAS = new HashMap<>();

    static {
        ZONAS.put("Hospital", new Zona("Hospital","URGENTE", 100, 1));
        ZONAS.put("CentralElectrica", new Zona("CentralElectrica", "URGENTE",  90, 2));
        ZONAS.put("Aeropuerto", new Zona("Aeropuerto","ALTO", 80, 3));
        ZONAS.put("Escuela", new Zona("Escuela","ALTO",70, 5));
        ZONAS.put("ZonaIndustrial", new Zona("ZonaIndustrial","MEDIO",50, 3));
        ZONAS.put("DepositoMilitar", new Zona("DepositoMilitar","MEDIO", 50, 1));
        ZONAS.put("Datacenter",new Zona("Datacenter","BAJO",30, 4));
    }


    public static Zona obtener(String nombre) {
        return ZONAS.getOrDefault(nombre, new Zona(nombre, "BAJO", 30, 35));
    }
}