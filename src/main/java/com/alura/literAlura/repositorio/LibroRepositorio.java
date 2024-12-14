package com.alura.literAlura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alura.literAlura.modelo.Libro;
import java.util.List;
import java.util.Optional;

public interface LibroRepositorio extends JpaRepository<Libro, Long> {
    List<Libro> findByAutorNombreContaining(String nombre);
    List<Libro> findByIdiomasContaining(String idiomas);
    Optional<Libro> findByTituloAndAutorNombre(String titulo, String autorNombre);
}
