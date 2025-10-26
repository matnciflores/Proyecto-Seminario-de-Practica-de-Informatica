package model;

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

    public String generarIngresos(List<Reserva> reservasFinalizadas) {
        double totalIngresos = 0;
        for (Reserva r : reservasFinalizadas) {
            if (r.getEstado() == EstadoReserva.FINALIZADA && r.getPago() != null) {
                totalIngresos += r.getPago().getMontoTotal();
            }
        }
        return "Reporte de Ingresos:\n- Total generado (reservas finalizadas): $" + totalIngresos;
    }

    //historial de un cliente específico[cite: 645].
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