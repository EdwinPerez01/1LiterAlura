package com.alura.literAlura;

import com.alura.literAlura.modelo.Autor;
import com.alura.literAlura.modelo.Libro;
import com.alura.literAlura.servicio.GutendexService;
import com.alura.literAlura.servicio.LiterAluraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class LiterAluraApp implements CommandLineRunner {

    @Autowired
    private LiterAluraService literAluraService;

    @Autowired
    private GutendexService gutendexService;

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            mostrarMenu();

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine();  // Consumir la nueva línea

                switch (opcion) {
                    case 1:
                        buscarYGuardarLibroPorTitulo(scanner);
                        break;
                    case 2:
                        mostrarLibrosGuardados();
                        break;
                    case 3:
                        listarAutores(scanner);
                        break;
                    case 4:
                        buscarLibrosPorIdioma(scanner);
                        break;
                    case 5:
                        System.out.println("¡Adiós!");
                        scanner.close();
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, intenta de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingresa un número.");
                scanner.nextLine();  // Limpiar el buffer
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("Bienvenido al Catálogo LiterAlura");
        System.out.println("1. Buscar y guardar libro por título");
        System.out.println("2. Mostrar libros guardados");
        System.out.println("3. Listar autores registrados");
        System.out.println("4. Buscar libros por idioma");
        System.out.println("5. Salir");
    }

    private void buscarYGuardarLibroPorTitulo(Scanner scanner) {
        System.out.print("Ingrese el título del libro: ");
        String titulo = scanner.nextLine();
        Libro libro = gutendexService.buscarLibroPorTitulo(titulo);
        if (libro == null) {
            System.out.println("El libro no fue encontrado.");
        } else {
            System.out.println("Libro encontrado:");
            System.out.println("Título: " + libro.getTitulo());
            System.out.println("Autor: " + libro.getAutor().getNombre());
            System.out.println("Año de Nacimiento: " + libro.getAutor().getAnioNacimiento());
            System.out.println("Año de Fallecimiento: " + (libro.getAutor().getAnioFallecimiento() == 0 ? "Vivo" : libro.getAutor().getAnioFallecimiento()));
            System.out.println("Idiomas: " + libro.getIdiomas());
            System.out.println("Número de Descargas: " + libro.getNumeroDescargas());
            if (!literAluraService.existeLibro(libro)) {
                literAluraService.guardarLibros(List.of(libro));
                System.out.println("El libro ha sido guardado exitosamente en la base de datos.");
            } else {
                System.out.println("No puede insertar el mismo libro más de una vez.");
            }
        }
    }

    private void mostrarLibrosGuardados() {
        List<Libro> librosGuardados = literAluraService.buscarLibros();
        if (librosGuardados.isEmpty()) {
            System.out.println("No hay libros guardados.");
        } else {
            librosGuardados.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + libro.getAutor().getNombre());
                System.out.println("Año de Nacimiento: " + libro.getAutor().getAnioNacimiento());
                System.out.println("Año de Fallecimiento: " + (libro.getAutor().getAnioFallecimiento() == 0 ? "Vivo" : libro.getAutor().getAnioFallecimiento()));
                System.out.println("Idiomas: " + libro.getIdiomas());
                System.out.println("Número de Descargas: " + libro.getNumeroDescargas());
                System.out.println("----------------------------");
            });
        }
    }

    private void listarAutores(Scanner scanner) {
        System.out.print("Desea listar autores vivos en un año específico? (s/n): ");
        String respuesta = scanner.nextLine();
        if (respuesta.equalsIgnoreCase("s")) {
            listarAutoresVivosEnAnio(scanner);
        } else {
            listarTodosLosAutores();
        }
    }

    private void listarTodosLosAutores() {
        List<Libro> librosGuardados = literAluraService.buscarLibros();
        Map<String, List<Libro>> autoresLibrosMap = librosGuardados.stream()
                .collect(Collectors.groupingBy(libro -> libro.getAutor().getNombre()));

        autoresLibrosMap.forEach((autor, libros) -> {
            System.out.println("Autor: " + autor);
            if (!libros.isEmpty()) {
                Autor autorInfo = libros.get(0).getAutor();
                System.out.println("  Año de Nacimiento: " + autorInfo.getAnioNacimiento());
                System.out.println("  Año de Fallecimiento: " + (autorInfo.getAnioFallecimiento() == 0 ? "Vivo" : autorInfo.getAnioFallecimiento()));
            }
            libros.forEach(libro -> {
                System.out.println("  - Título: " + libro.getTitulo());
                System.out.println("    Idiomas: " + libro.getIdiomas());
                System.out.println("    Número de Descargas: " + libro.getNumeroDescargas());
            });
            System.out.println("----------------------------");
        });
    }

    private void listarAutoresVivosEnAnio(Scanner scanner) {
        System.out.print("Ingrese el año: ");
        int anio = scanner.nextInt();
        scanner.nextLine();  // Consumir la nueva línea

        List<Libro> librosGuardados = literAluraService.buscarLibros();
        Map<String, List<Libro>> autoresLibrosMap = librosGuardados.stream()
                .filter(libro -> libro.getAutor().getAnioNacimiento() <= anio &&
                        (libro.getAutor().getAnioFallecimiento() == 0 || libro.getAutor().getAnioFallecimiento() >= anio))
                .collect(Collectors.groupingBy(libro -> libro.getAutor().getNombre()));

        autoresLibrosMap.forEach((autor, libros) -> {
            System.out.println("Autor: " + autor);
            if (!libros.isEmpty()) {
                Autor autorInfo = libros.get(0).getAutor();
                System.out.println("  Año de Nacimiento: " + autorInfo.getAnioNacimiento());
                System.out.println("  Año de Fallecimiento: " + (autorInfo.getAnioFallecimiento() == 0 ? "Vivo" : autorInfo.getAnioFallecimiento()));
            }
            libros.forEach(libro -> {
                System.out.println("  - Título: " + libro.getTitulo());
                System.out.println("    Idiomas: " + libro.getIdiomas());
                System.out.println("    Número de Descargas: " + libro.getNumeroDescargas());
            });
            System.out.println("----------------------------");
        });
    }

    private void buscarLibrosPorIdioma(Scanner scanner) {
        System.out.print("Ingrese el idioma: ");
        String idioma = scanner.nextLine();
        List<Libro> librosPorIdioma = literAluraService.buscarLibrosPorIdioma(idioma);
        if (librosPorIdioma.isEmpty()) {
            System.out.println("No se encontraron libros en el idioma: " + idioma);
        } else {
            librosPorIdioma.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + libro.getAutor().getNombre());
                System.out.println("Idiomas: " + libro.getIdiomas());
                System.out.println("Número de Descargas: " + libro.getNumeroDescargas());
                System.out.println("----------------------------");
            });
        }
    }
}