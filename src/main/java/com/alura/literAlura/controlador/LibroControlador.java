package com.alura.literAlura.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.alura.literAlura.modelo.Libro;
import com.alura.literAlura.repositorio.LibroRepositorio;

@RestController
@RequestMapping("/libros")
public class LibroControlador {

    @Autowired
    private LibroRepositorio libroRepositorio;

    @GetMapping
    public List<Libro> listarLibros() {
        return libroRepositorio.findAll();
    }

    @PostMapping
    public Libro crearLibro(@RequestBody Libro libro) {
        return libroRepositorio.save(libro);
    }

    @PutMapping("/{id}")
    public Libro actualizarLibro(@PathVariable Long id, @RequestBody Libro libroDetalles) {
        Libro libro = libroRepositorio.findById(id).orElseThrow();
        libro.setTitulo(libroDetalles.getTitulo());
        libro.setAutor(libroDetalles.getAutor());
        libro.setIdiomas(libroDetalles.getIdiomas());
        libro.setNumeroDescargas(libroDetalles.getNumeroDescargas());
        return libroRepositorio.save(libro);
    }

    @DeleteMapping("/{id}")
    public void eliminarLibro(@PathVariable Long id) {
        libroRepositorio.deleteById(id);
    }
}
