package com.proyecto.gav.controller;

import com.proyecto.gav.DTO.SolicitudViajeRequest;
import com.proyecto.gav.DTO.ViajeClienteResponse;
import com.proyecto.gav.entity.Locacion;
import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.model.Viaje;
import com.proyecto.gav.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cliente")
public class ClienteRestController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/destinos")
    public List<Locacion> obtenerDestinosDisponibles() {
        return clienteService.obtenerDestinosDisponibles();
    }

    @PostMapping("/viajes")
    public ResponseEntity<?> solicitarViaje(@RequestBody SolicitudViajeRequest solicitud, Principal principal) {
        try {
            String usernameCliente = principal.getName();
            Viaje viaje = clienteService.solicitarViaje(solicitud, usernameCliente);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Viaje solicitado correctamente",
                    "idViaje", viaje.getId()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/mis-viajes")
    public ResponseEntity<?> getMisViajes(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autenticado"));
            }

            String nombreUsuario = authentication.getName();  // obtiene nombreUsuario

            Usuario usuario = clienteService.buscarPorNombreUsuario(nombreUsuario);

            if (usuario == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            List<ViajeClienteResponse> viajes = clienteService.obtenerViajesActivosDelCliente(usuario.getId());
            return ResponseEntity.ok(viajes);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudieron cargar los viajes: " + e.getMessage()));
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<?> getHistorialViajes(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autenticado"));
            }

            String nombreUsuario = authentication.getName();

            Usuario usuario = clienteService.buscarPorNombreUsuario(nombreUsuario);

            if (usuario == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Usuario no encontrado"));
            }

            List<ViajeClienteResponse> historial = clienteService.obtenerHistorialViajesDelCliente(usuario.getId());
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "No se pudo obtener el historial: " + e.getMessage()));
        }
    }


    @PutMapping("/viajes/{id}/cancelar")
    public ResponseEntity<?> cancelarViaje(@PathVariable String id, Principal principal) {
        try {
            String usernameCliente = principal.getName();
            clienteService.cancelarViaje(id, usernameCliente);
            return ResponseEntity.ok(Map.of("mensaje", "Viaje cancelado correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }










}
