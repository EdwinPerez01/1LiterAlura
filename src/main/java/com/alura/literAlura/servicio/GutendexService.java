package com.alura.literAlura.servicio;

import com.alura.literAlura.modelo.Autor;
import com.alura.literAlura.modelo.Libro;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class GutendexService {
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public List<Libro> obtenerLibros(String uri) {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri)).build();
        List<Libro> libros = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode rootNode = objectMapper.readTree(response.body());
                JsonNode resultsNode = rootNode.get("results");
                if (resultsNode.isArray()) {
                    for (JsonNode libroNode : resultsNode) {
                        Libro libro = new Libro();
                        libro.setTitulo(libroNode.get("title").asText());

                        // Configurar autor
                        JsonNode authorNode = libroNode.get("authors").get(0);
                        Autor autor = new Autor();
                        autor.setNombre(authorNode.get("name").asText());
                        autor.setAnioNacimiento(authorNode.has("birth_year") ? authorNode.get("birth_year").asInt() : 0);
                        autor.setAnioFallecimiento(authorNode.has("death_year") ? authorNode.get("death_year").asInt() : 0);
                        libro.setAutor(autor);

                        // Configurar idiomas
                        JsonNode idiomasNode = libroNode.get("languages");
                        if (idiomasNode.isArray() && idiomasNode.size() > 0) {
                            libro.setIdiomas(idiomasNode.get(0).asText());  // Primer idioma
                        }

                        libro.setNumeroDescargas(libroNode.get("download_count").asInt());
                        libros.add(libro);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return libros;
    }

    public Libro buscarLibroPorTitulo(String titulo) {
        String uri = "https://gutendex.com/books/?search=" + titulo.replace(" ", "%20");
        List<Libro> libros = obtenerLibros(uri);
        return libros.isEmpty() ? null : libros.get(0);  // Retornamos el primer resultado
    }
}
