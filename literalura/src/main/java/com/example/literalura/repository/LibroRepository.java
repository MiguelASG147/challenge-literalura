package com.example.literalura.repository;

import com.example.literalura.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Libro findByTituloContainingIgnoreCase(String titulo);
    Libro findByTitulo(String titulo);
    List<Libro> findByIdioma(String idioma);
}
