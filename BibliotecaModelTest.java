package Biblioteca;

import Biblioteca.model.BibliotecaModel;

public class BibliotecaModelTest {
    public static void main(String[] args) {
        BibliotecaModel model = new BibliotecaModel();

        run("DNI vacío", !BibliotecaModel.esDniValido(""));
        run("DNI menor a 8", !BibliotecaModel.esDniValido("1234567"));
        run("DNI mayor a 8", !BibliotecaModel.esDniValido("123456789"));
        run("DNI con letras", !BibliotecaModel.esDniValido("12A45678"));
        run("DNI válido", BibliotecaModel.esDniValido("12345678"));

        String error1 = model.prestarLibro("111", "1234567");
        run("Prestamo con DNI inválido", "El DNI debe tener 8 números.".equals(error1));

        String error2 = model.prestarLibro("111", "12345678");
        run("Préstamo válido", error2 != null && error2.startsWith("Préstamo realizado a DNI"));

        String error3 = model.devolverLibro("111");
        run("Devolución válida", "Libro devuelto correctamente.".equals(error3));

        System.out.println("Todos los tests pasaron.");
        System.exit(0);
    }

    private static void run(String nombre, boolean condicion) {
        if (!condicion) {
            System.err.println("FALLO: " + nombre);
            System.exit(1);
        }
    }
}
