package com.proyecto.gav.service;

import com.proyecto.gav.DTO.SolicitudViajeRequest;
import com.proyecto.gav.DTO.ViajeClienteResponse;
import com.proyecto.gav.entity.Locacion;
import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.model.Viaje;
import com.proyecto.gav.model.Usuario.Rol;
import com.proyecto.gav.repository.LocacionRepository;
import com.proyecto.gav.repository.UsuarioRepository;
import com.proyecto.gav.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Date;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ViajeRepository viajeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LocacionRepository locacionRepository;


    public Usuario buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario).orElse(null);
    }


    public boolean nombreUsuarioExiste(String nombreUsuario) {
        return usuarioRepository.existsByNombreUsuario(nombreUsuario);
    }

    public boolean emailExiste(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public boolean numeroDocumentoExiste(String numeroDocumento) {
        return usuarioRepository.existsByNumeroDocumento(numeroDocumento);
    }

    public boolean telefonoExiste(String telefono) {
        return usuarioRepository.existsByTelefono(telefono);
    }

    public void registrarCliente(Usuario cliente) {

        cliente.setContrasena(passwordEncoder.encode(cliente.getContrasena()));
        cliente.setRol(Usuario.Rol.CLIENTE);
        usuarioRepository.save(cliente);
    }

    // ZONA LOGICA DE LOS VIAJES DEL CLIENTE
    private static final String NOMBRE_ORIGEN = "Hotel Estelar Manzanillo";

    public List<Locacion> obtenerDestinosDisponibles() {
        return locacionRepository.findAllByNombreLugarNot(NOMBRE_ORIGEN);
    }

    public Viaje solicitarViaje(SolicitudViajeRequest solicitud, String usernameCliente) {
        Usuario cliente = usuarioRepository.findByNombreUsuario(usernameCliente)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Validar si hay viajes activos (SOLICITADO, ASIGNADO, EN_CURSO)
        List<Viaje.EstadoViaje> estadosActivos = List.of(
                Viaje.EstadoViaje.SOLICITADO,
                Viaje.EstadoViaje.ASIGNADO,
                Viaje.EstadoViaje.EN_CURSO
        );
        boolean tieneViajeActivo = viajeRepository.findByClienteIdAndEstadoViajeIn(cliente.getId(), estadosActivos).size() > 0;

        if (tieneViajeActivo) {
            throw new RuntimeException("No puedes solicitar otro viaje porque ya tienes uno pendiente.");
        }

        Locacion destino = locacionRepository.findById(solicitud.getDestinoId())
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        Viaje viaje = new Viaje();
        viaje.setCantidadPasajeros(solicitud.getCantidadPasajeros());
        viaje.setDestinoId(destino.getId());
        viaje.setOrigenId(1L); // Hotel fijo
        viaje.setEstadoViaje(Viaje.EstadoViaje.SOLICITADO);
        viaje.setCliente(cliente);
        viaje.setFechaSolicitud(new Date());
        viaje.setAutomovil(null);
        viaje.setConductor(null);

        // Calcular y asignar precio final
        BigDecimal precioFinal = BigDecimal.valueOf(calcularPrecioFinal(viaje))
                .setScale(2, RoundingMode.HALF_UP);
        viaje.setPrecioFinal(precioFinal);

        return viajeRepository.save(viaje);
    }



    public List<ViajeClienteResponse> obtenerViajesActivosDelCliente(String clienteId) {
        List<Viaje.EstadoViaje> estadosActivos = List.of(
                Viaje.EstadoViaje.SOLICITADO,
                Viaje.EstadoViaje.ASIGNADO,
                Viaje.EstadoViaje.EN_CURSO
        );

        List<Viaje> viajes = viajeRepository.findByClienteIdAndEstadoViajeIn(clienteId, estadosActivos);

        return viajes.stream().map(viaje -> {
            String destinoNombre = locacionRepository.findById(viaje.getDestinoId())
                    .map(locacion -> locacion.getNombreLugar())
                    .orElse("Desconocido");

            ViajeClienteResponse.ConductorInfo conductorInfo = null;
            if (viaje.getConductor() != null) {
                String nombreCompleto = viaje.getConductor().getNombreCompleto() + " " + viaje.getConductor().getApellidosCompletos();
                String telefono = viaje.getConductor().getTelefono();
                conductorInfo = new ViajeClienteResponse.ConductorInfo(nombreCompleto, telefono);
            }

            return new ViajeClienteResponse(
                    viaje.getId(),
                    viaje.getCantidadPasajeros(),
                    viaje.getEstadoViaje().name(),
                    viaje.getFechaSolicitud(),
                    viaje.getFechaInicio(),
                    null,
                    null,
                    destinoNombre,
                    conductorInfo,
                    viaje.getPrecioFinal(),
                    null
            );
        }).toList();
    }


    public List<ViajeClienteResponse> obtenerHistorialViajesDelCliente(String clienteId) {
        List<Viaje.EstadoViaje> estadosFinalizados = List.of(
                Viaje.EstadoViaje.FINALIZADO,
                Viaje.EstadoViaje.CANCELADO
        );

        List<Viaje> viajes = viajeRepository.findByClienteIdAndEstadoViajeIn(clienteId, estadosFinalizados);

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

            String duracionViaje = null;
            if (viaje.getFechaInicio() != null && viaje.getFechaFinalizacion() != null) {
                long millis = viaje.getFechaFinalizacion().getTime() - viaje.getFechaInicio().getTime();
                long minutos = millis / (60 * 1000);
                long horas = minutos / 60;
                long mins = minutos % 60;
                duracionViaje = String.format("%02d:%02d", horas, mins);
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



    @Transactional
    public void cancelarViaje(String viajeId, String usernameCliente) {
        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RuntimeException("Viaje no encontrado"));

        if (!viaje.getCliente().getNombreUsuario().equals(usernameCliente)) {
            throw new RuntimeException("No tienes permiso para cancelar este viaje");
        }

        if (viaje.getEstadoViaje() != Viaje.EstadoViaje.SOLICITADO &&
                viaje.getEstadoViaje() != Viaje.EstadoViaje.ASIGNADO) {
            throw new RuntimeException("No se puede cancelar un viaje en este estado");
        }

        if (viaje.getConductor() != null) {
            Usuario conductor = viaje.getConductor();
            conductor.setDisponibilidad(true);
            usuarioRepository.save(conductor);
        }

        viaje.setEstadoViaje(Viaje.EstadoViaje.CANCELADO);
        viaje.setFechaCancelacion(new Date());
        viajeRepository.save(viaje);
    }




    public double calcularPrecioFinal(Viaje viaje) {
        Locacion destino = locacionRepository.findById(viaje.getDestinoId())
                .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

        double precioBase = destino.getPrecio().doubleValue();
        int pasajeros = viaje.getCantidadPasajeros();

        if (pasajeros <= 4) {
            return precioBase;
        }

        int extras = pasajeros - 4;
        double incremento = 1 + (0.05 * extras);
        return precioBase * incremento;
    }





}
