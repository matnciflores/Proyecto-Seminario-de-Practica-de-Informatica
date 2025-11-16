package view;

import manager.CabañaManager;
import manager.ClienteManager;
import manager.ReservaManager;
import manager.ServicioExtraManager;
import model.Cabaña;
import model.Cliente;
import model.Reserva;
import model.ServicioExtra;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservaPanel extends JPanel {

    private ReservaManager reservaManager;
    private ClienteManager clienteManager;
    private CabañaManager cabañaManager;
    private ServicioExtraManager servicioExtraManager;

    private JComboBox<Cliente> comboClientes;
    private JComboBox<Cabaña> comboCabañas;
    private JSpinner spinnerPasajeros;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnGuardar;
    private JButton btnActualizarListas;
    private JButton btnLimpiar;
    private JLabel lblMensaje;
    private JPanel panelServicios;
    private List<JCheckBox> checkServicios;
    private int idReservaEditando = 0;
    //formateador de fechas
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public ReservaPanel(ReservaManager resManager, ClienteManager cliManager, CabañaManager cabManager, ServicioExtraManager servManager) {
        this.reservaManager = resManager;
        this.clienteManager = cliManager;
        this.cabañaManager = cabManager;
        this.servicioExtraManager = servManager;
        this.checkServicios = new ArrayList<>();
        sdf.setLenient(false); //estricto con las fechas

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Panel del Formulario ---
        JPanel panelFormulario = new JPanel(new GridLayout(7, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos de la Reserva"));

        // --- Creación de Componentes ---
        comboClientes = new JComboBox<>();
        comboCabañas = new JComboBox<>();
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        spinnerPasajeros = new JSpinner(spinnerModel);
        txtFechaInicio = new JTextField();
        txtFechaFin = new JTextField();
        btnGuardar = new JButton("Crear Reserva (Nuevo)");
        btnActualizarListas = new JButton("Actualizar Listas");
        btnLimpiar = new JButton("Limpiar (Nueva Reserva)");
        lblMensaje = new JLabel(" ");
        lblMensaje.setForeground(Color.RED);

        panelFormulario.add(new JLabel("Cliente:"));
        panelFormulario.add(comboClientes);
        panelFormulario.add(new JLabel("Cabaña:"));
        panelFormulario.add(comboCabañas);
        panelFormulario.add(new JLabel("Cantidad de Pasajeros:"));
        panelFormulario.add(spinnerPasajeros);
        panelFormulario.add(new JLabel("Fecha Inicio (dd-MM-yyyy):"));
        panelFormulario.add(txtFechaInicio);
        panelFormulario.add(new JLabel("Fecha Fin (dd-MM-yyyy):"));
        panelFormulario.add(txtFechaFin);
        
        panelFormulario.add(new JLabel("Servicios Extras:"));
        panelServicios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cargarServiciosCheckboxes();
        panelFormulario.add(new JScrollPane(panelServicios));
        
        //panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnActualizarListas);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnGuardar);
        
        panelFormulario.add(panelBotones);

        add(panelFormulario, BorderLayout.NORTH);
        add(lblMensaje, BorderLayout.SOUTH);

        cargarListas();

        btnGuardar.addActionListener(e -> guardarReserva());
        btnActualizarListas.addActionListener(e -> cargarListas());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
       
        comboClientes.setEnabled(false);
    }


    public void limpiarFormulario() {
        idReservaEditando = 0; 
        comboClientes.setSelectedIndex(-1);
        comboClientes.setEnabled(true); //habilitar para reservas nuevas
        comboCabañas.setSelectedIndex(-1);
        spinnerPasajeros.setValue(1);
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        for (JCheckBox chk : checkServicios) {
            chk.setSelected(false);
        }
        btnGuardar.setText("Crear Reserva (Nuevo)");
        lblMensaje.setForeground(Color.BLACK);
        lblMensaje.setText("Formulario limpiado. Listo para nueva reserva.");
    }
    
    public void cargarReservaParaModificar(Reserva reserva) {
        limpiarFormulario(); //limpia todo primero
        
        idReservaEditando = reserva.getReservaId();
        
        comboClientes.setSelectedItem(reserva.getCliente());
        comboClientes.setEnabled(false);
        
        //cargar Cabaña
        comboCabañas.setSelectedItem(reserva.getCabaña());
        
        //cargar Pasajeros
        spinnerPasajeros.setValue(reserva.getCantidadPasajeros());
        
        //cargar Fechas
        txtFechaInicio.setText(sdf.format(reserva.getFechaInicio()));
        txtFechaFin.setText(sdf.format(reserva.getFechaFin()));
        
        //cargar Servicios Extras
        for (ServicioExtra sReservado : reserva.getServiciosContratados()) {
            for (JCheckBox chk : checkServicios) {
                int chkId = Integer.parseInt(chk.getActionCommand());
                if (chkId == sReservado.getServicioId()) {
                    chk.setSelected(true);
                    break;
                }
            }
        }
        
        btnGuardar.setText("Guardar Cambios (ID: " + idReservaEditando + ")");
        lblMensaje.setForeground(Color.BLUE);
        lblMensaje.setText("Modo Edición: Modifique los datos y guarde.");
    }

    private void cargarServiciosCheckboxes() {
        panelServicios.removeAll();
        checkServicios.clear();
        List<ServicioExtra> servicios = servicioExtraManager.obtenerTodosLosServicios();
        for (ServicioExtra s : servicios) {
            JCheckBox chk = new JCheckBox(s.toString());
            chk.setActionCommand(String.valueOf(s.getServicioId()));
            checkServicios.add(chk);
            panelServicios.add(chk);
        }
    }

    private void cargarListas() {
        Cliente clienteSel = (Cliente) comboClientes.getSelectedItem();
        Cabaña cabañaSel = (Cabaña) comboCabañas.getSelectedItem();
        
        comboClientes.removeAllItems();
        comboCabañas.removeAllItems();
        
        for (Cliente c : clienteManager.obtenerTodosLosClientes()) { comboClientes.addItem(c); }
        for (Cabaña c : cabañaManager.obtenerTodasLasCabañas()) { comboCabañas.addItem(c); }
        
        comboClientes.setSelectedItem(clienteSel);
        comboCabañas.setSelectedItem(cabañaSel);
        
        lblMensaje.setText("Listas de clientes y cabañas actualizadas.");
    }

    private void guardarReserva() {
        try {
            Cliente clienteSel = (Cliente) comboClientes.getSelectedItem();
            Cabaña cabañaSel = (Cabaña) comboCabañas.getSelectedItem();
            int cantidadPasajeros = (int) spinnerPasajeros.getValue();
            String fechaInicioStr = txtFechaInicio.getText();
            String fechaFinStr = txtFechaFin.getText();

            if (clienteSel == null || cabañaSel == null) { throw new Exception("Debe seleccionar un cliente y una cabaña."); }
            if (fechaInicioStr.isEmpty() || fechaFinStr.isEmpty()) { throw new Exception("Debe ingresar ambas fechas."); }
            
            Date fechaInicio = sdf.parse(fechaInicioStr);
            Date fechaFin = sdf.parse(fechaFinStr);

            if (fechaFin.before(fechaInicio)) { throw new Exception("La fecha de fin no puede ser anterior a la de inicio."); }
            if (fechaFin.equals(fechaInicio)) { throw new Exception("La reserva debe ser de al menos 1 día."); }
            
            List<ServicioExtra> serviciosSeleccionados = new ArrayList<>();
            for (JCheckBox chk : checkServicios) {
                if (chk.isSelected()) {
                    int servicioId = Integer.parseInt(chk.getActionCommand());
                    ServicioExtra s = servicioExtraManager.buscarServicioPorId(servicioId);
                    if (s != null) serviciosSeleccionados.add(s);
                }
            }

            if (idReservaEditando == 0) {
                Reserva nuevaReserva = new Reserva(clienteSel, cabañaSel, fechaInicio, fechaFin, cantidadPasajeros);
                for (ServicioExtra s : serviciosSeleccionados) {
                    nuevaReserva.agregarServicio(s);
                }
                reservaManager.crearReserva(nuevaReserva);
                limpiarFormulario();
               
                lblMensaje.setForeground(Color.BLUE);
                lblMensaje.setText("¡Reserva creada con éxito! ID: " + nuevaReserva.getReservaId());
            
            } else {
                reservaManager.modificarReserva(idReservaEditando, cabañaSel, fechaInicio, fechaFin, cantidadPasajeros, serviciosSeleccionados);
                lblMensaje.setForeground(Color.BLUE);
                lblMensaje.setText("¡Reserva (ID: " + idReservaEditando + ") modificada con éxito!");
            }

        

        } catch (Exception ex) {
            lblMensaje.setForeground(Color.RED);
            if(ex instanceof ParseException) {
                lblMensaje.setText("Error: Formato de fecha debe ser yyyy-MM-dd.");
            } else {
                lblMensaje.setText("Error: " + ex.getMessage());
            }
        }
    }
}