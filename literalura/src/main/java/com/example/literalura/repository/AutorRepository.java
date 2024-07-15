package com.example.literalura.repository;

import com.example.literalura.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    List<Autor> findByVivoTrueAndAnioNacimientoLessThanEqual(int anio);
    Autor findByNombre(String nombre);
}