package view;

import manager.ReservaManager;
import model.Estadia;
import model.Reserva;
import model.Pago;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class EstadiaPanel extends JPanel {

    private ReservaManager reservaManager;
    private MainView mainView;

    private JTable tablaReservas;
    private DefaultTableModel tableModel;
    private JButton btnCheckIn;
    private JButton btnCheckOut;
    private JButton btnActualizar;
    private JButton btnCancelar;
    private JButton btnModificar;
    private JLabel lblMensaje;

    public EstadiaPanel(ReservaManager manager, MainView mainView) {
        this.reservaManager = manager;
        this.mainView = mainView; 

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //tabla de reservas activas ---
        String[] columnas = {"ID Reserva", "Cliente", "Cabaña", "Nro. Pasajeros", "Fecha Inicio", "Serv. Extras", "Check-in", "Check-out"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla no editable
            }
        };
        tablaReservas = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(tablaReservas);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Reservas Activas"));

        add(scrollPane, BorderLayout.CENTER);

        //botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnCheckIn = new JButton("Registrar Check-in");
        btnCheckOut = new JButton("Registrar Check-out");
        btnActualizar = new JButton("Actualizar Lista");
        btnCancelar = new JButton("Cancelar Reserva (RF5)");
        btnCancelar.setForeground(Color.RED);

        btnModificar = new JButton("Modificar Reserva");
        btnModificar.setForeground(new Color(0, 100, 0)); 

        panelBotones.add(btnCheckIn);
        panelBotones.add(btnCheckOut);
        panelBotones.add(btnModificar); 
        panelBotones.add(btnCancelar);
        panelBotones.add(btnActualizar);

        add(panelBotones, BorderLayout.NORTH);

        //mensajes
        lblMensaje = new JLabel("Seleccione una reserva de la lista.");
        add(lblMensaje, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> actualizarTabla());
        btnCheckIn.addActionListener(e -> registrarCheckIn());
        btnCheckOut.addActionListener(e -> registrarCheckOut());
        btnCancelar.addActionListener(e -> cancelarReserva());
        btnModificar.addActionListener(e -> modificarReserva()); 

        actualizarTabla();
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0); //limpiar tabla
        List<Reserva> activas = reservaManager.obtenerReservasActivas();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Reserva r : activas) {
            Estadia est = r.getEstadia();
            String checkInStr = (est != null && est.getCheckIn() != null) ? sdf.format(est.getCheckIn()) : "---";
            String checkOutStr = (est != null && est.getCheckOut() != null) ? sdf.format(est.getCheckOut()) : "---";

            StringBuilder serviciosStr = new StringBuilder();
        if (r.getServiciosContratados().isEmpty()) {
            serviciosStr.append("Ninguno");
        } else {
            for (model.ServicioExtra s : r.getServiciosContratados()) {
                serviciosStr.append(s.getDescripcion()).append(", ");
        }

            serviciosStr.setLength(serviciosStr.length() - 2); 
        }
            
            Object[] fila = {
                r.getReservaId(),
                r.getCliente().getNombre() + " " + r.getCliente().getApellido(),
                r.getCabaña().getNumero(),
                r.getCantidadPasajeros(),
                sdf.format(r.getFechaInicio()),
                serviciosStr.toString(),
                checkInStr,
                checkOutStr
            };
            tableModel.addRow(fila);
        }
        lblMensaje.setText("Lista de reservas activas actualizada.");
    }
    
    private Reserva getReservaSeleccionada() throws Exception {
        int filaSel = tablaReservas.getSelectedRow();
        if (filaSel == -1) {
            throw new Exception("Error: Debe seleccionar una reserva de la tabla.");
        }
        int reservaId = (int) tableModel.getValueAt(filaSel, 0);
        Reserva r = reservaManager.buscarReservaPorId(reservaId);
        if (r == null) {
            throw new Exception("Error: No se encontró la reserva. Actualice la lista.");
        }
        return r;
    }

    //Check-in
    private void registrarCheckIn() {
        try {
            Reserva r = getReservaSeleccionada();
            if (r.getEstadia() != null && r.getEstadia().getCheckIn() != null) {
                throw new Exception("Error: El Check-in para esta reserva ya fue registrado.");
            }
            Estadia nuevaEstadia = new Estadia(r);
            nuevaEstadia.registrarCheckIn();
            r.setEstadia(nuevaEstadia);
            lblMensaje.setForeground(Color.BLUE);
            lblMensaje.setText("Check-in registrado para Reserva ID: " + r.getReservaId() +
                               ". Cabaña '" + r.getCabaña().getNumero() + "' ahora está OCUPADA.");
            actualizarTabla();
        } catch (Exception ex) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText(ex.getMessage());
        }
    }

    //Check-out
    private void registrarCheckOut() {
        try {
            Reserva r = getReservaSeleccionada();
            if (r.getEstadia() == null || r.getEstadia().getCheckIn() == null) {
                throw new Exception("Error: Debe registrar el Check-in antes del Check-out.");
            }
            if (r.getEstadia().getCheckOut() != null) {
                throw new Exception("Error: El Check-out para esta reserva ya fue registrado.");
            }
            r.getEstadia().registrarCheckOut();
            Pago nuevoPago = new Pago(r);
            nuevoPago.calcularTotal();
            r.setPago(nuevoPago);
            String comprobante = nuevoPago.generarComprobante();
            mostrarComprobante(comprobante);
            lblMensaje.setForeground(Color.BLUE);
            lblMensaje.setText("Check-out registrado para Reserva ID: " + r.getReservaId() +
                               ". Cabaña '" + r.getCabaña().getNumero() + "' ahora está DISPONIBLE.");
            actualizarTabla();
        } catch (Exception ex) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText(ex.getMessage());
        }
    }

    //comprobante de pago en una ventana emergente
    private void mostrarComprobante(String textoComprobante) {
        JTextArea textArea = new JTextArea(20, 40);
        textArea.setText(textoComprobante);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane, "Comprobante de Pago", JOptionPane.INFORMATION_MESSAGE);
    }

    //cancelar reserva 
    private void cancelarReserva() {
        try {
            Reserva r = getReservaSeleccionada();
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro de que desea CANCELAR la reserva ID: " + r.getReservaId() + "?\n" +
                "Cliente: " + r.getCliente().toString() + "\n" +
                "Cabaña: " + r.getCabaña().getNumero(),
                "Confirmar Cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (confirm == JOptionPane.YES_OPTION) {
                reservaManager.cancelarReserva(r.getReservaId());
                lblMensaje.setForeground(Color.BLUE);
                lblMensaje.setText("Reserva ID: " + r.getReservaId() + " ha sido CANCELADA.");
                actualizarTabla();
            } else {
                lblMensaje.setForeground(Color.BLACK);
                lblMensaje.setText("Operación de cancelación abortada.");
            }
        } catch (Exception ex) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText(ex.getMessage());
        }
    }

    private void modificarReserva() {
        try {
            Reserva r = getReservaSeleccionada();
            if (r.getEstadia() != null && r.getEstadia().getCheckIn() != null) {
                throw new Exception("Error: No se puede modificar una reserva con Check-in ya registrado.");
            }
            mainView.irAEdicionReserva(r); // Llama al método de MainView
        } catch (Exception ex) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText(ex.getMessage());
        }
    }
}
