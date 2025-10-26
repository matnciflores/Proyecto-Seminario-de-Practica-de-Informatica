package view;

import manager.CabañaManager;
import manager.ClienteManager;
import manager.ReservaManager;
import model.Cliente;
import model.Reporte;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReportePanel extends JPanel {

    private ReservaManager reservaManager;
    private ClienteManager clienteManager;
    private CabañaManager cabañaManager;
    private Reporte reporteGenerator;
    private JComboBox<Cliente> comboClientesReporte;
    private JButton btnReporteOcupacion;
    private JButton btnReporteIngresos;
    private JButton btnReporteHistorial;
    private JTextArea areaReporte; 

    public ReportePanel(ReservaManager resManager, ClienteManager cliManager, CabañaManager cabManager) {
        this.reservaManager = resManager;
        this.clienteManager = cliManager;
        this.cabañaManager = cabManager;
    
        this.reporteGenerator = new Reporte(); 

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //panel superior
        //botones de accion
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBorder(BorderFactory.createTitledBorder("Seleccionar Reporte"));
        
        btnReporteOcupacion = new JButton("Ocupación Actual");
        btnReporteIngresos = new JButton("Ingresos Totales");
        btnReporteHistorial = new JButton("Historial por Cliente");
        comboClientesReporte = new JComboBox<>();
        comboClientesReporte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                actualizarComboClientes();
            }
        });

        
        //actualizar la lista de clientes al crear el panel
        actualizarComboClientes();

        panelBotones.add(btnReporteOcupacion);
        panelBotones.add(btnReporteIngresos);
        panelBotones.add(new JLabel("Cliente:"));
        panelBotones.add(comboClientesReporte);
        panelBotones.add(btnReporteHistorial);
        
        add(panelBotones, BorderLayout.NORTH);

        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(areaReporte);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resultado del Reporte"));
        
        add(scrollPane, BorderLayout.CENTER);

        btnReporteOcupacion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reporte = reporteGenerator.generarOcupacion(
                        cabañaManager.obtenerTodasLasCabañas()
                );
                areaReporte.setText(reporte);
            }
        });

        //reporte de ingresos
        btnReporteIngresos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Llama al método en model/Reporte.java
                String reporte = reporteGenerator.generarIngresos(
                        reservaManager.obtenerTodasLasReservas()
                );
                areaReporte.setText(reporte);
            }
        });

        //historial de cliente
        btnReporteHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cliente clienteSel = (Cliente) comboClientesReporte.getSelectedItem();
                if (clienteSel == null) {
                    areaReporte.setText("Error: Debe seleccionar un cliente.");
                    return;
                }
                
                String reporte = reporteGenerator.generarHistorialCliente(
                        clienteSel,
                        reservaManager.obtenerTodasLasReservas()
                );
                areaReporte.setText(reporte);
                
                actualizarComboClientes(); 
            }
        });
    }
    
    private void actualizarComboClientes() {
        comboClientesReporte.removeAllItems();
        for (Cliente c : clienteManager.obtenerTodosLosClientes()) {
            comboClientesReporte.addItem(c);
        }
    }
}