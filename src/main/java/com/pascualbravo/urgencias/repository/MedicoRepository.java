package com.pascualbravo.urgencias.repository;

import com.pascualbravo.urgencias.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicoRepository extends JpaRepository<Medico, Long> { }