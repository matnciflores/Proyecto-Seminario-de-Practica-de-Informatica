package view;

import manager.CabañaManager;
import model.Cabaña;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class CabañaPanel extends JPanel {

    private JTable tablaCabañas;
    private DefaultTableModel tableModel; 
    private CabañaManager cabañaManager;
    private JButton btnActualizar;

    public CabañaPanel(CabañaManager manager) {
        this.cabañaManager = manager;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnActualizar = new JButton("Actualizar Lista de Cabañas");
        panelBotones.add(btnActualizar);
        
        add(panelBotones, BorderLayout.NORTH);

        String[] columnas = {"ID", "Número/Nombre", "Capacidad Máx.", "Estado"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaCabañas = new JTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(tablaCabañas);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Cabañas Registradas"));
        
        add(scrollPane, BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> actualizarTabla());

        actualizarTabla();
    }

    private void actualizarTabla() {
        tableModel.setRowCount(0); 
        List<Cabaña> cabañas = cabañaManager.obtenerTodasLasCabañas();
        
        for (Cabaña c : cabañas) {
            Object[] fila = {
                c.getCabañaId(),
                c.getNumero(),
                c.getCapacidad(),
                c.getEstado().toString()
            };
            tableModel.addRow(fila);
        }
    }
}