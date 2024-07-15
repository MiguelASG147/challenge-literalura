package com.example.literalura.service;

import com.example.literalura.entity.Autor;
import com.example.literalura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorService {
    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> listarAutores() {
        return autorRepository.findAll();
    }

    public List<Autor> listarAutoresVivosPorAnio(int anio) {
        return autorRepository.findByVivoTrueAndAnioNacimientoLessThanEqual(anio);
    }
}