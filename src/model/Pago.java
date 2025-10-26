package model;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Pago {
    
    private int pagoId;
    private double montoTotal;
    private Date fecha;
    
    private Reserva reserva;

    public Pago(Reserva reserva) {
        this.reserva = reserva;
        this.fecha = new Date();
    }

 
    public void calcularTotal() {
        // 1. Calcular días de estadía
        long diffEnMilisegundos = Math.abs(reserva.getFechaFin().getTime() - reserva.getFechaInicio().getTime());
        long dias = TimeUnit.DAYS.convert(diffEnMilisegundos, TimeUnit.MILLISECONDS);
        
        if (dias == 0) {
            dias = 1;
        }

        double costoCabaña = dias * reserva.getPrecioFinalPorNoche();

        double costoServicios = 0;
        for (ServicioExtra servicio : reserva.getServiciosContratados()) {
            costoServicios += servicio.getPrecio();
        }

        this.montoTotal = costoCabaña + costoServicios;
    }

    public String generarComprobante() {
        //el total esté calculado
        if (this.montoTotal == 0) {
            calcularTotal();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("--- Comprobante de Pago LikeHome ---\n");
        sb.append("Cliente: ").append(reserva.getCliente().getNombre()).append(" ").append(reserva.getCliente().getApellido()).append("\n");
        sb.append("Cabaña: ").append(reserva.getCabaña().getNumero()).append("\n");
        sb.append("Fecha: ").append(this.fecha.toString()).append("\n");
        sb.append("-------------------------------------\n");
        sb.append("Detalle:\n");
        sb.append("- Estadía (")
          .append(reserva.getCantidadPasajeros()).append(" pasajeros, $")
          .append(reserva.getPrecioFinalPorNoche()).append(" x noche)\n");
        for (ServicioExtra s : reserva.getServiciosContratados()) {
            sb.append("- ").append(s.getDescripcion()).append(" ($").append(s.getPrecio()).append(")\n");
        }
        sb.append("-------------------------------------\n");
        sb.append("TOTAL: $").append(this.montoTotal).append("\n");
        
        return sb.toString();
    }

  
    public int getPagoId() {
        return pagoId;
    }

    public void setPagoId(int pagoId) {
        this.pagoId = pagoId;
    }

    public double getMontoTotal() {
        if (this.montoTotal == 0) {
            calcularTotal();
        }
        return montoTotal;
    }

    public Date getFecha() {
        return fecha;
    }
}
