package model;

public class ServicioExtra {

    private int servicioId;
    private String descripcion;
    private double precio;

    //onstructor para crear un nuevo servicio extra.
     
    public ServicioExtra(String descripcion, double precio) {
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public int getServicioId() {
        return servicioId;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return descripcion + " ($" + precio + ")";
    }
}
