package model;

import java.text.SimpleDateFormat; 
import java.util.Date;
import java.util.List;

public class Reporte {

    private String tipo;
    private String periodo;

    public String generarOcupacion(List<Cabaña> todasLasCabañas) {
        int ocupadas = 0;
        for (Cabaña c : todasLasCabañas) {
            if (c.getEstado() == EstadoCabaña.OCUPADA) {
                ocupadas++;
            }
        }
        double porcentajeOcupacion = (double) ocupadas / todasLasCabañas.size() * 100;
        
        return String.format("Reporte de Ocupación:\n- Cabañas Totales: %d\n- Cabañas Ocupadas: %d\n- Ocupación: %.2f%%",
                todasLasCabañas.size(), ocupadas, porcentajeOcupacion);
    }

    //genera un reporte detallado de ingresos  para un rango de fechas
    public String generarIngresosDetallado(List<Reserva> reservasFinalizadas, Date fechaDesde, Date fechaHasta) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        
        sb.append(String.format("--- Reporte de Ingresos (RF14) ---\n"));
        sb.append(String.format("Período: %s al %s\n\n", sdf.format(fechaDesde), sdf.format(fechaHasta)));
        
        // Encabezados de la tabla
        sb.append(String.format("%-30s | %-10s | %-15s\n", "Cliente", "Pasajeros", "Monto Pagado"));
        sb.append("--------------------------------------------------------------\n");

        double totalIngresos = 0;
        
        if (reservasFinalizadas.isEmpty()) {
            sb.append("No se encontraron reservas finalizadas en este período.");
        } else {
            for (Reserva r : reservasFinalizadas) {
                if (r.getPago() != null) {
                    String cliente = r.getCliente().getNombre() + " " + r.getCliente().getApellido();
                    if (cliente.length() > 29) {
                        cliente = cliente.substring(0, 28) + ".";
                    }
                    
                    sb.append(String.format("%-30s | %-10d | $%-14.2f\n",
                            cliente,
                            r.getCantidadPasajeros(),
                            r.getPago().getMontoTotal()
                    ));
                    totalIngresos += r.getPago().getMontoTotal();
                }
            }
        }

        sb.append("--------------------------------------------------------------\n");
        sb.append(String.format("TOTAL INGRESOS PERÍODO: $%.2f\n", totalIngresos));
        
        return sb.toString();
    }

    //historial de un cliente específico
    public String generarHistorialCliente(Cliente cliente, List<Reserva> todasLasReservas) {
        StringBuilder sb = new StringBuilder();
        sb.append("Historial de: ").append(cliente.getNombre()).append(" ").append(cliente.getApellido()).append("\n");
        
        int count = 0;
        for (Reserva r : todasLasReservas) {
            if (r.getCliente().getClienteId() == cliente.getClienteId()) {
                sb.append(String.format("- Reserva #%d | Cabaña: %s | Estado: %s\n",
                        r.getReservaId(), r.getCabaña().getNumero(), r.getEstado()));
                count++;
            }
        }
        
        if (count == 0) {
            sb.append("El cliente no tiene reservas en el historial.");
        }
        
        return sb.toString();
    }
}