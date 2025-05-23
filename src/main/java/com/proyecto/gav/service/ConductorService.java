package com.proyecto.gav.service;

import com.proyecto.gav.DTO.ViajeConductorResponse;
import com.proyecto.gav.entity.Locacion;
import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.model.Viaje;
import com.proyecto.gav.repository.LocacionRepository;
import com.proyecto.gav.repository.UsuarioRepository;
import com.proyecto.gav.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
public class ConductorService {

    @Autowired
    private LocacionRepository locacionRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public Usuario buscarPorNombreUsuario(String username) {
        return usuarioRepository.findByNombreUsuario(username).orElse(null);
    }

    public List<ViajeConductorResponse> obtenerViajesAsignadosOEnCurso(String conductorId) {
        List<Viaje.EstadoViaje> estados = List.of(
                Viaje.EstadoViaje.ASIGNADO,
                Viaje.EstadoViaje.EN_CURSO
        );

        List<Viaje> viajes = viajeRepository.findByConductorIdAndEstadoViajeIn(conductorId, estados);

        return viajes.stream().map(viaje -> {
            String destinoNombre = locacionRepository.findById(viaje.getDestinoId())
                    .map(Locacion::getNombreLugar)
                    .orElse("Desconocido");

            // Obtener info del cliente
            Usuario cliente = viaje.getCliente();
            String nombreCompleto = cliente.getNombreCompleto() + " " + cliente.getApellidosCompletos();
            String telefono = cliente.getTelefono();
            ViajeConductorResponse.ClienteInfo clienteInfo =
                    new ViajeConductorResponse.ClienteInfo(nombreCompleto, telefono);

            return new ViajeConductorResponse(
                    viaje.getId(),
                    viaje.getCantidadPasajeros(),
                    viaje.getEstadoViaje().name(),
                    viaje.getFechaSolicitud(),
                    viaje.getFechaInicio(),
                    viaje.getFechaFinalizacion(),
                    viaje.getFechaCancelacion(),
                    destinoNombre,
                    clienteInfo,
                    viaje.getPrecioFinal(),
                    null
            );
        }).toList();
    }

    public void iniciarViaje(String viajeId) {
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));
        viaje.setEstadoViaje(Viaje.EstadoViaje.EN_CURSO);
        viaje.setFechaInicio(new Date());
        viajeRepository.save(viaje);
    }

    public void finalizarViaje(String viajeId) {
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));
        viaje.setEstadoViaje(Viaje.EstadoViaje.FINALIZADO);
        viaje.setFechaFinalizacion(new Date());

        Usuario conductor = viaje.getConductor();
        if (conductor != null) {
            conductor.setDisponibilidad(true);
            usuarioRepository.save(conductor);
        }

        viajeRepository.save(viaje);
    }

    public List<ViajeConductorResponse> obtenerHistorialViajesDelConductor(String conductorId) {
        List<Viaje.EstadoViaje> estadosFinalizados = List.of(
                Viaje.EstadoViaje.FINALIZADO,
                Viaje.EstadoViaje.CANCELADO
        );

        List<Viaje> viajes = viajeRepository.findByConductorIdAndEstadoViajeIn(conductorId, estadosFinalizados);

        return viajes.stream().map(viaje -> {
            String destinoNombre = locacionRepository.findById(viaje.getDestinoId())
                    .map(Locacion::getNombreLugar)
                    .orElse("Desconocido");

            // Cliente info
            ViajeConductorResponse.ClienteInfo clienteInfo = null;
            if (viaje.getCliente() != null) {
                String nombre = viaje.getCliente().getNombreCompleto() + " " + viaje.getCliente().getApellidosCompletos();
                String telefono = viaje.getCliente().getTelefono();
                clienteInfo = new ViajeConductorResponse.ClienteInfo(nombre, telefono);
            }

            // Calcular duración del viaje (si aplica)
            String duracionViaje = null;
            if (viaje.getFechaInicio() != null && viaje.getFechaFinalizacion() != null) {
                long minutos = Duration.between(viaje.getFechaInicio().toInstant(), viaje.getFechaFinalizacion().toInstant()).toMinutes();
                long horas = minutos / 60;
                long minsRestantes = minutos % 60;
                duracionViaje = String.format("%02d:%02d", horas, minsRestantes);
            }

            return new ViajeConductorResponse(
                    viaje.getId(),
                    viaje.getCantidadPasajeros(),
                    viaje.getEstadoViaje().name(),
                    viaje.getFechaSolicitud(),
                    viaje.getFechaInicio(),
                    viaje.getFechaFinalizacion(),
                    viaje.getFechaCancelacion(),
                    destinoNombre,
                    clienteInfo,
                    viaje.getPrecioFinal(),
                    duracionViaje // NUEVO CAMPO
            );
        }).toList();
    }



}
