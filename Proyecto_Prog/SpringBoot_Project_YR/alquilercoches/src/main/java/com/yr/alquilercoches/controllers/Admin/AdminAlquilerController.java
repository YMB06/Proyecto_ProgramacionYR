package com.yr.alquilercoches.controllers.Admin;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yr.alquilercoches.models.entities.Alquiler;
import com.yr.alquilercoches.models.entities.Clientes;
import com.yr.alquilercoches.models.entities.Coches;
import com.yr.alquilercoches.models.services.AlquilerService;
import com.yr.alquilercoches.models.services.CochesService;
import com.yr.alquilercoches.models.services.ClienteService;

@Controller
public class AdminAlquilerController {
    @Autowired
    private AlquilerService alquilerService;
    
    @Autowired
    private CochesService cochesService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/admin/alquileres")
    public String alquiler(Model model) {
        List<Alquiler> alquileres = this.alquilerService.getAll();
        model.addAttribute("LAlquiler", alquileres);
        return "admin/alquileres"; 
    }

    @GetMapping("/admin/alquileres/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("alquiler", new Alquiler());
        List<Coches> coches = cochesService.getAll();
        List<Clientes> clientes = clienteService.getAll(); 
        model.addAttribute("coches", coches);
        model.addAttribute("clientes", clientes); 
        return "admin/crearAlquileres";
    }
    
    @PostMapping("/admin/alquileres/crear")
    public String crearAlquiler(@ModelAttribute Alquiler alquiler, RedirectAttributes redirectAttributes) {
        try {
            // crea el alquiler
            alquilerService.create(alquiler);
            redirectAttributes.addFlashAttribute("mensaje", "Alquiler creado exitosamente");
            return "redirect:/admin/alquileres";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el alquiler: " + e.getMessage());
            return "redirect:/admin/alquileres/crear"; 
        }
    }

    @PostMapping("/admin/alquileres/eliminar/{id}")
    public String eliminarAlquiler(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // elimina el alquiler
            alquilerService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Alquiler eliminado exitosamente");
            return "redirect:/admin/alquileres";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el alquiler: " + e.getMessage());
            return "redirect:/admin/alquileres";
        }
    }

    @GetMapping("/admin/alquileres/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        try {
            // obtiene el alquiler y los coches y clientes para el formulario
            Alquiler alquiler = alquilerService.getId(id);
            List<Coches> coches = cochesService.getAll();
            List<Clientes> clientes = clienteService.getAll();
            
            // agrega los datos al model y muestra el formulario
            model.addAttribute("alquiler", alquiler);
            model.addAttribute("coches", coches);
            model.addAttribute("clientes", clientes);
            
            return "admin/editarAlquiler"; 
        } catch (Exception e) {
            return "redirect:/admin/alquileres";
        }
    }
    
    @PostMapping("/admin/alquileres/editar/{id}")
    public String editarAlquiler(@PathVariable Long id, @ModelAttribute Alquiler alquiler, RedirectAttributes redirectAttributes) {
        try {
            // actualiza el alquiler
            alquiler.setId(id);
            alquilerService.update(alquiler);
            redirectAttributes.addFlashAttribute("mensaje", "Alquiler actualizado exitosamente");
            return "redirect:/admin/alquileres";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el alquiler: " + e.getMessage());
            return "redirect:/admin/alquileres/editar/" + id;
        }
    }
}



