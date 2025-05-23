package com.proyecto.gav.repository;

import com.proyecto.gav.model.Calificacion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepository extends MongoRepository<Calificacion, String> {

    List<Calificacion> findByCalificadorId(String calificadorId);

    List<Calificacion> findByCalificadoId(String calificadoId);

    List<Calificacion> findByTipoCalificacion(Calificacion.TipoCalificacion tipoCalificacion);

}
