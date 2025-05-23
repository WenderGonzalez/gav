package com.proyecto.gav;

import com.proyecto.gav.entity.Locacion;
import com.proyecto.gav.model.Automovil;
import com.proyecto.gav.model.Usuario;
import com.proyecto.gav.model.Viaje;
import com.proyecto.gav.repository.AutomovilRepository;
import com.proyecto.gav.repository.LocacionRepository;
import com.proyecto.gav.repository.UsuarioRepository;
import com.proyecto.gav.repository.ViajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SpringBootApplication
public class GavApplication {

	public static void main(String[] args)	 {
		SpringApplication.run(GavApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner generateData(
			UsuarioRepository usuarioRepository,
			ViajeRepository viajeRepository,
			LocacionRepository locacionRepository,
			AutomovilRepository automovilRepository,
			PasswordEncoder passwordEncoder) {

		return args -> {
			Random random = new Random();

			// 1. LOCACIONES
			List<Locacion> locaciones = List.of(
					new Locacion("Aeropuerto Rafael Núñez", "Es la principal puerta de entrada aérea a Cartagena", 10.4456353, -75.5162998, new BigDecimal("65000")),
					new Locacion("Playa Blanca Cartagena", "Un paraíso de aguas cristalinas", 10.2243139, -75.6077949, new BigDecimal("70000")),
					new Locacion("Cerro La Popa Cartagena", "El punto más alto de Cartagena", 10.4234841, -75.5263002, new BigDecimal("55000")),
					new Locacion("CC La Serrezuela Cartagena", "Centro comercial de lujo", 10.427356, -75.5451417, new BigDecimal("60000")),
					new Locacion("Bocagrande Cartagena", "Zona turística por excelencia", 10.4040929, -75.553419, new BigDecimal("58000")),
					new Locacion("Castillo San Felipe de Barajas", "Imponente fortaleza colonial", 10.4227114, -75.5393663, new BigDecimal("75000")),
					new Locacion("La Boquilla Cartagena", "Comunidad pesquera con playas tranquilas", 10.4760437, -75.4947047, new BigDecimal("63000")),
					new Locacion("Getsemaní Cartagena", "Barrio bohemio y lleno de vida", 10.4224089, -75.5438889, new BigDecimal("54000")),
					new Locacion("Playa Azul Cartagena", "Playa certificada Blue Flag", -33.5264525, -71.6047136, new BigDecimal("80000")),
					new Locacion("Muelle de los Pegasos Cartagena", "Histórico embarcadero", 10.4221599, -75.5488189, new BigDecimal("68000"))
			);
			locacionRepository.saveAll(locaciones);


			// 2. VEHÍCULOS Y CONDUCTORES
			List<Automovil> autos = new ArrayList<>();
			List<Usuario> conductores = new ArrayList<>();
			for (int i = 0; i < 20; i++) {
				Automovil auto = new Automovil();
				auto.setCapacidadMaxima(random.nextInt(3) + 4); // 4 a 6
				auto.setMarca("Marca" + i);
				auto.setModelo("Modelo" + i);
				auto.setPlaca("XYZ" + (100 + i));
				auto.setCategoria(Automovil.Categoria.values()[random.nextInt(Automovil.Categoria.values().length)]);
				automovilRepository.save(auto);
				autos.add(auto);

				Usuario conductor = new Usuario();
				conductor.setNombreCompleto("Conductor" + i);
				conductor.setApellidosCompletos("Apellido" + i);
				conductor.setNombreUsuario("conductor" + i);
				conductor.setContrasena(passwordEncoder.encode("pass" + i));
				conductor.setEmail("conductor" + i + "@test.com");
				conductor.setFechaNacimiento(new Date(90, random.nextInt(12), random.nextInt(28) + 1));
				conductor.setNumeroDocumento("1000" + i);
				conductor.setTipoDocumento(Usuario.TipoDocumento.CEDULA);
				conductor.setTelefono("30000000" + i);
				conductor.setRol(Usuario.Rol.CONDUCTOR);
				conductor.setSexo(i % 2 == 0 ? Usuario.Sexo.MASCULINO : Usuario.Sexo.FEMENINO);
				conductor.setEdad(30 + i);
				conductor.setDisponibilidad(true);
				conductor.setLicencia("LIC" + i);
				conductor.setTipoLicencia(Usuario.TipoLicencia.values()[random.nextInt(3)]);
				conductor.setAutomovil(auto);
				usuarioRepository.save(conductor);
				conductores.add(conductor);
			}

			// 3. CLIENTES
			List<Usuario> clientes = new ArrayList<>();
			for (int i = 0; i < 30; i++) {
				Usuario cliente = new Usuario();
				cliente.setNombreCompleto("Cliente" + i);
				cliente.setApellidosCompletos("Apellido" + i);
				cliente.setNombreUsuario("cliente" + i);
				cliente.setContrasena(passwordEncoder.encode("pass" + i));
				cliente.setEmail("cliente" + i + "@test.com");
				cliente.setFechaNacimiento(new Date(95, random.nextInt(12), random.nextInt(28) + 1));
				cliente.setNumeroDocumento("2000" + i);
				cliente.setTipoDocumento(Usuario.TipoDocumento.CEDULA);
				cliente.setTelefono("31000000" + i);
				cliente.setRol(Usuario.Rol.CLIENTE);
				cliente.setSexo(i % 2 == 0 ? Usuario.Sexo.MASCULINO : Usuario.Sexo.FEMENINO);
				cliente.setEdad(20 + i);
				usuarioRepository.save(cliente);
				clientes.add(cliente);
			}

			// 4. VIAJES
			List<Locacion> destinos = locacionRepository.findAll().stream()
					.filter(loc -> !loc.getNombreLugar().equalsIgnoreCase("Hotel Estelar Manzanillo"))
					.collect(Collectors.toList());

			for (int i = 0; i < 1000; i++) {
				Usuario cliente = clientes.get(random.nextInt(clientes.size()));
				Usuario conductor = conductores.get(random.nextInt(conductores.size()));
				Automovil auto = conductor.getAutomovil();
				Locacion destino = destinos.get(random.nextInt(destinos.size()));

				int cantidadPasajeros = random.nextInt(6) + 1;
				BigDecimal precioBase = destino.getPrecio();
				BigDecimal precioFinal = cantidadPasajeros > 4
						? precioBase.multiply(BigDecimal.valueOf(1.05))
						: precioBase;

				if (precioFinal.compareTo(new BigDecimal("50000")) <= 0) continue;

				Viaje viaje = new Viaje();
				viaje.setCliente(cliente);
				viaje.setConductor(conductor);
				viaje.setAutomovil(auto);
				viaje.setOrigenId(1L);
				viaje.setDestinoId(destino.getId());
				viaje.setCantidadPasajeros(cantidadPasajeros);
				viaje.setPrecioFinal(precioFinal);

				Date ahora = new Date();
				Date fechaSolicitud = new Date(ahora.getTime() - random.nextInt(100_000_000));
				viaje.setFechaSolicitud(fechaSolicitud);

				boolean esFinalizado = random.nextBoolean();
				if (esFinalizado) {
					viaje.setEstadoViaje(Viaje.EstadoViaje.FINALIZADO);
					Date inicio = new Date(fechaSolicitud.getTime() + random.nextInt(1_000_000));
					Date fin = new Date(inicio.getTime() + random.nextInt(3_600_000));
					viaje.setFechaInicio(inicio);
					viaje.setFechaFinalizacion(fin);
				} else {
					viaje.setEstadoViaje(Viaje.EstadoViaje.CANCELADO);
					Date cancel = new Date(fechaSolicitud.getTime() + random.nextInt(1_000_000));
					viaje.setFechaCancelacion(cancel);
				}

				viajeRepository.save(viaje);
			}

			System.out.println("✅ Se generaron:");
			System.out.println("- 10 locaciones (más el Hotel Estelar como origen)");
			System.out.println("- 20 conductores con automóviles");
			System.out.println("- 30 clientes");
			System.out.println("- 1000 viajes en estado FINALIZADO o CANCELADO");
		};
	}




}
