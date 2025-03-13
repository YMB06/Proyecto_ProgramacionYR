package com.yr.alquilercoches.controllers.wwwControllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.yr.alquilercoches.models.entities.Coches;
import com.yr.alquilercoches.models.services.CochesService;

@Controller
public class CochesController {

    @Autowired
    private CochesService cochesService;

    @GetMapping("/coches")
    public String alquiler(Model model){
        List<Coches> coches = this.cochesService.getAll();
        model.addAttribute("coches", coches);
        return "www/Coches/index";
    }

    @GetMapping("/coches/{id}")
    public String getAlquiler(@PathVariable Long id, Model model){
        Coches coche = this.cochesService.getId(id);
        model.addAttribute("coche", coche);
        return "www/Coches/detalle";
    }
}