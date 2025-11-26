package ar.edu.huergo.tombers.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.tombers.dto.DTOPRequest;
import lombok.RequiredArgsConstructor;
import ar.edu.huergo.tombers.dto.DTOPResponse;
import ar.edu.huergo.tombers.mapper.ProductoMapper;
import ar.edu.huergo.tombers.entity.Producto;
import ar.edu.huergo.tombers.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(Long id) throws EntityNotFoundException {
        return productoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado"));
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, DTOPRequest request) throws EntityNotFoundException {
        Producto productoExistente = obtenerProductoPorId(id);
        productoExistente.setNombre(request.getNombre());
        productoExistente.setCategoria(request.getCategoria());
        productoExistente.setPrecio(request.getPrecio());
        return productoRepository.save(productoExistente);
    }

    public void eliminarProducto(Long id) throws EntityNotFoundException {
        Producto producto = obtenerProductoPorId(id);
        productoRepository.delete(producto);
    }

}


