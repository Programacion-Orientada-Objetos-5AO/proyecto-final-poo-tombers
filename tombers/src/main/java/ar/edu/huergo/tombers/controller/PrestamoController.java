package ar.edu.huergo.tombers.controller;



import ar.edu.huergo.tombers.entity.Prestamo;
import ar.edu.huergo.tombers.service.PrestamoService;

import java.util.List;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController

public class PrestamoController {

    @Autowired private PrestamoService PrestamoService;

    @PostMapping("/prestamos")
    public Prestamo savePrestamo(
        @Valid @RequestBody Prestamo prestamoId, Prestamo libroId, Prestamo nombre, Prestamo fechaPrestamo, Prestamo fechaDevolucion)
    {
        return PrestamoService.savePrestamo(prestamoId, libroId, nombre ,fechaPrestamo, fechaDevolucion);
    }

    @GetMapping("/prestamos")
    public List<Prestamo> fetchPrestamoList()
    {
        return PrestamoService.fetchPrestamoList();
    }

    @PutMapping("/prestamos/{id}")
    public Prestamo
    updatePrestamo(@RequestBody Prestamo devuelto,
                     @PathVariable("id") Prestamo prestamoId)
    {
        return PrestamoService.updatePrestamo(
            devuelto, prestamoId);
    }

    @DeleteMapping("/prestamos/{id}")
    public String deletePrestamoById(@PathVariable("id") Prestamo prestamoId)
    {
        PrestamoService.deletePrestamoById(prestamoId);
        return "Prestamo borrado";
    }
}
