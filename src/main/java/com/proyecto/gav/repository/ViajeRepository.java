package com.proyecto.gav.repository;

import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.model.Viaje;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ViajeRepository extends MongoRepository<Viaje, String> {


    List<Viaje> findByClienteId(String clienteId);

    List<Viaje> findByConductorId(String conductorId);

    List<Viaje> findByEstadoViaje(Viaje.EstadoViaje estadoViaje);

    List<Viaje> findByEstadoViajeIn(List<Viaje.EstadoViaje> estados);

    List<Viaje> findByAutomovilId(String automovilId);

    List<Viaje> findByFechaSolicitud(Date fechaSolicitud);

    List<Viaje> findByClienteIdAndEstadoViajeIn(String clienteId, List<Viaje.EstadoViaje> estados);

    List<Viaje> findByConductorIdAndEstadoViajeIn(String conductorId, List<Viaje.EstadoViaje> estados);




}
