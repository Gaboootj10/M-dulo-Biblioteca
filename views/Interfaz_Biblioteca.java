package Biblioteca.views;

import Biblioteca.controller.BibliotecaController;
import Biblioteca.model.Libro;
import Biblioteca.model.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Interfaz_Biblioteca extends JFrame {

    private final BibliotecaController controller;
    private final DefaultTableModel modelo;
    private final JTable tabla;

    public Interfaz_Biblioteca(BibliotecaController controller) {
        this.controller = controller;
        setTitle("Sistema de Biblioteca");
        setSize(920, 540);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 248, 255));

        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(70, 130, 180));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel("📚 Biblioteca Digital");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Consulta, presta y guarda todo en JSON como una base de datos ligera.");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(230, 230, 250));

        panelTitulo.add(lblTitulo, BorderLayout.NORTH);
        panelTitulo.add(lblSubtitulo, BorderLayout.SOUTH);
        add(panelTitulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"Título", "ISBN", "Stock", "Último DNI"}, 0);
        tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tabla.getTableHeader().setBackground(new Color(100, 149, 237));
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.setSelectionBackground(new Color(176, 196, 222));

        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2), "Libros Disponibles"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 14));
        panelBotones.setBackground(new Color(240, 248, 255));

        JButton btnRegistrar = new JButton("📖 Registrar Libro");
        JButton btnPrestar = new JButton("🔄 Prestar Libro");
        JButton btnDevolver = new JButton("↩️ Devolver Libro");
        JButton btnConsultar = new JButton("🔎 Consultar disponibilidad");
        JButton btnPrestamos = new JButton("📋 Listar préstamos");

        Font btnFont = new Font("Arial", Font.BOLD, 14);
        btnRegistrar.setFont(btnFont);
        btnPrestar.setFont(btnFont);
        btnDevolver.setFont(btnFont);
        btnConsultar.setFont(btnFont);
        btnPrestamos.setFont(btnFont);

        btnRegistrar.setBackground(new Color(34, 139, 34));
        btnRegistrar.setForeground(Color.WHITE);
        btnPrestar.setBackground(new Color(255, 165, 0));
        btnPrestar.setForeground(Color.WHITE);
        btnDevolver.setBackground(new Color(220, 20, 60));
        btnDevolver.setForeground(Color.WHITE);
        btnConsultar.setBackground(new Color(65, 105, 225));
        btnConsultar.setForeground(Color.WHITE);
        btnPrestamos.setBackground(new Color(72, 209, 204));
        btnPrestamos.setForeground(Color.WHITE);

        panelBotones.add(btnRegistrar);
        panelBotones.add(btnPrestar);
        panelBotones.add(btnDevolver);
        panelBotones.add(btnConsultar);
        panelBotones.add(btnPrestamos);
        add(panelBotones, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(e -> abrirFormularioRegistro());
        btnPrestar.addActionListener(e -> prestar());
        btnDevolver.addActionListener(e -> devolver());
        btnConsultar.addActionListener(e -> consultarDisponibilidad());
        btnPrestamos.addActionListener(e -> mostrarListaPrestamos());

        actualizarTabla();
    }

    private void actualizarTabla() {
        modelo.setRowCount(0);
        for (Libro libro : controller.obtenerLibros()) {
            modelo.addRow(new Object[]{
                    libro.getTitulo(),
                    libro.getIsbn(),
                    libro.getStock(),
                    libro.getUltimoDNI().isEmpty() ? "-" : libro.getUltimoDNI()
            });
        }
    }

    private void abrirFormularioRegistro() {
        JDialog dialog = new JDialog(this, "Registrar Libro", true);
        dialog.setSize(380, 340);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(240, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField txtTitulo = new JTextField(20);
        JTextField txtISBN = new JTextField(20);
        JTextField txtStock = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Título:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtTitulo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtISBN, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1;
        dialog.add(txtStock, gbc);

        JButton btnGuardar = new JButton("💾 Guardar");
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        btnGuardar.setBackground(new Color(34, 139, 34));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        dialog.add(btnGuardar, gbc);

        btnGuardar.addActionListener(e -> {
            String titulo = txtTitulo.getText();
            String isbn = txtISBN.getText();
            String stockStr = txtStock.getText();
            int stock;
            try {
                stock = Integer.parseInt(stockStr);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Stock inválido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String error = controller.registrarLibro(titulo, isbn, stock);
            if (error != null) {
                JOptionPane.showMessageDialog(dialog, error, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            actualizarTabla();
            dialog.dispose();
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void prestar() {
        String isbn = JOptionPane.showInputDialog(this, "Ingrese ISBN del libro a prestar:");
        if (isbn == null) {
            return;
        }
        String dni = JOptionPane.showInputDialog(this, "Ingrese el DNI del lector:");
        if (dni == null) {
            return;
        }

        String resultado = controller.prestarLibro(isbn.trim(), dni.trim());
        JOptionPane.showMessageDialog(this, resultado, resultado.startsWith("Préstamo") ? "¡Éxito!" : "Error", resultado.startsWith("Préstamo") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (resultado.startsWith("Préstamo")) {
            actualizarTabla();
        }
    }

    private void devolver() {
        String isbn = JOptionPane.showInputDialog(this, "Ingrese ISBN del libro a devolver:");
        if (isbn == null) {
            return;
        }

        String resultado = controller.devolverLibro(isbn.trim());
        JOptionPane.showMessageDialog(this, resultado, resultado.startsWith("Libro devuelto") ? "¡Éxito!" : "Error", resultado.startsWith("Libro devuelto") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        if (resultado.startsWith("Libro devuelto")) {
            actualizarTabla();
        }
    }

    private void consultarDisponibilidad() {
        String isbn = JOptionPane.showInputDialog(this, "Ingrese ISBN a consultar:");
        if (isbn == null) {
            return;
        }

        String info = controller.consultarDisponibilidad(isbn.trim());
        if (info == null) {
            JOptionPane.showMessageDialog(this, "No se encontró el libro con ese ISBN.", "No encontrado", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, info, "Disponibilidad", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void mostrarListaPrestamos() {
        List<Prestamo> prestamos = controller.obtenerPrestamos();
        if (prestamos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay préstamos registrados todavía.", "Préstamos", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < prestamos.size(); i++) {
            Prestamo prestamo = prestamos.get(i);
            builder.append(i + 1)
                    .append(". ISBN: ")
                    .append(prestamo.getIsbn())
                    .append(" | DNI: ")
                    .append(prestamo.getDni())
                    .append(" | Fecha: ")
                    .append(prestamo.getFecha())
                    .append("\n");
        }

        JTextArea area = new JTextArea(builder.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scrollArea = new JScrollPane(area);
        scrollArea.setPreferredSize(new Dimension(620, 300));

        JOptionPane.showMessageDialog(this, scrollArea, "Préstamos registrados", JOptionPane.INFORMATION_MESSAGE);
    }
}
