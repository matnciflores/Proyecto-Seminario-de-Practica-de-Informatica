package manager;

import model.ServicioExtra;
import java.util.ArrayList;
import java.util.List;

//simula la base de datos de los Servicios Extras disponibles.

public class ServicioExtraManager {

    private List<ServicioExtra> serviciosDisponibles;
    private int proximoId = 1;

    public ServicioExtraManager() {
        this.serviciosDisponibles = new ArrayList<>();
        

        agregarServicio(new ServicioExtra("Desayuno", 2500.00));
        agregarServicio(new ServicioExtra("Excursión a la montaña", 15000.00));
        agregarServicio(new ServicioExtra("Excursión en caballo", 5000.00));
    }

    private void agregarServicio(ServicioExtra servicio) {
        servicio.setServicioId(proximoId++);
        this.serviciosDisponibles.add(servicio);
    }
    
    public List<ServicioExtra> obtenerTodosLosServicios() {
        return new ArrayList<>(this.serviciosDisponibles);
    }
    
    public ServicioExtra buscarServicioPorId(int id) {
        for (ServicioExtra s : this.serviciosDisponibles) {
            if (s.getServicioId() == id) {
                return s;
            }
        }
        return null;
    }
}