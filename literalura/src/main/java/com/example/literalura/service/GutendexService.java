package com.example.literalura.service;

import com.example.literalura.entity.Autor;
import com.example.literalura.entity.Libro;
import com.example.literalura.repository.AutorRepository;
import com.example.literalura.repository.LibroRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GutendexService {

    private static final String GUTENDEX_API_URL = "https://gutendex.com/books/";

    private final RestClient restClient;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public GutendexService(RestClient.Builder restClientBuilder,
                           LibroRepository libroRepository,
                           AutorRepository autorRepository,
                           ObjectMapper objectMapper) {
        this.restClient = restClientBuilder.baseUrl(GUTENDEX_API_URL).build();
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
        this.objectMapper = objectMapper;
    }

    public void actualizarCatalogo() {
        String nextUrl = "";
        while (nextUrl != null) {
            String response = restClient.get()
                    .uri(nextUrl)
                    .retrieve()
                    .body(String.class);
            try {
                JsonNode root = objectMapper.readTree(response);
                JsonNode results = root.get("results");

                for (JsonNode bookNode : results) {
                    procesarLibro(bookNode);
                }

                nextUrl = root.get("next").asText("null");
                if (nextUrl.equals("null")) {
                    nextUrl = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void procesarLibro(JsonNode bookNode) {
        String titulo = bookNode.get("title").asText();
        int numeroDescargas = bookNode.get("download_count").asInt();

        JsonNode authorsNode = bookNode.get("authors");
        Autor autor = null;
        if (authorsNode.size() > 0) {
            JsonNode authorNode = authorsNode.get(0);
            String nombreAutor = authorNode.get("name").asText();
            boolean vivo = !authorNode.has("death_year");
            int anioNacimiento = authorNode.get("birth_year").asInt(0);

            autor = autorRepository.findByNombre(nombreAutor);
            if (autor == null) {
                autor = new Autor();
                autor.setNombre(nombreAutor);
                autor.setVivo(vivo);
                autor.setAnioNacimiento(anioNacimiento);
                autorRepository.save(autor);
            }
        }

        Libro libro = libroRepository.findByTitulo(titulo);
        if (libro == null) {
            libro = new Libro();
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setNumeroDescargas(numeroDescargas);

            JsonNode languagesNode = bookNode.get("languages");
            if (languagesNode.size() > 0) {
                libro.setIdioma(languagesNode.get(0).asText());
            }

            libroRepository.save(libro);
        } else {
            libro.setNumeroDescargas(numeroDescargas);
            libroRepository.save(libro);
        }
    }
}