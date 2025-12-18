package com.demo.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.demo.models.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
}
