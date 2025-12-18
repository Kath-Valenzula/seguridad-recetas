package com.demo.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.demo.demo.models.Valoracion;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
}
