package ar.edu.huergo.tombers.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;


import ar.edu.huergo.tombers.dto.DTOPRequest;
import ar.edu.huergo.tombers.dto.DTOPResponse;
import ar.edu.huergo.tombers.dto.DTOPResumenCategoria;
import ar.edu.huergo.tombers.service.ProductoService;
import ar.edu.huergo.tombers.repository.ProductoRepository;
import ar.edu.huergo.tombers.mapper.ProductoMapper;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import ar.edu.huergo.tombers.entity.Producto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;





@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
public class ProductoController {


    private final ProductoService productoService;
    private final ProductoMapper productoMapper;
    private final ProductoRepository productoRepository;

    @PostMapping("/crear")
    public DTOPResponse crearProducto(@Valid @RequestBody DTOPRequest request) {
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setCategoria(request.getCategoria());
        producto.setPrecio(request.getPrecio());
        producto.setStock(0);

        productoService.crearProducto(producto);

        DTOPResponse respuesta = productoMapper.toDto(producto);
        return respuesta;
    }

    @GetMapping("/productos")
    public List<Producto> verProductos() {
            List<Producto> listaProductos = productoRepository.findAll();
        return listaProductos;
    }

    @PutMapping("/actualizarProducto/{id}")
    public String actualizarProducto( @Valid @PathVariable Long id , @RequestBody DTOPRequest request) {
        productoService.actualizarProducto(id , request);
        return "Producto editado correctamente";
    }

    @DeleteMapping("eliminarProducto/{id}")
    public String eliminarProducto(@Valid @PathVariable Long id) {
        productoService.eliminarProducto(id);
        return "Producto eliminado exitosamente";
    }

    @GetMapping("categoria/{categoria}")
    public List<DTOPResponse> mostrarProductosPorCategoria(@Valid @PathVariable String categoria) {
        List<Producto> listaProductos = productoService.obtenerProductosPorCategoria(categoria);
        return productoMapper.toDtoList(listaProductos);
    }

    @PatchMapping("actualizarStock/{id}")
    public String actualizarStock(@Valid @PathVariable Long id , @RequestBody Integer stock){
        productoService.actualizarStock(id, stock);
        return "Stock actualizado correctamente.";
    }
    
    @GetMapping("resumenCategoria/{categoria}")
    public DTOPResumenCategoria mostrarResumenCategoria(@Valid @PathVariable String categoria) {
        DTOPResumenCategoria resumen = productoService.generarResumenCategoria(categoria);
        return resumen;
    }
    
}
