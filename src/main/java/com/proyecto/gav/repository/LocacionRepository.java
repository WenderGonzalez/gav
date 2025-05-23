package com.proyecto.gav.repository;

import com.proyecto.gav.entity.Locacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocacionRepository extends JpaRepository<Locacion, Long> {

    Optional<Locacion> findById(Long id);

    Optional<Locacion> findByNombreLugar(String nombre);

    boolean existsByLatitudAndLongitud(double latitud, double longitud);

    boolean existsByNombreLugar(String nombreLugar);

    List<Locacion> findAllByNombreLugarNot(String nombreLugar);

}
