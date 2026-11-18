package com.pascualbravo.urgencias.infrastructure.config;

import com.pascualbravo.urgencias.application.service.MedicoService;
import com.pascualbravo.urgencias.application.service.PacienteService;
import com.pascualbravo.urgencias.domain.port.in.GestionarMedicoUseCase;
import com.pascualbravo.urgencias.domain.port.in.GestionarPacienteUseCase;
import com.pascualbravo.urgencias.domain.port.out.MedicoRepositoryPort;
import com.pascualbravo.urgencias.domain.port.out.PacienteRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Ensambla el hexágono: conecta los servicios de aplicación (núcleo, sin Spring)
 * con las implementaciones concretas de los puertos de salida (adaptadores).
 * Aquí se materializa la inversión de dependencias.
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public Clock reloj() {
        return Clock.systemDefaultZone();
    }

    @Bean
    public GestionarMedicoUseCase gestionarMedicoUseCase(MedicoRepositoryPort medicos,
                                                         PacienteRepositoryPort pacientes) {
        return new MedicoService(medicos, pacientes);
    }

    @Bean
    public GestionarPacienteUseCase gestionarPacienteUseCase(PacienteRepositoryPort pacientes,
                                                             MedicoRepositoryPort medicos,
                                                             Clock reloj) {
        return new PacienteService(pacientes, medicos, reloj);
    }
}
