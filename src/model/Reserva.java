package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reserva {

    private int reservaId;
    private Date fechaInicio;
    private Date fechaFin;
    private EstadoReserva estado;
    
    private Cliente cliente;
    private Cabaña cabaña;
    private List<ServicioExtra> serviciosContratados;
    private Estadia estadia;
    private Pago pago;
    
    // --- ATRIBUTOS NUEVOS ---
    private int cantidadPasajeros;
    private double precioFinalPorNoche; 


    public Reserva(Cliente cliente, Cabaña cabaña, Date fechaInicio, Date fechaFin, int cantidadPasajeros) throws Exception {
        this.cliente = cliente;
        this.cabaña = cabaña;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = EstadoReserva.ACTIVA;
        this.serviciosContratados = new ArrayList<>();
        
        this.cantidadPasajeros = cantidadPasajeros;
        this.precioFinalPorNoche = cabaña.getPrecioPorNoche(cantidadPasajeros);
    }

    public int getCantidadPasajeros() {
        return cantidadPasajeros;
    }

    public double getPrecioFinalPorNoche() {
        return precioFinalPorNoche;
    }
    
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }
    
    public void setCantidadPasajeros(int cantidadPasajeros) {
        this.cantidadPasajeros = cantidadPasajeros;
    }

    public void setPrecioFinalPorNoche(double precioFinalPorNoche) {
        this.precioFinalPorNoche = precioFinalPorNoche;
    }
    
    public void modificar(Date nuevaFechaInicio, Date nuevaFechaFin) {
        this.fechaInicio = nuevaFechaInicio;
        this.fechaFin = nuevaFechaFin;
    }
    public void cancelar() { this.estado = EstadoReserva.CANCELADA; }
    public void agregarServicio(ServicioExtra servicio) { this.serviciosContratados.add(servicio); }
    public void quitarServicio(ServicioExtra servicio) { this.serviciosContratados.remove(servicio); }
    public void finalizarReserva() { this.estado = EstadoReserva.FINALIZADA; }
    
    public int getReservaId() {
        return reservaId; 
    }
    public void setReservaId(int reservaId) {
        this.reservaId = reservaId; 
    }
    public Date getFechaInicio() {
        return fechaInicio; }
    public Date getFechaFin() {
        return fechaFin; }
    public EstadoReserva getEstado() {
        return estado; }
    public Cliente getCliente() {
        return cliente; }
    public Cabaña getCabaña() {
        return cabaña; }
    public void setCabaña(Cabaña cabaña) {
        this.cabaña = cabaña; }
    public List<ServicioExtra> getServiciosContratados() {
        return serviciosContratados; }
    public Estadia getEstadia() {
        return estadia; }
    public void setEstadia(Estadia estadia) {
        this.estadia = estadia; }
    public Pago getPago() {
        return pago; }
    public void setPago(Pago pago) {
        this.pago = pago; }
}