package com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.repository;

import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.entity.MedicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repositorio Spring Data: detalle técnico interno del adaptador de persistencia. */
public interface SpringDataMedicoRepository extends JpaRepository<MedicoEntity, Long> { }
