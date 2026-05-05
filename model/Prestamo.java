package Biblioteca.model;

public class Prestamo {
    private final String isbn;
    private final String dni;
    private final String fecha;

    public Prestamo(String isbn, String dni, String fecha) {
        this.isbn = isbn;
        this.dni = dni;
        this.fecha = fecha;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getDni() {
        return dni;
    }

    public String getFecha() {
        return fecha;
    }
}
