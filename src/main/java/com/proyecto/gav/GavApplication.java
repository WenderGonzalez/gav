package com.proyecto.gav;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GavApplication {

	public static void main(String[] args) {
		SpringApplication.run(GavApplication.class, args);
	}
	@Bean
	CommandLineRunner generateData(...) {
    return args -> {
        try {
            System.out.println("Iniciando generación de datos...");
            // Tu lógica actual
            System.out.println("Generación de datos completada.");
        } catch (Exception e) {
            System.err.println("Error durante la generación de datos: " + e.getMessage());
            e.printStackTrace(); // Muy importante para ver el error completo
        }
    };
}

}

