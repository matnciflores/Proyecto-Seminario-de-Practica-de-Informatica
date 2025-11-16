package view;

import manager.CabañaManager;
import manager.ClienteManager;
import manager.ReservaManager;
import model.Cliente;
import model.Reporte;
import model.Reserva;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException; 
import java.text.SimpleDateFormat; 
import java.util.Date; 
import java.util.List;

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
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JButton btnGenerarReporteIngresos;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public ReportePanel(ReservaManager resManager, ClienteManager cliManager, CabañaManager cabManager) {
        this.reservaManager = resManager;
        this.clienteManager = cliManager;
        this.cabañaManager = cabManager;
        this.reporteGenerator = new Reporte(); 
        sdf.setLenient(false);
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //panel superior
        //botones de accion
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 10, 10));
        panelBotones.setBorder(BorderFactory.createTitledBorder("Seleccionar Reporte"));
        
        //reporte de ocupación
        JPanel panelOcupacion = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnReporteOcupacion = new JButton("Ocupación Actual");
        panelOcupacion.add(btnReporteOcupacion);
        
        //reporte de ingresos
        JPanel panelIngresos = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelIngresos.add(new JLabel("Desde (dd-MM-yyyy):"));
        txtFechaDesde = new JTextField(10);
        panelIngresos.add(txtFechaDesde);
        panelIngresos.add(new JLabel("Hasta (dd-MM-yyyy):"));
        txtFechaHasta = new JTextField(10);
        panelIngresos.add(txtFechaHasta);
        btnGenerarReporteIngresos = new JButton("Ingresos Totales");
        panelIngresos.add(btnGenerarReporteIngresos);
        
        //historial de clientes
        JPanel panelHistorial = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHistorial.add(new JLabel("Cliente:"));
        comboClientesReporte = new JComboBox<>();
        refrescarComboClientes(); 
        panelHistorial.add(comboClientesReporte);
        btnReporteHistorial = new JButton("Historial por Cliente");
        panelHistorial.add(btnReporteHistorial);
        
        //añadir filas al panel principal de botones
        panelBotones.add(panelOcupacion);
        panelBotones.add(panelIngresos);
        panelBotones.add(panelHistorial);
        
        add(panelBotones, BorderLayout.NORTH);

        //area de Texto del Reporte
        areaReporte = new JTextArea();
        areaReporte.setEditable(false);
        areaReporte.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(areaReporte);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Resultado del Reporte"));
        
        add(scrollPane, BorderLayout.CENTER);
        
        //reporte de ocupación
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
        btnGenerarReporteIngresos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //validar las fechas
                    String fechaDesdeStr = txtFechaDesde.getText();
                    String fechaHastaStr = txtFechaHasta.getText();
                    if (fechaDesdeStr.isEmpty() || fechaHastaStr.isEmpty()) {
                        throw new Exception("Debe ingresar ambas fechas.");
                    }
                    
                    Date fechaDesde = sdf.parse(fechaDesdeStr);
                    Date fechaHasta = sdf.parse(fechaHastaStr);
                    
                    if (fechaHasta.before(fechaDesde)) {
                        throw new Exception("La fecha 'Hasta' no puede ser anterior a la fecha 'Desde'.");
                    }
                    
                    //llamar al Manager para que busque en la base de datos
                    List<Reserva> reservas = reservaManager.obtenerReservasFinalizadasPorFecha(fechaDesde, fechaHasta);
                    
                    //llamar al generador de reporte
                    String reporte = reporteGenerator.generarIngresosDetallado(reservas, fechaDesde, fechaHasta);
                    areaReporte.setText(reporte);
                    
                } catch (ParseException pe) {
                    areaReporte.setText("Error: El formato de fecha debe ser dd-MM-yyyy.");
                } catch (Exception ex) {
                    areaReporte.setText("Error: " + ex.getMessage());
                }
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
            }
        });
    }

    private void refrescarComboClientes() {
        Cliente clienteSel = (Cliente) comboClientesReporte.getSelectedItem();
        
        comboClientesReporte.removeAllItems();
        List<Cliente> clientes = clienteManager.obtenerTodosLosClientes();
        for (Cliente c : clientes) {
            comboClientesReporte.addItem(c);
        }
        
        if (clienteSel != null) {
            comboClientesReporte.setSelectedItem(clienteSel);
        }
    }
}