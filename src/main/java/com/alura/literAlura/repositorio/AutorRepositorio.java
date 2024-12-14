package com.alura.literAlura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.alura.literAlura.modelo.Autor;
import java.util.List;

public interface AutorRepositorio extends JpaRepository<Autor, Long> {
    List<Autor> findByNombre(String nombre);
}
