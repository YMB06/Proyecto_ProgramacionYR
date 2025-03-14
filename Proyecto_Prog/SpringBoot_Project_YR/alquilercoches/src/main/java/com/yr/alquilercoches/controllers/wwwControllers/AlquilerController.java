package com.yr.alquilercoches.controllers.wwwControllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yr.alquilercoches.models.entities.Alquiler;
import com.yr.alquilercoches.models.entities.CustomUserDetails;
import com.yr.alquilercoches.models.services.AlquilerService;
import com.yr.alquilercoches.models.services.CochesService;

@Controller
public class AlquilerController {

    @Autowired
    private AlquilerService alquilerService;

    @Autowired
    private CochesService cochesService;

    @GetMapping("/alquiler")
    public String alquiler(Model model) {

        List<Alquiler> alquileres = this.alquilerService.getAll(); //
        alquileres.forEach(System.out::println);
        model.addAttribute("LAlquiler", alquileres);
        return "/www/alquiler/index";

    }

    @GetMapping("/alquiler/{id}")
    public String getAlquiler(@PathVariable(value = "id", required = false) Long id, Model model) {
        System.out.println("El alquiler es " + id);
        Alquiler alquiler = this.alquilerService.getId(id);
        System.out.println(alquiler);
        model.addAttribute("LAlquiler", alquiler);
        return "/www/alquiler/detalle";

    }

    @PostMapping("/alquiler/reservar")
    public String reservarCoche(@RequestParam Long cocheId,
            @RequestParam String fecha_inicio,
            @RequestParam String fecha_fin,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            // verifica si el coche está disponible
            if (!alquilerService.isCarAvailable(cocheId, fecha_inicio, fecha_fin)) {
                redirectAttributes.addFlashAttribute("error", "El coche no está disponible para estas fechas");
                return "redirect:/coches/" + cocheId;
            }

            // crea el alquiler
            Alquiler alquiler = new Alquiler();
            alquiler.setCoche(cochesService.getId(cocheId));
            alquiler.setFecha_inicio(fecha_inicio);
            alquiler.setFecha_fin(fecha_fin);
            alquiler.setCliente(userDetails.getCliente());

            // calcula el precio total
            BigDecimal precioBase = alquiler.getCoche().getPrecio();
            LocalDate start = LocalDate.parse(fecha_inicio);
            LocalDate end = LocalDate.parse(fecha_fin);
            long days = ChronoUnit.DAYS.between(start, end);
            BigDecimal precioTotal = precioBase.multiply(new BigDecimal(days));
            alquiler.setPrecio_total(precioTotal);

            alquilerService.create(alquiler);
            redirectAttributes.addFlashAttribute("mensaje", "¡Reserva realizada con éxito!");
            return "redirect:/coches/" + cocheId;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al realizar la reserva: " + e.getMessage());
            return "redirect:/coches/" + cocheId;
        }
    }

    // comprueba la disponibilidad de un coche para un rango de fechas
    @GetMapping("/api/alquiler/check-availability")
    @ResponseBody
    public boolean checkAvailability(@RequestParam Long cocheId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return alquilerService.isCarAvailable(cocheId, startDate, endDate);
    }
}
