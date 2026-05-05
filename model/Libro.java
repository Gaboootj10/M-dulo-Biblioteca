package Biblioteca.model;

public class Libro {
    private String titulo;
    private String isbn;
    private int stock;
    private String ultimoDNI;

    public Libro(String titulo, String isbn, int stock) {
        this.titulo = titulo;
        this.isbn = isbn;
        this.stock = stock;
        this.ultimoDNI = "";
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getUltimoDNI() {
        return ultimoDNI;
    }

    public void setUltimoDNI(String ultimoDNI) {
        this.ultimoDNI = ultimoDNI;
    }
}
