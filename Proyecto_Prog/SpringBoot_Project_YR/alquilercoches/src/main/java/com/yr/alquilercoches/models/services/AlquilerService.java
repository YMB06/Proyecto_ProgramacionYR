package com.yr.alquilercoches.models.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import com.yr.alquilercoches.models.entities.Alquiler;
import com.yr.alquilercoches.models.repositories.AlquilerRepository;

@Service
public class AlquilerService {

    @Autowired
    AlquilerRepository alquilerRepository;

    public List<Alquiler> getAll(){
        return this.alquilerRepository.findAll();
    }
    
    //create
    public Alquiler create(Alquiler alquiler){
        return this.alquilerRepository.save(alquiler);
    }

    //update
    public Alquiler update(Alquiler alquiler){
        return this.alquilerRepository.save(alquiler);
    }

    //delete
    public void delete(Alquiler alquiler){
        this.alquilerRepository.delete(alquiler);
    }

    public void deleteById(Long id){
        this.alquilerRepository.deleteById(id);
    }
    public Alquiler getId(Long id){
        Alquiler alquiler = this.alquilerRepository.findById(id).get();
        System.out.println(alquiler);
        return alquiler;
    }

    public List<Alquiler> findByCocheId(Long cocheId) {
        return alquilerRepository.findByCocheId(cocheId);
    }

    public List<Alquiler> findByClienteId(Long clienteId) {
        return alquilerRepository.findByClienteId(clienteId);
    }

    public List<Alquiler> getUltimosAlquileres(int limit) {
        return alquilerRepository.findTop10ByOrderByIdDesc();
    }

    public boolean isCarAvailable(Long carId, String startDate, String endDate) {
        try {
            List<Alquiler> existingRentals = alquilerRepository.findByCocheId(carId);
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // comprobamos que las fechas de los alquileres sean validas
            if (start.isAfter(end)) {
                return false;
            }

            // comprobamos los alquileres
            for (Alquiler rental : existingRentals) {
                try {
                    LocalDate rentalStart = LocalDate.parse(rental.getFecha_inicio());
                    LocalDate rentalEnd = LocalDate.parse(rental.getFecha_fin());

                    // comprobamos que no se solapen
                    boolean overlap = !(end.isBefore(rentalStart) || start.isAfter(rentalEnd));
                    if (overlap) {
                        return false;
                    }
                } catch (Exception e) {
                    // se salta el alquiler si hay algun error en las fechas
                    continue;
                }
            }
            return true;
        } catch (Exception e) {
            // si hay algun error en las fechas se dice que el coche no esta disponible
            return false;
        }
    }

    
}
