package Biblioteca.controller;

import Biblioteca.model.BibliotecaModel;
import Biblioteca.model.Libro;
import Biblioteca.model.Prestamo;

import java.util.List;

public class BibliotecaController {
    private final BibliotecaModel model;

    public BibliotecaController() {
        model = new BibliotecaModel();
    }

    public List<Libro> obtenerLibros() {
        return model.obtenerLibros();
    }

    public List<Prestamo> obtenerPrestamos() {
        return model.obtenerPrestamos();
    }

    public String registrarLibro(String titulo, String isbn, int stock) {
        return model.registrarLibro(titulo, isbn, stock);
    }

    public String prestarLibro(String isbn, String dni) {
        return model.prestarLibro(isbn, dni);
    }

    public String devolverLibro(String isbn) {
        return model.devolverLibro(isbn);
    }

    public String consultarDisponibilidad(String isbn) {
        return model.consultarDisponibilidad(isbn);
    }
}
