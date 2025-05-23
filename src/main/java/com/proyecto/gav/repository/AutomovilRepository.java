package com.proyecto.gav.repository;

import com.proyecto.gav.model.Automovil;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutomovilRepository extends MongoRepository<Automovil, String> {

    boolean existsByPlaca(String placa);

    Automovil findByPlaca(String placa);

    boolean existsByPlacaAndIdNot(String placa, String id);

}