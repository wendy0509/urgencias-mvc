package com.pascualbravo.urgencias;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Verifica en cada build la regla de dependencia de la arquitectura hexagonal:
 * las dependencias apuntan siempre hacia el dominio.
 */
@AnalyzeClasses(packages = "com.pascualbravo.urgencias", importOptions = ImportOption.DoNotIncludeTests.class)
class ArquitecturaHexagonalTest {

    @ArchTest
    static final ArchRule elDominioNoDependeDeNadaExterno = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..application..", "..infrastructure..", "org.springframework..", "jakarta..");

    @ArchTest
    static final ArchRule laAplicacionNoConoceLaInfraestructura = noClasses()
            .that().resideInAPackage("..application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..infrastructure..", "org.springframework..", "jakarta..");

    @ArchTest
    static final ArchRule losControladoresNoAccedenAPersistencia = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..adapter.out..");

    @ArchTest
    static final ArchRule losControladoresUsanPuertosNoServicios = noClasses()
            .that().resideInAPackage("..adapter.in..")
            .should().dependOnClassesThat().resideInAPackage("..application.service..");
}
