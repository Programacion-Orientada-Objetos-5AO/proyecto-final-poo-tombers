package ar.edu.huergo.tombers.service;


import ar.edu.huergo.tombers.entity.Prestamo;

import java.util.List;

public interface PrestamoService {

    Prestamo savePrestamo(Prestamo prestamoId, Prestamo libroId, Prestamo nombre, Prestamo fechaPrestamo, Prestamo fechaDevolucion);

    List<Prestamo> fetchPrestamoList();

    Prestamo updatePrestamo(Prestamo prestamoId, Prestamo devuelto);

    void deletePrestamoById(Prestamo prestamoId);
}
