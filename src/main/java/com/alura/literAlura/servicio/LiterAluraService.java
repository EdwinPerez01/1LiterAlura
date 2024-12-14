package com.alura.literAlura.servicio;

import com.alura.literAlura.modelo.Autor;
import com.alura.literAlura.modelo.Libro;
import com.alura.literAlura.repositorio.AutorRepositorio;
import com.alura.literAlura.repositorio.LibroRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class LiterAluraService {

    @Autowired
    private LibroRepositorio libroRepositorio;

    @Autowired
    private AutorRepositorio autorRepositorio;

    @Transactional
    public void guardarLibros(List<Libro> libros) {
        for (Libro libro : libros) {
            // Verificar si el libro ya existe
            Optional<Libro> libroExistente = libroRepositorio.findByTituloAndAutorNombre(libro.getTitulo(), libro.getAutor().getNombre());
            if (libroExistente.isEmpty()) {
                // Guardar el autor si no existe
                Autor autor = libro.getAutor();
                List<Autor> autoresExistentes = autorRepositorio.findByNombre(autor.getNombre());
                if (autoresExistentes.isEmpty()) {
                    autorRepositorio.save(autor);
                    libro.setAutor(autor);
                } else if (autoresExistentes.size() == 1) {
                    libro.setAutor(autoresExistentes.get(0));
                } else {
                    for (Autor autorExistente : autoresExistentes) {
                        if (autorExistente.getAnioNacimiento() == autor.getAnioNacimiento() &&
                                autorExistente.getAnioFallecimiento() == autor.getAnioFallecimiento()) {
                            libro.setAutor(autorExistente);
                            break;
                        }
                    }
                    if (libro.getAutor() == null) {
                        autorRepositorio.save(autor);
                        libro.setAutor(autor);
                    }
                }
                libroRepositorio.save(libro);
            } else {
                System.out.println("El libro '" + libro.getTitulo() + "' de " + libro.getAutor().getNombre() + " ya está en la base de datos.");
            }
        }
    }

    public boolean existeLibro(Libro libro) {
        Optional<Libro> libroExistente = libroRepositorio.findByTituloAndAutorNombre(libro.getTitulo(), libro.getAutor().getNombre());
        return libroExistente.isPresent();
    }

    public List<Libro> buscarLibros() {
        return libroRepositorio.findAll();
    }

    public List<Libro> buscarLibrosPorAutor(String nombre) {
        return libroRepositorio.findByAutorNombreContaining(nombre);
    }

    public List<Libro> buscarLibrosPorIdioma(String idioma) {
        return libroRepositorio.findByIdiomasContaining(idioma);
    }
}
