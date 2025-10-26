package manager;

import model.Cabaña;
import model.Reserva;
import model.EstadoReserva; 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.ServicioExtra;

//simula la base de datos de Reservas.

public class ReservaManager {

    private List<Reserva> reservas;
    private int proximoId = 1; 

    public ReservaManager() {
        this.reservas = new ArrayList<>();
    }

  
    private boolean isCabañaDisponible(Cabaña cabaña, Date fechaInicio, Date fechaFin, int idReservaExcluir) {
        for (Reserva r : this.reservas) {
            
            if (r.getReservaId() == idReservaExcluir) {
                continue;
            }
            
            if (r.getCabaña().getCabañaId() != cabaña.getCabañaId() || 
                r.getEstado() == EstadoReserva.CANCELADA) {
                continue;
            }

            boolean haySuperposicion = 
                (fechaInicio.before(r.getFechaFin())) && 
                (fechaFin.after(r.getFechaInicio()));

            if (haySuperposicion) {
                return false; 
            }
        }
        return true; 
    }
    
    public void crearReserva(Reserva reserva) throws Exception {
        

        if (!isCabañaDisponible(reserva.getCabaña(), reserva.getFechaInicio(), reserva.getFechaFin(), 0)) {
            throw new Exception("La cabaña no está disponible en esas fechas.");
        }


        reserva.setReservaId(proximoId++);  
        this.reservas.add(reserva);      
        System.out.println("Reserva creada con éxito: ID " + reserva.getReservaId());
    }

    public void modificarReserva(int idReserva, Cabaña nuevaCabaña, Date nuevaFechaInicio, Date nuevaFechaFin, int nuevaCantPasajeros, List<ServicioExtra> nuevosServicios) throws Exception {
        
        Reserva reservaAModificar = buscarReservaPorId(idReserva);
        if (reservaAModificar == null) {
            throw new Exception("No se encontró la reserva a modificar.");
        }
        
        if (reservaAModificar.getEstado() != EstadoReserva.ACTIVA) {
            throw new Exception("Solo se pueden modificar reservas 'ACTIVAS'.");
        }
        
        // Re-validar disponibilidad (RF6), excluyendo esta misma reserva
        if (!isCabañaDisponible(nuevaCabaña, nuevaFechaInicio, nuevaFechaFin, idReserva)) {
            throw new Exception("La cabaña no está disponible en las NUEVAS fechas seleccionadas.");
        }
        
        // Validar capacidad
        if (nuevaCantPasajeros > nuevaCabaña.getCapacidad()) {
             throw new Exception("Error: La cantidad de pasajeros (" + nuevaCantPasajeros + 
                                  ") excede la capacidad de la cabaña (" + nuevaCabaña.getCapacidad() + ").");
        }

        double nuevoPrecioNoche = nuevaCabaña.getPrecioPorNoche(nuevaCantPasajeros);
        
        //aplicamos los cambios ---
        reservaAModificar.setCliente(reservaAModificar.getCliente());
        reservaAModificar.setCabaña(nuevaCabaña);
        reservaAModificar.setFechaInicio(nuevaFechaInicio);
        reservaAModificar.setFechaFin(nuevaFechaFin);
        reservaAModificar.setCantidadPasajeros(nuevaCantPasajeros);
        reservaAModificar.setPrecioFinalPorNoche(nuevoPrecioNoche);
        
        // Actualizar servicios
        reservaAModificar.getServiciosContratados().clear(); 
        for(ServicioExtra s : nuevosServicios) { 
            reservaAModificar.agregarServicio(s);
        }
        
        System.out.println("Reserva modificada con éxito: ID " + idReserva);
    }
    
    
    //reserva específica por su ID.
    public Reserva buscarReservaPorId(int id) {
        for (Reserva r : this.reservas) {
            if (r.getReservaId() == id) {
                return r;
            }
        }
        return null;
    }

    public List<Reserva> obtenerReservasActivas() {
        List<Reserva> activas = new ArrayList<>();
        for (Reserva r : this.reservas) {
            if (r.getEstado() == EstadoReserva.ACTIVA) {
                activas.add(r);
            }
        }
        return activas;
    }

    public List<Reserva> obtenerTodasLasReservas() {
        return new ArrayList<>(this.reservas);
    }
    
    public void cancelarReserva(int id) throws Exception {
        Reserva r = buscarReservaPorId(id);
        if (r == null) {
            throw new Exception("No se encontró la reserva con ID: " + id);
        }

        //no se puede cancelar si ya empezó la estadía
        if (r.getEstadia() != null && r.getEstadia().getCheckIn() != null) {
            throw new Exception("Error: No se puede cancelar una reserva con Check-in ya registrado.");
        }
        
        //no se puede cancelar algo que no esté activo
        if (r.getEstado() != EstadoReserva.ACTIVA) {
            throw new Exception("Error: La reserva ya está " + r.getEstado().toString().toLowerCase() + ".");
        }

        r.cancelar(); 
        System.out.println("Reserva cancelada: ID " + r.getReservaId());
    }
}
    
