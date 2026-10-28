package uniandes.dpoo.hamburguesas.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uniandes.dpoo.hamburguesas.excepciones.IngredienteRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.NoHayPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoFaltanteException;
import uniandes.dpoo.hamburguesas.excepciones.ProductoRepetidoException;
import uniandes.dpoo.hamburguesas.excepciones.YaHayUnPedidoEnCursoException;
import uniandes.dpoo.hamburguesas.mundo.Ingrediente;
import uniandes.dpoo.hamburguesas.mundo.Pedido;
import uniandes.dpoo.hamburguesas.mundo.ProductoAjustado;
import uniandes.dpoo.hamburguesas.mundo.Restaurante;

public class RestauranteTest
{
    private Restaurante restaurante;
    private File ingredientesOk;
    private File menuOk;
    private File combosOk;
    private File factura;

    @BeforeEach
    void setUp( ) throws Exception
    {
        restaurante = new Restaurante( );
        ingredientesOk = new File( "tests/data/ingredientes_ok.txt" );
        menuOk = new File( "tests/data/menu_ok.txt" );
        combosOk = new File( "tests/data/combos_ok.txt" );
        new File( "facturas" ).mkdirs( );
    }

    @AfterEach
    void tearDown( ) throws Exception
    {
        if( factura != null && factura.exists( ) )
            factura.delete( );
    }

