package view;

import manager.CabañaManager;
import manager.ClienteManager;
import manager.ReservaManager; 
import manager.ServicioExtraManager;
import manager.EstadiaManager;
import manager.PagoManager;
import model.Reserva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainView extends JFrame {

    private JPanel panelContenedor;

    private ClienteManager clienteManager;
    private CabañaManager cabañaManager;
    private ServicioExtraManager servicioExtraManager;
    private PagoManager pagoManager;
    private EstadiaManager estadiaManager;
    private ReservaManager reservaManager;
    private ReservaPanel panelReservas;

    public MainView() {
        this.clienteManager = new ClienteManager();
        this.cabañaManager = new CabañaManager();
        this.servicioExtraManager = new ServicioExtraManager();
        this.pagoManager = new PagoManager();
        this.estadiaManager = new EstadiaManager(cabañaManager, pagoManager);
        this.reservaManager = new ReservaManager(clienteManager, cabañaManager, servicioExtraManager,estadiaManager, pagoManager);

        setTitle("Sistema de Gestión LikeHome");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        //MENÚ DE SELECCIÓN
        JMenuBar menuBar = new JMenuBar();

        JMenu menuGestion = new JMenu("Gestión");
        JMenuItem itemClientes = new JMenuItem("Gestionar Clientes");
        JMenuItem itemCabañas = new JMenuItem("Ver Cabañas");
        menuGestion.add(itemClientes);
        menuGestion.add(itemCabañas);

        JMenu menuReservas = new JMenu("Reservas");
        JMenuItem itemNuevaReserva = new JMenuItem("Nueva Reserva");
        JMenuItem itemEstadias = new JMenuItem("Gestionar Reservas");
        menuReservas.add(itemNuevaReserva);
        menuReservas.add(itemEstadias);

        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemVerReportes = new JMenuItem("Ver Reportes");
        menuReportes.add(itemVerReportes);

        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem itemSalir = new JMenuItem("Salir");
        menuSistema.add(itemSalir);

        menuBar.add(menuGestion);
        menuBar.add(menuReservas);
        menuBar.add(menuReportes);
        menuBar.add(menuSistema);

        setJMenuBar(menuBar);

        //panel contenedor
        panelContenedor = new JPanel(new CardLayout());
        add(panelContenedor, BorderLayout.CENTER);

        //formularios
        ClientePanel panelClientes = new ClientePanel(clienteManager);
        CabañaPanel panelCabañas = new CabañaPanel(cabañaManager);

        this.panelReservas = new ReservaPanel(
                reservaManager, clienteManager, cabañaManager, servicioExtraManager
        );

        EstadiaPanel panelEstadias = new EstadiaPanel(reservaManager, estadiaManager, this);

        ReportePanel panelReportes = new ReportePanel(reservaManager, clienteManager, cabañaManager);

        panelContenedor.add(panelClientes, "Clientes");
        panelContenedor.add(panelCabañas, "Cabañas");
        panelContenedor.add(this.panelReservas, "Reservas");
        panelContenedor.add(panelEstadias, "Estadias");
        panelContenedor.add(panelReportes, "Reportes");

        //botones del menu
        itemClientes.addActionListener(e -> mostrarPanel("Clientes"));
        itemCabañas.addActionListener(e -> mostrarPanel("Cabañas"));
        itemNuevaReserva.addActionListener(e -> {

            if (this.panelReservas != null) {
                this.panelReservas.limpiarFormulario();
            }
            mostrarPanel("Reservas");
        });
        itemEstadias.addActionListener(e -> mostrarPanel("Estadias"));
        itemVerReportes.addActionListener(e -> mostrarPanel("Reportes"));
        itemSalir.addActionListener(e -> System.exit(0));

        mostrarPanel("Clientes"); //panel inicial
    }

    private void mostrarPanel(String nombrePanel) {
        CardLayout cl = (CardLayout)(panelContenedor.getLayout());
        cl.show(panelContenedor, nombrePanel);
    }

  
    public void irAEdicionReserva(Reserva reserva) {
        if (this.panelReservas != null) {
            this.panelReservas.cargarReservaParaModificar(reserva);
            this.mostrarPanel("Reservas");
        }
    }

    //main
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainView ventana = new MainView();
                ventana.setVisible(true);
            }
        });
    }
}