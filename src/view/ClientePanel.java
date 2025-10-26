package view;

import manager.ClienteManager;
import model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class ClientePanel extends JPanel {

    private JTextField txtNombre, txtApellido, txtDni, txtTelefono, txtEmail;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JLabel lblMensaje;
    
    private JTable tablaClientes;
    private DefaultTableModel tableModel;
    private JButton btnCargar; 
    private JButton btnActualizar;

    private ClienteManager clienteManager;
    
    //Guarda el ID del cliente a editar
    //si es 0 significa que estamos creando uno
    private int idClienteEditando = 0;

    public ClientePanel(ClienteManager manager) {
        this.clienteManager = manager;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //panel izquierdo
        JPanel panelFormulario = new JPanel(new BorderLayout(10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        
        JPanel panelCampos = new JPanel(new GridLayout(5, 2, 5, 5));
        txtNombre = new JTextField(20);
        txtApellido = new JTextField(20);
        txtDni = new JTextField(20);
        txtTelefono = new JTextField(20);
        txtEmail = new JTextField(20);
        
        panelCampos.add(new JLabel("Nombre:"));
        panelCampos.add(txtNombre);
        panelCampos.add(new JLabel("Apellido:"));
        panelCampos.add(txtApellido);
        panelCampos.add(new JLabel("DNI:"));
        panelCampos.add(txtDni);
        panelCampos.add(new JLabel("Teléfono:"));
        panelCampos.add(txtTelefono);
        panelCampos.add(new JLabel("Email:"));
        panelCampos.add(txtEmail);
        
        JPanel panelBotonesForm = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar Cliente");
        btnNuevo = new JButton("Limpiar");
        panelBotonesForm.add(btnNuevo);
        panelBotonesForm.add(btnGuardar);
        
        lblMensaje = new JLabel("Complete los datos para un nuevo cliente.");
        
        panelFormulario.add(panelCampos, BorderLayout.CENTER);
        panelFormulario.add(panelBotonesForm, BorderLayout.SOUTH);
        panelFormulario.add(lblMensaje, BorderLayout.NORTH);

        //tabla de clientes
        JPanel panelTabla = new JPanel(new BorderLayout(10, 10));
        panelTabla.setBorder(BorderFactory.createTitledBorder("Clientes Registrados"));

        String[] columnas = {"ID", "Nombre", "Apellido", "DNI", "Teléfono"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaClientes = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        
        JPanel panelBotonesTabla = new JPanel(new FlowLayout());
        btnCargar = new JButton("Cargar Cliente para Editar");
        btnActualizar = new JButton("Actualizar Lista");
        panelBotonesTabla.add(btnActualizar);
        panelBotonesTabla.add(btnCargar);
        
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        panelTabla.add(panelBotonesTabla, BorderLayout.SOUTH);

        add(panelFormulario, BorderLayout.WEST);
        add(panelTabla, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> guardarCliente());
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnActualizar.addActionListener(e -> actualizarTabla());
        btnCargar.addActionListener(e -> cargarClienteSeleccionado());
        
        actualizarTabla();
    }


    private void limpiarFormulario() {
        idClienteEditando = 0;
        txtNombre.setText("");
        txtApellido.setText("");
        txtDni.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        btnGuardar.setText("Guardar (Nuevo)");
        lblMensaje.setForeground(Color.BLACK);
        lblMensaje.setText("Complete los datos para un nuevo cliente.");
    }
    
    private void actualizarTabla() {
        tableModel.setRowCount(0);
        List<Cliente> clientes = clienteManager.obtenerTodosLosClientes();
        for (Cliente c : clientes) {
            Object[] fila = {
                c.getClienteId(),
                c.getNombre(),
                c.getApellido(),
                c.getDni(),
                c.getTelefono()
            };
            tableModel.addRow(fila);
        }
    }
    

    private void cargarClienteSeleccionado() {
        int filaSel = tablaClientes.getSelectedRow();
        if (filaSel == -1) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText("Error: Debe seleccionar un cliente de la tabla.");
            return;
        }
        
        int idCliente = (int) tableModel.getValueAt(filaSel, 0);
        Cliente c = clienteManager.buscarClientePorId(idCliente);
        
        if (c != null) {
            //cargar datos en el formulario
            txtNombre.setText(c.getNombre());
            txtApellido.setText(c.getApellido());
            txtDni.setText(c.getDni());
            txtTelefono.setText(c.getTelefono());
            txtEmail.setText(c.getEmail());
            
            //marcar la edición
            idClienteEditando = c.getClienteId();
            
            btnGuardar.setText("Guardar Cambios (ID: " + idCliente + ")");
            lblMensaje.setForeground(Color.BLUE);
            lblMensaje.setText("Editando cliente. Haga clic en 'Guardar Cambios'.");
        }
    }

    //botón guardar
    private void guardarCliente() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String dni = txtDni.getText();
        String telefono = txtTelefono.getText();
        String email = txtEmail.getText();

        try {
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                throw new Exception("Nombre, Apellido y DNI son obligatorios.");
            }

            if (idClienteEditando == 0) {
                Cliente nuevoCliente = new Cliente(nombre, apellido, dni, telefono, email);
                clienteManager.agregarCliente(nuevoCliente);
                lblMensaje.setForeground(Color.BLUE);
                lblMensaje.setText("¡Cliente nuevo guardado con éxito!");
            } else {
                clienteManager.modificarCliente(idClienteEditando, nombre, apellido, dni, telefono, email);
                lblMensaje.setForeground(Color.BLUE);
                lblMensaje.setText("¡Cliente (ID: " + idClienteEditando + ") modificado con éxito!");
            }
            limpiarFormulario();
            actualizarTabla();

        } catch (Exception ex) {
            lblMensaje.setForeground(Color.RED);
            lblMensaje.setText(ex.getMessage());
        }
    }
}