package Biblioteca;

import Biblioteca.controller.BibliotecaController;
import Biblioteca.views.Interfaz_Biblioteca;

import javax.swing.SwingUtilities;

public class Proyecto_Biblioteca {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BibliotecaController controller = new BibliotecaController();
            new Interfaz_Biblioteca(controller).setVisible(true);
        });
    }
}
