package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Combo;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ComboTest{
    private ArrayList<ProductoMenu> items;

    @BeforeEach
    void setUp() throws Exception{ items=new ArrayList<ProductoMenu>();
    items.add(new ProductoMenu("corral",14000));
    items.add(new ProductoMenu("papas medianas",5500));}

    @AfterEach
    void tearDown() throws Exception{}

    @Test
    void testGetNombre(){ 
        Combo combo= new Combo("combo corral", 0.1,items);
    assertEquals("combo corral", combo.getNombre(), "El nombre del ombo no es el esperado");}

    @Test
    void testGetPrecioSinDescuento(){
        Combo combo= new Combo("combo corral", 0, items);
    assertEquals(19500, combo.getPrecio(), "Sin descuento se tiene que pagar la suma de los productos");}

    @Test
    void testGetPrecioDiezPorciento(){
        Combo combo=new Combo("combo corral", 0.1, items);
        assertEquals(17550,combo.getPrecio(), "Un descuento del 10% deberia dejar el 90% del precio total");
    }
    
    @Test
    void testgetPrecioDescuentoTotal(){
        Combo combo= new Combo("combo corral", 1, items);
        assertEquals(0, combo.getPrecio(), "Un descuento del 100% retorna 0");
    }

    @Test
    void testGetPrecioSinProductos(){
        Combo combo= new Combo("vacio",0.1,new ArrayList<ProductoMenu>());
        assertEquals(0,combo.getPrecio(), "Un combo sin comida tiene precio de 0");
    }

    @Test
    void testGenerarTextoFactura(){
        Combo combo= new Combo("combo corral",0.1,items);
        String esperado= "Combo combo corral\n"+" Descuento: 0.1\n"+"            17550\n";
        assertEquals(esperado, combo.generarTextoFactura(), "La factura no coincide con lo esperado");
    }
}