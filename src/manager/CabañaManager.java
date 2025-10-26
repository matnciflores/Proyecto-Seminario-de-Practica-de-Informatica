package manager;

import model.Cabaña;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap; 

public class CabañaManager {

    private List<Cabaña> cabañas;
    private int proximoId = 1;

    public CabañaManager() {
        this.cabañas = new ArrayList<>();
        
        //Datos de prueba
        try {           
            Map<Integer, Double>preciosKurmi = new HashMap<>();
           preciosKurmi.put(2, 15000.0);
           preciosKurmi.put(3, 18000.0);
           preciosKurmi.put(4, 20000.0);
           agregarCabaña(new Cabaña("Cabaña Kurmi", 4,preciosKurmi));
           
            Map<Integer, Double> preciosSumaq = new HashMap<>();
            preciosSumaq.put(1, 18000.0);
            preciosSumaq.put(2, 22000.0);
            agregarCabaña(new Cabaña("Cabaña Sumaq", 2, preciosSumaq));

            Map<Integer, Double> preciosQinti = new HashMap<>();
            preciosQinti.put(2, 28000.0);
            agregarCabaña(new Cabaña("Cabaña Qinti", 2, preciosQinti));
            
        } catch (Exception e) {
            System.err.println("Error precargando cabañas: " + e.getMessage());
        }
    }
    
    public void agregarCabaña(Cabaña cabaña) throws Exception {
        if (buscarCabañaPorNumero(cabaña.getNumero()) != null) {
            throw new Exception("Error: Ya existe una cabaña con el número/nombre: " + cabaña.getNumero());
        }
        cabaña.setCabañaId(proximoId++);
        this.cabañas.add(cabaña);
        System.out.println("Cabaña agregada: " + cabaña.getNumero());
    }

    public Cabaña buscarCabañaPorNumero(String numero) {
        for (Cabaña c : this.cabañas) {
            if (c.getNumero().equalsIgnoreCase(numero)) {
                return c;
            }
        }
        return null;
    }
    
    public List<Cabaña> obtenerTodasLasCabañas() {
        return new ArrayList<>(this.cabañas);
    }
    
    public List<Cabaña> obtenerCabañasDisponibles() {
        List<Cabaña> disponibles = new ArrayList<>();
        for (Cabaña c : this.cabañas) {
            if (c.getEstado() == model.EstadoCabaña.DISPONIBLE) {
                disponibles.add(c);
            }
        }
        return disponibles;
    }
}