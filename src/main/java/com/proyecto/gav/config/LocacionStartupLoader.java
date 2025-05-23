package com.proyecto.gav.config;

import com.proyecto.gav.entity.Locacion;
import com.proyecto.gav.repository.LocacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class LocacionStartupLoader implements CommandLineRunner {

    @Autowired
    private LocacionRepository locacionRepository;

    @Override
    public void run(String... args) {
        String nombreHotel = "Hotel Estelar Manzanillo";

        boolean existe = locacionRepository.existsByNombreLugar(nombreHotel);
        if (!existe) {
            Locacion hotel = new Locacion();
            hotel.setNombreLugar(nombreHotel);
            hotel.setDescripcion("Punto de partida oficial para los viajes del hotel.");
            hotel.setLatitud(10.524308437901766);
            hotel.setLongitud(-75.49448764230837);
            hotel.setPrecio(BigDecimal.ZERO);

            locacionRepository.save(hotel);
            System.out.println("✅ Locación de origen creada correctamente");
        }
    }
}