    @Test
    void testCargarInformacion( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, combosOk );
        assertEquals( 2, restaurante.getIngredientes( ).size( ), "Deben cargarse dos ingredientes." );
        assertEquals( "lechuga", restaurante.getIngredientes( ).get( 0 ).getNombre( ), "El primer ingrediente no es el esperado." );
        assertEquals( 2, restaurante.getMenuBase( ).size( ), "Deben cargarse dos productos base." );
        assertEquals( 1, restaurante.getMenuCombos( ).size( ), "Debe cargarse un combo." );
        assertEquals( "combo corral", restaurante.getMenuCombos( ).get( 0 ).getNombre( ), "El nombre del combo no es el esperado." );
        assertEquals( 17550, restaurante.getMenuCombos( ).get( 0 ).getPrecio( ), "El combo debe costar el 90% de la suma." );
    }

    @Test
    void testIngredienteRepetido( )
    {
        File archivo = new File( "tests/data/ingredientes_repetido.txt" );
        IngredienteRepetidoException ex = assertThrows( IngredienteRepetidoException.class, ( ) -> restaurante.cargarInformacionRestaurante( archivo, menuOk, combosOk ) );
        assertEquals( "El ingrediente tomate está repetido", ex.getMessage( ), "El mensaje de la excepción no es el esperado." );
    }

    @Test
    void testProductoRepetido( )
    {
        File archivo = new File( "tests/data/menu_repetido.txt" );
        ProductoRepetidoException ex = assertThrows( ProductoRepetidoException.class, ( ) -> restaurante.cargarInformacionRestaurante( ingredientesOk, archivo, combosOk ) );
        assertEquals( "El producto corral está repetido", ex.getMessage( ), "El mensaje de la excepción no es el esperado." );
    }

    @Test
    void testComboRepetido( )
    {
        File archivo = new File( "tests/data/combos_repetido.txt" );
        ProductoRepetidoException ex = assertThrows( ProductoRepetidoException.class, ( ) -> restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, archivo ) );
        assertEquals( "El producto combo corral está repetido", ex.getMessage( ), "El mensaje de la excepción no es el esperado." );
    }

    @Test
    void testProductoFaltante( )
    {
        File archivo = new File( "tests/data/combos_faltante.txt" );
        ProductoFaltanteException ex = assertThrows( ProductoFaltanteException.class, ( ) -> restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, archivo ) );
        assertEquals( "El producto helado no aparece en la información del restaurante", ex.getMessage( ), "El mensaje de la excepción no es el esperado." );
    }

    @Test
    void testArchivoInexistente( )
    {
        File archivo = new File( "tests/data/no_existe.txt" );
        assertThrows( FileNotFoundException.class, ( ) -> restaurante.cargarInformacionRestaurante( archivo, menuOk, combosOk ) );
    }

    @Test
    void testPrecioNoNumerico( )
    {
        File archivo = new File( "tests/data/menu_x_ok.txt" );
        assertThrows( NumberFormatException.class, ( ) -> restaurante.cargarInformacionRestaurante( ingredientesOk, archivo, combosOk ) );
    }

    @Test
    void testIniciarPedido( ) throws Exception
    {
        restaurante.iniciarPedido( "Ana", "Calle 1" );
        Pedido pedido = restaurante.getPedidoEnCurso( );
        assertEquals( "Ana", pedido.getNombreCliente( ), "El pedido en curso no es el del cliente." );
        assertEquals( 0, restaurante.getPedidos( ).size( ), "Un pedido en curso todavía no está cerrado." );
    }

    @Test
    void testYaHayUnPedidoEnCurso( ) throws Exception
    {
        restaurante.iniciarPedido( "Ana", "Calle 1" );
        YaHayUnPedidoEnCursoException ex = assertThrows( YaHayUnPedidoEnCursoException.class, ( ) -> restaurante.iniciarPedido( "Luis", "Cra 2" ) );
        assertEquals( "Ya existe un pedido en curso, para el cliente Ana así que no se puede crear un pedido para Luis", ex.getMessage( ), "El mensaje de la excepción no es el esperado." );
    }

    @Test
    void testCerrarSinPedido( )
    {
        NoHayPedidoEnCursoException ex = assertThrows( NoHayPedidoEnCursoException.class, ( ) -> restaurante.cerrarYGuardarPedido( ) );
        assertEquals( "Actualmente no hay un pedido en curso", ex.getMessage( ), "El mensaje de la excepción no es el esperado." );
    }

    @Test
    void testCerrarYGuardarPedido( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, combosOk );
        restaurante.iniciarPedido( "Ana", "Calle 1" );
        Pedido pedido = restaurante.getPedidoEnCurso( );
        pedido.agregarProducto( restaurante.getMenuBase( ).get( 0 ) );
        int id = pedido.getIdPedido( );
        restaurante.cerrarYGuardarPedido( );
        factura = new File( "facturas/factura_" + id + ".txt" );
        assertTrue( factura.exists( ), "La factura debe guardarse con el id del pedido." );
        assertEquals( pedido.generarTextoFactura( ), leer( factura ), "El archivo no guarda el texto de la factura." );
        assertNull( restaurante.getPedidoEnCurso( ), "Después de cerrar no hay pedido en curso." );
        assertEquals( 1, restaurante.getPedidos( ).size( ), "El pedido cerrado debe quedar en el histórico." );
        assertEquals( pedido, restaurante.getPedidos( ).get( 0 ), "El histórico debe contener el pedido cerrado." );
    }

    @Test
    void testCerrarConComboYAjuste( ) throws Exception
    {
        restaurante.cargarInformacionRestaurante( ingredientesOk, menuOk, combosOk );
        restaurante.iniciarPedido( "Ana", "Calle 1" );
        Pedido pedido = restaurante.getPedidoEnCurso( );
        pedido.agregarProducto( restaurante.getMenuCombos( ).get( 0 ) );
        ProductoAjustado ajustado = new ProductoAjustado( restaurante.getMenuBase( ).get( 0 ) );
        Ingrediente lechuga = restaurante.getIngredientes( ).get( 0 );
        ajustado.agregarIngrediente( lechuga );
        ajustado.eliminarIngrediente( new Ingrediente( "tomate", 1000 ) );
        pedido.agregarProducto( ajustado );
        assertEquals( 17550, restaurante.getMenuCombos( ).get( 0 ).getPrecio( ), "El combo debe costar el 90%." );
        assertEquals( 15000, ajustado.getPrecio( ), "El ajuste suma la lechuga y no resta el tomate." );
        int id = pedido.getIdPedido( );
        restaurante.cerrarYGuardarPedido( );
        factura = new File( "facturas/factura_" + id + ".txt" );
        assertEquals( pedido.generarTextoFactura( ), leer( factura ), "La factura del pedido no es la esperada." );
        assertEquals( 38734, pedido.getPrecioTotalPedido( ), "El total debe sumar el combo con descuento, el ajuste y el IVA." );
        assertEquals( 1, restaurante.getPedidos( ).size( ), "El pedido cerrado debe quedar en el histórico." );
    }

    private String leer( File archivo ) throws Exception
    {
        BufferedReader reader = new BufferedReader( new FileReader( archivo ) );
        StringBuilder sb = new StringBuilder( );
        try
        {
            String linea = reader.readLine( );
            while( linea != null )
            {
                sb.append( linea );
                sb.append( "\n" );
                linea = reader.readLine( );
            }
        }
        finally
        {
            reader.close( );
        }
        return sb.toString( );
    }
}