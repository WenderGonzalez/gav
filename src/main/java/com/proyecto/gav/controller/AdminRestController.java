package com.proyecto.gav.controller;

import com.proyecto.gav.DTO.ConductorDisponibleDTO;
import com.proyecto.gav.DTO.SolicitudPendienteDTO;
import com.proyecto.gav.DTO.ViajeClienteResponse;
import com.proyecto.gav.entity.Locacion;
import com.proyecto.gav.model.Automovil;
import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.repository.AutomovilRepository;
import com.proyecto.gav.service.AdminService;
import com.proyecto.gav.Exceptions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdminRestController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AutomovilRepository automovilRepository;

    // VEHÍCULOS

    @GetMapping("/vehiculos")
    public List<Automovil> listarVehiculos() {
        return adminService.obtenerVehiculos();
    }

    @PostMapping("/vehiculos")
    public ResponseEntity<?> registrarVehiculo(@RequestBody Automovil automovil) {
        try {
            Automovil creado = adminService.registrarVehiculo(automovil);
            return ResponseEntity.ok(creado);
        } catch (PlacaDuplicadaException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/vehiculos/{id}")
    public ResponseEntity<?> eliminarVehiculo(@PathVariable String id) {
        try {
            adminService.eliminarVehiculo(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/vehiculos/{id}")
    public ResponseEntity<?> actualizarVehiculo(@PathVariable String id, @RequestBody Automovil automovil) {
        try {
            automovil.setId(id);  // Asegúrate de setear el id del vehículo que se actualiza
            Automovil actualizado = adminService.actualizarVehiculo(automovil);
            return ResponseEntity.ok(actualizado);
        } catch (PlacaDuplicadaException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/vehiculos/{id}")
    public ResponseEntity<?> obtenerVehiculo(@PathVariable String id) {
        Optional<Automovil> vehiculoOpt = adminService.obtenerVehiculoPorId(id);
        if (vehiculoOpt.isPresent()) {
            return ResponseEntity.ok(vehiculoOpt.get());
        } else {
            return ResponseEntity.status(404).body(new ErrorResponse("Vehículo no encontrado"));
        }
    }



    // CONDUCTORES
    @GetMapping("/conductores")
    public List<Usuario> listarConductores() {
        return adminService.obtenerConductores();
    }

    @PostMapping("/conductores")
    public ResponseEntity<?> registrarConductor(@RequestBody Usuario conductorRequest) {
        try {
            // 1. Procesar referencia al vehículo si existe
            if (conductorRequest.getAutomovil() != null && conductorRequest.getAutomovil().getId() != null) {
                // 2. Buscar el vehículo completo
                Automovil vehiculo = automovilRepository.findById(conductorRequest.getAutomovil().getId())
                        .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

                // 3. Asignar el vehículo completo al conductor
                conductorRequest.setAutomovil(vehiculo);
            }

            // 4. Registrar conductor (validaciones se ejecutan en el servicio)
            Usuario creado = adminService.registrarConductor(conductorRequest);
            return ResponseEntity.ok(creado);

        } catch (UsernameDuplicadoException | EmailDuplicadoException |
                 DocumentoDuplicadoException | TelefonoDuplicadoException |
                 VehiculoYaAsignadoException e) {
            // Excepciones específicas de tu servicio
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));

        } catch (RuntimeException e) {
            // Para manejar "Vehículo no encontrado" y otros errores inesperados
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/conductores/{id}")
    public ResponseEntity<?> eliminarConductor(@PathVariable String id) {
        try {
            adminService.eliminarConductor(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/conductores/{id}")
    public ResponseEntity<?> actualizarConductor(@PathVariable String id, @RequestBody Usuario conductor) {
        try {
            conductor.setId(id);  // Asumiendo que Usuario tiene campo id y setter
            Usuario actualizado = adminService.actualizarConductor(conductor);
            return ResponseEntity.ok(actualizado);
        } catch (UsernameDuplicadoException | EmailDuplicadoException | DocumentoDuplicadoException |
                 TelefonoDuplicadoException | VehiculoYaAsignadoException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/conductores/{id}")
    public ResponseEntity<?> obtenerConductor(@PathVariable String id) {
        Optional<Usuario> conductorOpt = adminService.obtenerConductorPorId(id);

        if (conductorOpt.isPresent()) {
            return ResponseEntity.ok(conductorOpt.get());
        } else {
            return ResponseEntity.status(404)
                    .body(new ErrorResponse("Conductor no encontrado"));
        }
    }

    // LOCACIONES
    @PostMapping("/locaciones")
    public ResponseEntity<?> registrarLocacion(@RequestBody Locacion locacion) {
        try {
            Locacion creada = adminService.registrarLocacion(locacion);
            return ResponseEntity.ok(creada);
        } catch (LocacionDuplicadaException | CoordenadasDuplicadasException e) { // <-- Nueva excepción
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/locaciones")
    public List<Locacion> listarLocaciones() {
        return adminService.obtenerTodasLocaciones();
    }

    @GetMapping("/locaciones/{id}")
    public ResponseEntity<?> obtenerLocacion(@PathVariable Long id) {
        try {
            Locacion locacion = adminService.obtenerLocacionPorId(id);
            return ResponseEntity.ok(locacion);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/locaciones/{id}")
    public ResponseEntity<?> eliminarLocacion(@PathVariable Long id) {
        try {
            adminService.eliminarLocacion(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/locaciones/{id}")
    public ResponseEntity<?> actualizarLocacion(@PathVariable Long id, @RequestBody Locacion locacion) {
        try {
            locacion.setId(id);
            Locacion actualizada = adminService.actualizarLocacion(locacion);
            return ResponseEntity.ok(actualizada);
        } catch (LocacionDuplicadaException | CoordenadasDuplicadasException e) { // <-- Nueva excepción
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage()));
        }
    }

    // SOLICITUDES
    @GetMapping("/solicitudes-pendientes")
    public ResponseEntity<?> getSolicitudesPendientes() {
        try {
            List<SolicitudPendienteDTO> solicitudes = adminService.obtenerSolicitudesPendientes();
            return ResponseEntity.ok(solicitudes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/conductores-disponibles")
    public ResponseEntity<?> getConductoresDisponibles(
            @RequestParam int pasajeros) {
        try {
            List<ConductorDisponibleDTO> conductores = adminService.obtenerConductoresDisponibles(pasajeros);
            return ResponseEntity.ok(conductores);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/asignar-conductor")
    public ResponseEntity<?> asignarConductor(
            @RequestBody Map<String, String> request) {
        try {
            String viajeId = request.get("viajeId");
            String conductorId = request.get("conductorId");

            adminService.asignarConductorAViaje(viajeId, conductorId);
            return ResponseEntity.ok(Map.of("mensaje", "Conductor asignado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorialCompleto() {
        try {
            List<ViajeClienteResponse> historial = adminService.obtenerHistorialCompleto();
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener historial: " + e.getMessage()));
        }
    }

}
