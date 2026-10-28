package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoMenu;

public class PedidoTest{
    private Pedido pedido;
    private ProductoMenu corral;
    private File factura;

    @BeforeEach
    void setUp() throws Exception{ corral= new ProductoMenu("corral",14000);
    pedido= new Pedido("Tulio","Calle 77");}

    @AfterEach
    void tearDown() throws Exception{ if(factura != null && factura.exists())factura.delete();}

    @Test
    void testGetIdPedido(){
        Pedido otro= new Pedido("Ana","Cra 2");
        assertEquals(pedido.getIdPedido() + 1, otro.getIdPedido(), "El id del pedidio debe incrementar cuando se agrega otro pedido");
    }

    @Test 
    void testGetNombreCliente(){
        assertEquals("Tulio", pedido.getNombreCliente(), "El nombre del cliente no es el esperado");
    }

    @Test
    void testPrecioTotalVacio(){
        assertEquals(0, pedido.getPrecioTotalPedido(), "Un pedido sin productos cuesta 0");
    }

    @Test
    void testGenerarTextoFactura(){
        pedido.agregarProducto(corral);
        String esperado= "Cliente: Tulio\n" + "Dirección: Calle 77\n" + "----------------\n" + "corral\n" + "            14000\n" + "----------------\n" + "Precio Neto:  14000\n" + "IVA:          2660\n" + "Precio Total: 16660\n";
        assertEquals(esperado, pedido.generarTextoFactura(), "La factura no coincide con lo esperado");
    }

    @Test
    void testPrecioTotalConProducto(){
        pedido.agregarProducto(corral);
        assertEquals(16660, pedido.getPrecioTotalPedido(), "el precio total debe incluir la IVA");
    }

    @Test
    void testGuardarFactura() throws Exception{
        pedido.agregarProducto(corral);
        factura = new File("factura_test.txt");
        pedido.guardarFactura(factura);
        assertEquals(pedido.generarTextoFactura(), read(factura), "La factura no quedo guardada en el archivo txt");
    }

    @Test
    void testGuardarFacturaDirectorioInexistente(){
        File archivo = new File("directorio_que_no_existe/factura.txt");
        assertThrows( FileNotFoundException.class,() -> pedido.guardarFactura(archivo));
    }

    private String read(File archivo) throws Exception{
        BufferedReader reader= new BufferedReader(new FileReader(archivo));
        StringBuilder sb = new StringBuilder();
        try{
            String l1= reader.readLine();
            while(l1!=null)
            {sb.append(l1);
            sb.append("\n");
            l1 = reader.readLine();
            }
        }
        finally{
            reader.close();
        }
        return sb.toString();
    }
}