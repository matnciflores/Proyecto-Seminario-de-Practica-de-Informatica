package model;

import java.util.Map;

public class Cabaña {

    private int cabañaId;
    private String numero; 
    private EstadoCabaña estado;
    private int capacidad;
    
    private Map<Integer, Double> preciosPorNoche; 

    public Cabaña(String numero, int capacidad, Map<Integer, Double> precios) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.preciosPorNoche = precios; // <-- ARREGLO
        this.estado = EstadoCabaña.DISPONIBLE; 
    }

    //devuelve el precio por noche para una cantidad específica de pasajeros.
    public double getPrecioPorNoche(int cantidadPasajeros) throws Exception {
        if (!preciosPorNoche.containsKey(cantidadPasajeros)) {
            throw new Exception("No hay tarifa definida para " + cantidadPasajeros + " pasajeros en esta cabaña.");
        }
        return preciosPorNoche.get(cantidadPasajeros);
    }

    
    public int getCabañaId() {
        return cabañaId; }
    public void setCabañaId(int cabañaId) {
        this.cabañaId = cabañaId; }
    public String getNumero() {
        return numero; }
    public void setNumero(String numero) {
        this.numero = numero; }
    public EstadoCabaña getEstado() {
        return estado; }
    public void marcarOcupada() {
        this.estado = EstadoCabaña.OCUPADA; }
    public void marcarDisponible() {
        this.estado = EstadoCabaña.DISPONIBLE; }
    public int getCapacidad() {
        return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    
    @Override
    public String toString() {
        String precioBase = "";
        if (preciosPorNoche.containsKey(2)) {
            precioBase = " (Base: $" + preciosPorNoche.get(2) + "/noche)";
        }
        return numero + " (Cap: " + capacidad + ")" + precioBase;
    }
}
