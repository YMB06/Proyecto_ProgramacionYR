package com.yr.alquilercoches.controllers.Admin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.yr.alquilercoches.models.entities.Alquiler;
import com.yr.alquilercoches.models.entities.Coches;
import com.yr.alquilercoches.models.services.AlquilerService;
import com.yr.alquilercoches.models.services.CochesService;

@Controller
public class AdminCochesController {
    
    @Autowired
    private CochesService cochesService;

    @GetMapping("/admin/coches")
    public String coches(Model model){
        List<Coches> coches = this.cochesService.getAll();
        model.addAttribute("LCoches", coches);
        return "admin/Coches";
    }

    @GetMapping("/admin/coches/crear")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("coche", new Coches());
        return "admin/crearCoches";
    }

   @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/admin/coches/crear")
    public String crearCoche(@ModelAttribute Coches coche, 
                           @RequestParam("file") MultipartFile file,
                           RedirectAttributes redirectAttributes) {
        try {
            // maneja la subida de archivos
            if (!file.isEmpty()) {
                // genera un nombre de archivo único
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                
                // crea el directorio donde s eguardan si todavia no exite
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                
                // guarda el archivo
                Path filePath = uploadDir.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // guarda el nombre de la imagen en su campo dentro de la entidad de coches
                coche.setImagen(filename);
            }
            
            cochesService.save(coche);
            redirectAttributes.addFlashAttribute("mensaje", "Coche creado exitosamente");
            return "redirect:/admin/coches";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el coche: " + e.getMessage());
            return "redirect:/admin/coches/crear";
        }
    }
    @GetMapping("/admin/coches/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        try {
            Coches coche = cochesService.getId(id);
            if (coche != null) {
                model.addAttribute("coche", coche);
                return "admin/editarCoches";
            } else {
                return "redirect:/admin/coches";
            }
        } catch (Exception e) {
            return "redirect:/admin/coches";
        }
    }
    @PostMapping("/admin/coches/editar/{id}")
    public String editarCoche(@PathVariable Long id, 
                             @ModelAttribute Coches coche,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes) {
        try {
            // maneja la subida de archivos
            if (file != null && !file.isEmpty()) {
                // elimina la imagen anterior si es que habia alguna previamente
                Coches existingCoche = cochesService.getId(id);
                if (existingCoche != null && existingCoche.getImagen() != null) {
                    Path oldFile = Paths.get(uploadPath).resolve(existingCoche.getImagen());
                    Files.deleteIfExists(oldFile);
                }
    
                // genera un nombre para esa imagen
                String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                
                // crea el directorio si no existe todavia
                Path uploadDir = Paths.get(uploadPath);
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }
                
                // guarda la nueva imagen
                Path filePath = uploadDir.resolve(filename);
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                
                // guarda la nueva imagen en su respectivo campo en la entidad de coches
                coche.setImagen(filename);
            } else {
                // deja la imagen que ya habia previamente si no se ha cambiado 
                Coches existingCoche = cochesService.getId(id);
                if (existingCoche != null) {
                    coche.setImagen(existingCoche.getImagen());
                }
            }
            
            coche.setId(id);
            cochesService.update(coche);
            redirectAttributes.addFlashAttribute("mensaje", "Coche actualizado exitosamente");
            return "redirect:/admin/coches";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el coche: " + e.getMessage());
            return "redirect:/admin/coches/editar/" + id;
        }
    }

    @Autowired
private AlquilerService alquilerService; 

@PostMapping("/admin/coches/borrar/{id}")
public String borrarCoche(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        Coches coche = cochesService.getId(id);
        if (coche != null) {
            // elimina los alquileres asociados
            List<Alquiler> alquileresAsociados = alquilerService.findByCocheId(id);
            for (Alquiler alquiler : alquileresAsociados) {
                alquilerService.deleteById(alquiler.getId());
            }
            
            // luego elimina el coche
            cochesService.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Coche y sus alquileres asociados eliminados exitosamente");
        } else {
            redirectAttributes.addFlashAttribute("error", "Coche no encontrado");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al eliminar el coche: " + e.getMessage());
    }
    return "redirect:/admin/coches";
}
}
