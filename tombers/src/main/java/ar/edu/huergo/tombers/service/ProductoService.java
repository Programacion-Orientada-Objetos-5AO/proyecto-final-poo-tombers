package ar.edu.huergo.tombers.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.huergo.tombers.dto.DTOPRequest;
import lombok.RequiredArgsConstructor;
import ar.edu.huergo.tombers.dto.DTOPResponse;
import ar.edu.huergo.tombers.dto.DTOPResumenCategoria;
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

    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        return productoRepository.findByCategoriaContainingIgnoreCase(categoria);
    }

    public void actualizarStock(Long id, Integer nuevoStock){
        Producto producto = obtenerProductoPorId(id);
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }

    public DTOPResumenCategoria generarResumenCategoria(String categoria) {
        DTOPResumenCategoria resumen = new DTOPResumenCategoria(null , null , null , null , null, null);
        List<Producto> listaProductos = obtenerProductosPorCategoria(categoria);
        Integer totalProductos = listaProductos.size();
        Double valorTotalInventario = 0.0;
        String productoMinimo = "Ninguno";
        Double minimo = 99999999999999.0;
        Double maximo = 0.0;
        String productoMaximo = "Ninguno";
        Double sumaPrecios = 0.0;
        Double contador = 0.0;
        for (Producto producto : listaProductos){
            String nombreProducto = producto.getNombre();
            Double precioProducto = producto.getPrecio();
            Double valorProducto = precioProducto * producto.getStock();

            contador = contador + 1;
            sumaPrecios = sumaPrecios + precioProducto;

            if (precioProducto < minimo){
                minimo = precioProducto;
                productoMinimo = nombreProducto;
            }

            if (precioProducto >= maximo){
                maximo = precioProducto;
                productoMaximo = nombreProducto;
            }

            valorTotalInventario += valorProducto;
        }
        Double precioPromedio = 0.0;
        if (contador != 0){
            precioPromedio = sumaPrecios / contador;
        }

        resumen.setNombreCategoria(categoria);
        resumen.setTotalProductos(totalProductos);
        resumen.setValorTotalInventario(valorTotalInventario);
        resumen.setProductoMasCaro(productoMaximo);
        resumen.setProductoMasBarato(productoMinimo);
        resumen.setPromedioPrecios(precioPromedio);

        return resumen;
    }
}


