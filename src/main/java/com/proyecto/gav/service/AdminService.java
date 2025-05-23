package com.proyecto.gav.service;

import com.proyecto.gav.DTO.ConductorDisponibleDTO;
import com.proyecto.gav.DTO.SolicitudPendienteDTO;
import com.proyecto.gav.DTO.ViajeClienteResponse;
import com.proyecto.gav.entity.Locacion;
import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.model.Automovil;
import com.proyecto.gav.model.Viaje;
import com.proyecto.gav.repository.LocacionRepository;
import com.proyecto.gav.repository.UsuarioRepository;
import com.proyecto.gav.repository.AutomovilRepository;
import com.proyecto.gav.Exceptions.*;

import com.proyecto.gav.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AutomovilRepository automovilRepository;

    @Autowired
    private LocacionRepository locacionRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ------------------ CONDUCTORES ------------------

    public Usuario registrarConductor(Usuario conductor) {
        if (usuarioRepository.existsByNombreUsuario(conductor.getNombreUsuario())) {
            throw new UsernameDuplicadoException("El nombre de usuario ya está en uso.");
        }

        if (usuarioRepository.existsByEmail(conductor.getEmail())) {
            throw new EmailDuplicadoException("El correo electrónico ya está registrado.");
        }

        if (usuarioRepository.existsByNumeroDocumento(conductor.getNumeroDocumento())) {
            throw new DocumentoDuplicadoException("El número de documento ya está registrado.");
        }

        if (usuarioRepository.existsByTelefono(conductor.getTelefono())) {
            throw new TelefonoDuplicadoException("El teléfono ya está registrado.");
        }

        if (conductor.getAutomovil() != null &&
                usuarioRepository.existsByAutomovil(conductor.getAutomovil())) {
            throw new VehiculoYaAsignadoException("El vehículo ya está asignado a otro conductor.");
        }

        conductor.setContrasena(passwordEncoder.encode(conductor.getContrasena()));
        conductor.setRol(Usuario.Rol.CONDUCTOR);
        conductor.setDisponibilidad(true);
        return usuarioRepository.save(conductor);
    }

    public List<Usuario> obtenerConductores() {
        return usuarioRepository.findByRol(Usuario.Rol.CONDUCTOR);
    }

    public void eliminarConductor(String id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Conductor no encontrado");
        }
        usuarioRepository.deleteById(id);
    }

    public Usuario actualizarConductor(Usuario conductorActualizado) {
        if (conductorActualizado.getId() == null) {
            throw new RuntimeException("El ID del conductor es obligatorio");
        }
        Optional<Usuario> existente = usuarioRepository.findById(conductorActualizado.getId());
        if (existente.isEmpty()) {
            throw new RuntimeException("Conductor no encontrado");
        }
        // Validar duplicados pero ignorando el propio conductor actualizado
        usuarioRepository.findByNombreUsuario(conductorActualizado.getNombreUsuario())
                .filter(u -> !u.getId().equals(conductorActualizado.getId()))
                .ifPresent(u -> { throw new UsernameDuplicadoException("El nombre de usuario ya está en uso."); });

        usuarioRepository.findByEmail(conductorActualizado.getEmail())
                .filter(u -> !u.getId().equals(conductorActualizado.getId()))
                .ifPresent(u -> { throw new EmailDuplicadoException("El correo electrónico ya está registrado."); });

        usuarioRepository.findByNumeroDocumento(conductorActualizado.getNumeroDocumento())
                .filter(u -> !u.getId().equals(conductorActualizado.getId()))
                .ifPresent(u -> { throw new DocumentoDuplicadoException("El número de documento ya está registrado."); });

        usuarioRepository.findByTelefono(conductorActualizado.getTelefono())
                .filter(u -> !u.getId().equals(conductorActualizado.getId()))
                .ifPresent(u -> { throw new TelefonoDuplicadoException("El teléfono ya está registrado."); });

        if (conductorActualizado.getAutomovil() != null) {
            usuarioRepository.findByAutomovil(conductorActualizado.getAutomovil())
                    .filter(u -> !u.getId().equals(conductorActualizado.getId()))
                    .ifPresent(u -> { throw new VehiculoYaAsignadoException("El vehículo ya está asignado a otro conductor."); });
        }

        // Manejar contraseña: si viene no vacía la codificas, si no, mantienes la existente
        if (conductorActualizado.getContrasena() != null && !conductorActualizado.getContrasena().isEmpty()) {
            conductorActualizado.setContrasena(passwordEncoder.encode(conductorActualizado.getContrasena()));
        } else {
            conductorActualizado.setContrasena(existente.get().getContrasena());
        }

        conductorActualizado.setRol(Usuario.Rol.CONDUCTOR);

        return usuarioRepository.save(conductorActualizado);
    }

    public Optional<Usuario> obtenerUsuarioPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerConductorPorId(String id) {
        return usuarioRepository.findByIdAndRol(id, Usuario.Rol.CONDUCTOR);
    }


    // ------------------ VEHÍCULOS ------------------

    public Automovil registrarVehiculo(Automovil automovil) {
        if (automovilRepository.existsByPlaca(automovil.getPlaca())) {
            throw new PlacaDuplicadaException("La placa ya está registrada.");
        }
        return automovilRepository.save(automovil);
    }

    public List<Automovil> obtenerVehiculos() {
        return automovilRepository.findAll();
    }

    public void eliminarVehiculo(String id) {
        if (!automovilRepository.existsById(id)) {
            throw new RuntimeException("Vehículo no encontrado");
        }
        automovilRepository.deleteById(id);
    }


    public Automovil actualizarVehiculo(Automovil vehiculoActualizado) {
        if (automovilRepository.existsByPlacaAndIdNot(vehiculoActualizado.getPlaca(), vehiculoActualizado.getId())) {
            throw new PlacaDuplicadaException("La placa ya está registrada por otro vehículo.");
        }
        return automovilRepository.save(vehiculoActualizado);
    }

    public Optional<Automovil> obtenerVehiculoPorId(String id) {
        return automovilRepository.findById(id);
    }

    // LOCACIONES
    public Locacion registrarLocacion(Locacion locacion) {
        // Validar nombre duplicado
        if (locacionRepository.existsByNombreLugar(locacion.getNombreLugar())) {
            throw new LocacionDuplicadaException("Ya existe una locación con ese nombre.");
        }

        // Validar coordenadas duplicadas (nuevo)
        if (locacionRepository.existsByLatitudAndLongitud(locacion.getLatitud(), locacion.getLongitud())) {
            throw new CoordenadasDuplicadasException("Ya existe una locación con estas coordenadas.");
        }

        return locacionRepository.save(locacion);
    }

    public List<Locacion> obtenerTodasLocaciones() {
        return locacionRepository.findAll();
    }

    public Locacion obtenerLocacionPorId(Long id) {
        return locacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Locación no encontrada"));
    }

    public void eliminarLocacion(Long id) {
        if (!locacionRepository.existsById(id)) {
            throw new RuntimeException("Locación no encontrada");
        }
        locacionRepository.deleteById(id);
    }

    public Locacion actualizarLocacion(Locacion locacionActualizada) {
        Locacion existente = obtenerLocacionPorId(locacionActualizada.getId());

        if (!existente.getNombreLugar().equals(locacionActualizada.getNombreLugar()) &&
                locacionRepository.existsByNombreLugar(locacionActualizada.getNombreLugar())) {
            throw new LocacionDuplicadaException("Ya existe una locación con ese nombre.");
        }

        if ((existente.getLatitud() != locacionActualizada.getLatitud() ||
                existente.getLongitud() != locacionActualizada.getLongitud()) &&
                locacionRepository.existsByLatitudAndLongitud(locacionActualizada.getLatitud(), locacionActualizada.getLongitud())) {
            throw new CoordenadasDuplicadasException("Ya existe una locación con estas coordenadas.");
        }

        return locacionRepository.save(locacionActualizada);
    }


    // LOGICA ASIGNACION

    public List<SolicitudPendienteDTO> obtenerSolicitudesPendientes() {
        List<Viaje> viajes = viajeRepository.findByEstadoViaje(Viaje.EstadoViaje.SOLICITADO);

        return viajes.stream().map(viaje -> {
            String destinoNombre = locacionRepository.findById(viaje.getDestinoId())
                    .map(Locacion::getNombreLugar)
                    .orElse("Destino desconocido");

            String clienteNombre = viaje.getCliente().getNombreCompleto();

            return new SolicitudPendienteDTO(
                    viaje.getId(),
                    destinoNombre,
                    clienteNombre,
                    viaje.getCantidadPasajeros(),
                    viaje.getFechaSolicitud()
            );
        }).toList();
    }

    public List<ConductorDisponibleDTO> obtenerConductoresDisponibles(int pasajeros) {
        List<Usuario> conductoresDisponibles = usuarioRepository.findByRolAndDisponibilidadTrue(Usuario.Rol.CONDUCTOR);

        List<ConductorDisponibleDTO> disponibles = new ArrayList<>();

        for (Usuario conductor : conductoresDisponibles) {
            Automovil automovil = conductor.getAutomovil();

            if (automovil != null && automovil.getCapacidadMaxima() >= pasajeros) {
                disponibles.add(new ConductorDisponibleDTO(
                        conductor.getId(),
                        conductor.getNombreCompleto(),
                        conductor.getTelefono(),
                        automovil
                ));
            }
        }

        return disponibles;
    }


    @Transactional
    public void asignarConductorAViaje(String viajeId, String conductorId) {
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        Usuario conductor = usuarioRepository.findById(conductorId)
                .orElseThrow(() -> new RuntimeException("Conductor no encontrado"));

        // Validar que el conductor sigue disponible
        if (!conductor.getDisponibilidad() || conductor.getAutomovil() == null) {
            throw new RuntimeException("El conductor ya no está disponible");
        }

        // Validar capacidad del vehículo
        if (conductor.getAutomovil().getCapacidadMaxima() < viaje.getCantidadPasajeros()) {
            throw new RuntimeException("El vehículo no tiene capacidad suficiente");
        }

        // Asignar conductor y vehículo
        viaje.setConductor(conductor);
        viaje.setAutomovil(conductor.getAutomovil());
        viaje.setEstadoViaje(Viaje.EstadoViaje.ASIGNADO);

        // Marcar conductor como no disponible
        conductor.setDisponibilidad(false);

        viajeRepository.save(viaje);
        usuarioRepository.save(conductor);
    }

    public List<ViajeClienteResponse> obtenerHistorialCompleto() {
        List<Viaje.EstadoViaje> estadosFinalizados = List.of(
                Viaje.EstadoViaje.FINALIZADO,
                Viaje.EstadoViaje.CANCELADO
        );

        List<Viaje> viajes = viajeRepository.findByEstadoViajeIn(estadosFinalizados);

        return viajes.stream().map(viaje -> {
            String destinoNombre = locacionRepository.findById(viaje.getDestinoId())
                    .map(Locacion::getNombreLugar)
                    .orElse("Desconocido");

            ViajeClienteResponse.ConductorInfo conductorInfo = null;
            if (viaje.getConductor() != null) {
                String nombreCompleto = viaje.getConductor().getNombreCompleto() + " " + viaje.getConductor().getApellidosCompletos();
                String telefono = viaje.getConductor().getTelefono();
                conductorInfo = new ViajeClienteResponse.ConductorInfo(nombreCompleto, telefono);
            }

            // Cálculo de duración si es FINALIZADO
            String duracionViaje = null;
            if (viaje.getEstadoViaje() == Viaje.EstadoViaje.FINALIZADO &&
                    viaje.getFechaInicio() != null && viaje.getFechaFinalizacion() != null) {

                long minutos = Duration.between(viaje.getFechaInicio().toInstant(), viaje.getFechaFinalizacion().toInstant()).toMinutes();
                long horas = minutos / 60;
                long minsRestantes = minutos % 60;
                duracionViaje = String.format("%02d:%02d", horas, minsRestantes);
            }

            return new ViajeClienteResponse(
                    viaje.getId(),
                    viaje.getCantidadPasajeros(),
                    viaje.getEstadoViaje().name(),
                    viaje.getFechaSolicitud(),
                    viaje.getFechaInicio(),
                    viaje.getFechaFinalizacion(),
                    viaje.getFechaCancelacion(),
                    destinoNombre,
                    conductorInfo,
                    viaje.getPrecioFinal(),
                    duracionViaje
            );
        }).toList();
    }



}
