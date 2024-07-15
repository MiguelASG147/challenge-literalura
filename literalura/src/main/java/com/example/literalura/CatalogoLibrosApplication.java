package com.example.literalura;

import com.example.literalura.entity.Autor;
import com.example.literalura.entity.Libro;
import com.example.literalura.service.AutorService;
import com.example.literalura.service.GutendexService;
import com.example.literalura.service.LibroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class CatalogoLibrosApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CatalogoLibrosApplication.class, args);

        LibroService libroService = context.getBean(LibroService.class);
        AutorService autorService = context.getBean(AutorService.class);
        GutendexService gutendexService = context.getBean(GutendexService.class);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Buscar libro por título");
            System.out.println("2. Listar libros registrados");
            System.out.println("3. Listar autores registrados");
            System.out.println("4. Listar autores vivos en un determinado año");
            System.out.println("5. Listar libros por idioma");
            System.out.println("6. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea

            switch (opcion) {
                case 1:
                    System.out.print("Ingrese el título del libro: ");
                    String titulo = scanner.nextLine();
                    Libro libro = libroService.buscarPorTitulo(titulo);
                    if (libro != null) {
                        System.out.println("Título: " + libro.getTitulo());
                        System.out.println("Autor: " + libro.getAutor().getNombre());
                        System.out.println("Idioma: " + libro.getIdioma());
                        System.out.println("Número de descargas: " + libro.getNumeroDescargas());
                    } else {
                        System.out.println("Libro no encontrado");
                    }
                    break;
                case 2:
                    List<Libro> libros = libroService.listarLibros();
                    for (Libro l : libros) {
                        System.out.println(l.getTitulo() + " - " + l.getAutor().getNombre());
                    }
                    break;
                case 3:
                    List<Autor> autores = autorService.listarAutores();
                    for (Autor a : autores) {
                        System.out.println(a.getNombre());
                    }
                    break;
                case 4:
                    System.out.print("Ingrese el año: ");
                    int anio = scanner.nextInt();
                    List<Autor> autoresVivos = autorService.listarAutoresVivosPorAnio(anio);
                    for (Autor a : autoresVivos) {
                        System.out.println(a.getNombre() + " (Nacido en " + a.getAnioNacimiento() + ")");
                    }
                    break;
                case 5:
                    String[] idiomas = {"es", "en", "fr", "pt"};
                    for (String idioma : idiomas) {
                        List<Libro> librosPorIdioma = libroService.listarLibrosPorIdioma(idioma);
                        System.out.println("Libros en " + idioma + ":");
                        for (Libro l : librosPorIdioma) {
                            System.out.println("- " + l.getTitulo());
                        }
                    }
                    break;
                case 6:
                    System.out.println("Actualizando catálogo...");
                    gutendexService.actualizarCatalogo();
                    System.out.println("Catálogo actualizado.");
                    break;
                case 7:
                    System.out.println("Saliendo de la aplicación...");
                    context.close();
                    return;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}