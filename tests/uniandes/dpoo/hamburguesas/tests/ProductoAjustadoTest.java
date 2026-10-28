package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class ProductoAjustadoTest{
    private ProductoMenu base;
    private ProductoAjustado ajustado;
    private Ingrediente I1;
    private Ingrediente I2;

    @BeforeEach
    void setUp() throws Exception{ base= new ProductoMenu("corral",14000);
    ajustado= new ProductoAjustado(base);
    I1= new Ingrediente("queso mozzarella",2500);
    I2= new Ingrediente("tomate", 1000);}

    @AfterEach
    void tearDown() throws Exception{}

    @Test
    void testGetNombre(){ assertEquals("corral", ajustado.getNombre(), "El nombre del producto ajustadono es igual al esperado");}

    @Test
    void testGetPrecioConAgregadoYEliminado(){
        ajustado.agregarIngrediente(I1);
        ajustado.eliminarIngrediente(I2);
        assertEquals(16500,ajustado.getPrecio(), "Quitar un ingrediente no puede cambiar el precio");
    }

    @Test
    void testGetPrecioIngredienteRepetido(){
        ajustado.agregarIngrediente(I1);
        ajustado.agregarIngrediente(I1);
        assertEquals(19000,ajustado.getPrecio(), "El mismo ingrediente puede cambiar el precio varias veces");
    }

    @Test
    void testGenerarTextoFacturaSinAjustes(){ String esperado="corral\n"+"            14000\n"+"            14000\n";
    assertEquals(esperado, ajustado.generarTextoFactura(), "La factura sin ajustes no el la esperada");}

    @Test
    void testGenerarTextoFacturaConAjustes(){ ajustado.agregarIngrediente(I1);
    ajustado.eliminarIngrediente(I2);
    String esperado="corral\n"+"            14000\n"+"    +queso mozzarella"+"                2500"+"    -tomate"+"            16500\n";
    assertEquals(esperado,ajustado.generarTextoFactura(), "La factura con ajustes no es la esperada");}
}