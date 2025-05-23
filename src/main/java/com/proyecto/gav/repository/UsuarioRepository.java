package com.proyecto.gav.repository;

import com.proyecto.gav.model.Automovil;
import com.proyecto.gav.model.Usuario;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Optional<Usuario> findById(String id);

    Optional<Usuario> findByIdAndRol(String id, Usuario.Rol rol);

    Optional<Usuario> findByLicencia(String licencia);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);

    Optional<Usuario> findByTelefono(String telefono);

    Optional<Usuario> findByAutomovil(Automovil automovil);

    boolean existsByAutomovil(Automovil automovil);

    boolean existsByNombreUsuario(String nombreUsuario);

    boolean existsByNumeroDocumento(String numeroDocumento);

    boolean existsByEmail(String email);

    boolean existsByTelefono(String telefono);

    List<Usuario> findByRol(Usuario.Rol rol);

    List<Usuario> findByRolAndDisponibilidadTrue(Usuario.Rol rol);


}
