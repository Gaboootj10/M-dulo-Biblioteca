package Biblioteca.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaModel {
    private static final String DB_FILE = "biblioteca.json";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final List<Libro> libros = new ArrayList<>();
    private final List<Prestamo> prestamos = new ArrayList<>();

    public BibliotecaModel() {
        cargarBaseDatos();
    }

    public List<Libro> obtenerLibros() {
        return libros;
    }

    public List<Prestamo> obtenerPrestamos() {
        return prestamos;
    }

    public String registrarLibro(String titulo, String isbn, int stock) {
        if (titulo == null || titulo.isEmpty() || isbn == null || isbn.isEmpty()) {
            return "Debe ingresar título e ISBN.";
        }
        if (stock < 0) {
            return "El stock debe ser un número mayor o igual a cero.";
        }
        if (buscarLibro(isbn) != null) {
            return "ISBN duplicado.";
        }

        libros.add(new Libro(titulo, isbn, stock));
        guardarBaseDatos();
        return null;
    }

    public String prestarLibro(String isbn, String dni) {
        Libro libro = buscarLibro(isbn);
        if (libro == null) {
            return "No se encontró el libro con ese ISBN.";
        }
        if (libro.getStock() <= 0) {
            return "No hay stock disponible para ese libro.";
        }
        if (dni == null || dni.trim().isEmpty()) {
            return "Debe ingresar un DNI válido.";
        }

        libro.setStock(libro.getStock() - 1);
        libro.setUltimoDNI(dni.trim());
        prestamos.add(new Prestamo(isbn, dni.trim(), LocalDateTime.now().format(FORMATO_FECHA)));
        guardarBaseDatos();
        return "Préstamo realizado a DNI " + dni.trim() + ".";
    }

    public String devolverLibro(String isbn) {
        Libro libro = buscarLibro(isbn);
        if (libro == null) {
            return "No se encontró el libro con ese ISBN.";
        }
        libro.setStock(libro.getStock() + 1);
        guardarBaseDatos();
        return "Libro devuelto correctamente.";
    }

    public String consultarDisponibilidad(String isbn) {
        Libro libro = buscarLibro(isbn);
        if (libro == null) {
            return null;
        }
        return "Título: " + libro.getTitulo() + "\n" +
                "ISBN: " + libro.getIsbn() + "\n" +
                "Stock disponible: " + libro.getStock() + "\n" +
                "Último DNI registrado: " + (libro.getUltimoDNI().isEmpty() ? "Ninguno" : libro.getUltimoDNI());
    }

    private Libro buscarLibro(String isbn) {
        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }

    private void inicializarDatos() {
        libros.clear();
        prestamos.clear();
        libros.add(new Libro("Java", "111", 3));
        libros.add(new Libro("Redes", "222", 0));
        libros.add(new Libro("BD", "333", 2));
    }

    private void cargarBaseDatos() {
        File file = new File(DB_FILE);
        if (!file.exists()) {
            inicializarDatos();
            guardarBaseDatos();
            return;
        }

        try {
            String contenido = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            String librosArray = extraerArray(contenido, "libros");
            String prestamosArray = extraerArray(contenido, "prestamos");
            parseLibros(librosArray);
            parsePrestamos(prestamosArray);
        } catch (Exception e) {
            inicializarDatos();
            guardarBaseDatos();
        }
    }

    public void guardarBaseDatos() {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DB_FILE), StandardCharsets.UTF_8))) {
            writer.write("{\n");
            writer.write("  \"libros\": [\n");
            for (int i = 0; i < libros.size(); i++) {
                Libro libro = libros.get(i);
                writer.write("    {\"titulo\":\"" + escapeJson(libro.getTitulo()) + "\", \"isbn\":\"" + escapeJson(libro.getIsbn()) + "\", \"stock\":" + libro.getStock() + ", \"ultimoDNI\":\"" + escapeJson(libro.getUltimoDNI()) + "\"}");
                if (i < libros.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }
            writer.write("  ],\n");
            writer.write("  \"prestamos\": [\n");
            for (int i = 0; i < prestamos.size(); i++) {
                Prestamo prestamo = prestamos.get(i);
                writer.write("    {\"isbn\":\"" + escapeJson(prestamo.getIsbn()) + "\", \"dni\":\"" + escapeJson(prestamo.getDni()) + "\", \"fecha\":\"" + escapeJson(prestamo.getFecha()) + "\"}");
                if (i < prestamos.size() - 1) {
                    writer.write(",\n");
                } else {
                    writer.write("\n");
                }
            }
            writer.write("  ]\n}");
        } catch (Exception e) {
            System.out.println("Error guardando JSON: " + e.getMessage());
        }
    }

    private String extraerArray(String json, String llave) {
        int index = json.indexOf('"' + llave + '"');
        if (index < 0) {
            return "";
        }
        index = json.indexOf('[', index);
        if (index < 0) {
            return "";
        }
        int depth = 0;
        for (int i = index; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') {
                depth++;
            } else if (c == ']') {
                depth--;
                if (depth == 0) {
                    return json.substring(index + 1, i);
                }
            }
        }
        return "";
    }

    private void parseLibros(String arrayText) {
        libros.clear();
        if (arrayText.trim().isEmpty()) {
            return;
        }
        String[] items = arrayText.split("\\},\\s*\\{");
        for (String item : items) {
            String objeto = item.trim();
            if (!objeto.startsWith("{")) {
                objeto = "{" + objeto;
            }
            if (!objeto.endsWith("}")) {
                objeto = objeto + "}";
            }
            String titulo = getJsonString(objeto, "titulo");
            String isbn = getJsonString(objeto, "isbn");
            int stock = getJsonInt(objeto, "stock");
            String ultimoDNI = getJsonString(objeto, "ultimoDNI");
            if (!titulo.isEmpty() && !isbn.isEmpty()) {
                Libro libro = new Libro(titulo, isbn, stock);
                libro.setUltimoDNI(ultimoDNI);
                libros.add(libro);
            }
        }
    }

    private void parsePrestamos(String arrayText) {
        prestamos.clear();
        if (arrayText.trim().isEmpty()) {
            return;
        }
        String[] items = arrayText.split("\\},\\s*\\{");
        for (String item : items) {
            String objeto = item.trim();
            if (!objeto.startsWith("{")) {
                objeto = "{" + objeto;
            }
            if (!objeto.endsWith("}")) {
                objeto = objeto + "}";
            }
            String isbn = getJsonString(objeto, "isbn");
            String dni = getJsonString(objeto, "dni");
            String fecha = getJsonString(objeto, "fecha");
            if (!isbn.isEmpty() && !dni.isEmpty()) {
                prestamos.add(new Prestamo(isbn, dni, fecha));
            }
        }
    }

    private String getJsonString(String texto, String campo) {
        String etiqueta = '"' + campo + '"' + ":\"";
        int inicio = texto.indexOf(etiqueta);
        if (inicio < 0) {
            return "";
        }
        inicio += etiqueta.length();
        int fin = texto.indexOf('"', inicio);
        if (fin < 0) {
            return "";
        }
        return unescapeJson(texto.substring(inicio, fin));
    }

    private int getJsonInt(String texto, String campo) {
        String etiqueta = '"' + campo + '"' + ":";
        int inicio = texto.indexOf(etiqueta);
        if (inicio < 0) {
            return 0;
        }
        inicio += etiqueta.length();
        int fin = inicio;
        while (fin < texto.length() && Character.isDigit(texto.charAt(fin))) {
            fin++;
        }
        try {
            return Integer.parseInt(texto.substring(inicio, fin));
        } catch (Exception e) {
            return 0;
        }
    }

    private String unescapeJson(String valor) {
        return valor.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private String escapeJson(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
