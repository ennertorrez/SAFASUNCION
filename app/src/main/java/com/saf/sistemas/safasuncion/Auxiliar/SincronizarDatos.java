package com.saf.sistemas.safasuncion.Auxiliar;

import android.app.Activity;
import android.util.Log;

import com.google.gson.Gson;
import com.saf.sistemas.safasuncion.AccesoDatos.ArticulosHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.CategoriasClienteHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.ClientesHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.ClientesSucursalHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.ConfiguracionSistemaHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.DataBaseOpenHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.EscalaPreciosHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.FormaPagoHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.PedidosDetalleHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.PedidosHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.PromocionesHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.RutasHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.TPreciosHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.UsuariosHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.VendedoresHelper;
import com.saf.sistemas.safasuncion.Entidades.Cliente;
import com.saf.sistemas.safasuncion.Entidades.Vendedor;
import com.saf.sistemas.safasuncion.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static com.saf.sistemas.safasuncion.Auxiliar.Funciones.Codificar;

/**
 * Created by usuario on 5/5/2017.
 */

public class SincronizarDatos {

    private String urlClientes = variables_publicas.direccionIp + "/ServicioClientes.svc/BuscarClientes";
    private String urlDptoMuniBarrios = variables_publicas.direccionIp + "/ServicioClientes.svc/ObtenerDptoMuniBarrios";
    private String urlRutas = variables_publicas.direccionIp + "/ServicioClientes.svc/GetRutas/";
    private String urlArticulos = variables_publicas.direccionIp + "/ServicioTotalArticulos.svc/BuscarTotalArticulo/";
    final String urlVendedores = variables_publicas.direccionIp + "/ServicioPedidos.svc/ListaVendedores/";
    final String urlPromociones = variables_publicas.direccionIp + "/ServicioPedidos.svc/GetPromociones/";
    final String urlFormasPago = variables_publicas.direccionIp + "/ServicioPedidos.svc/FormasPago/";
    final String urlGetConfiguraciones = variables_publicas.direccionIp + "/ServicioPedidos.svc/GetConfiguraciones/";
    final String urlGetClienteSucursales = variables_publicas.direccionIp + "/ServicioPedidos.svc/GetClienteSucursales/";
    final String url = variables_publicas.direccionIp + "/ServicioLogin.svc/BuscarUsuario/";
    static final String urlConsultarExistencias = variables_publicas.direccionIp + "/ServicioPedidos.svc/ObtenerInventarioArticulo/";
    final String urlGetCategorias = variables_publicas.direccionIp + "/ServicioClientes.svc/GetListaCategorias";
    static final String urlPrecios = variables_publicas.direccionIp + "/ServicioPedidos.svc/GetPreciosArticulos/1";
    final String urlEscalaPrecios = variables_publicas.direccionIp + "/ServicioPedidos.svc/GetEscalaPrecios";
    private String TAG = SincronizarDatos.class.getSimpleName();
    private DataBaseOpenHelper DbOpenHelper;
    private ClientesHelper ClientesH;
    private VendedoresHelper VendedoresH;
    private ArticulosHelper ArticulosH;
    private UsuariosHelper UsuariosH;
    private PedidosHelper PedidosH;
    private PedidosDetalleHelper PedidosDetalleH;
    private RutasHelper RutasH;
    private CategoriasClienteHelper CategoriaH;
    private PromocionesHelper PromocionesH;
    private FormaPagoHelper FormaPagoH;
    private ConfiguracionSistemaHelper ConfigSistemasH;
    private ClientesSucursalHelper ClientesSucH;
    private TPreciosHelper TPreciosH;
    private EscalaPreciosHelper EscalaPreciosH;

    public SincronizarDatos(DataBaseOpenHelper dbh, ClientesHelper Clientesh,
                            VendedoresHelper Vendedoresh, PromocionesHelper Promocionesh, FormaPagoHelper FormaPagoh,
                            ConfiguracionSistemaHelper ConfigSistemah,
                            ClientesSucursalHelper ClientesSuch, ArticulosHelper Articulosh, UsuariosHelper usuariosH,
                            PedidosHelper pedidoH, PedidosDetalleHelper pedidosDetalleH ,TPreciosHelper tpreciosH,RutasHelper rutasH,EscalaPreciosHelper escalaPreciosH) {
        DbOpenHelper = dbh;
        ClientesH = Clientesh;
        VendedoresH = Vendedoresh;
        PromocionesH = Promocionesh;
        FormaPagoH = FormaPagoh;
        ConfigSistemasH = ConfigSistemah;
        ClientesSucH = ClientesSuch;
        ArticulosH = Articulosh;
        UsuariosH = usuariosH;
        PedidosH = pedidoH;
        PedidosDetalleH = pedidosDetalleH;
        RutasH = rutasH;
        TPreciosH = tpreciosH;
        EscalaPreciosH = escalaPreciosH;
    }

    public SincronizarDatos(DataBaseOpenHelper dbh, ClientesHelper Clientesh,
                            VendedoresHelper Vendedoresh, PromocionesHelper Promocionesh, FormaPagoHelper FormaPagoh,
                            ConfiguracionSistemaHelper ConfigSistemah,
                            ClientesSucursalHelper ClientesSuch, ArticulosHelper Articulosh, UsuariosHelper usuariosH,
                            PedidosHelper pedidoH, PedidosDetalleHelper pedidosDetalleH,
                            CategoriasClienteHelper categoriasH,TPreciosHelper tpreciosH,RutasHelper rutasH,EscalaPreciosHelper escalaPreciosH) {
        DbOpenHelper = dbh;
        ClientesH = Clientesh;
        VendedoresH = Vendedoresh;
        PromocionesH = Promocionesh;
        FormaPagoH = FormaPagoh;
        ConfigSistemasH = ConfigSistemah;
        ClientesSucH = ClientesSuch;
        ArticulosH = Articulosh;
        UsuariosH = usuariosH;
        PedidosH = pedidoH;
        PedidosDetalleH = pedidosDetalleH;
        TPreciosH = tpreciosH;
        CategoriaH=categoriasH;
        RutasH =rutasH;
        EscalaPreciosH = escalaPreciosH;
    }

    public SincronizarDatos(DataBaseOpenHelper dbh , ClientesHelper Clientesh, CategoriasClienteHelper Categoriah,TPreciosHelper tpreciosH,RutasHelper rutasH) {
        DbOpenHelper = dbh;
        ClientesH = Clientesh;
        CategoriaH = Categoriah;
        TPreciosH=tpreciosH;
        RutasH= rutasH;
    }

    public SincronizarDatos(DataBaseOpenHelper dbh, ClientesHelper Clientesh, RutasHelper rutasH) {
        DbOpenHelper = dbh;
        ClientesH = Clientesh;
        RutasH= rutasH;
    }

    private boolean SincronizarArticulos() throws JSONException {
        HttpHandler shC = new HttpHandler();
        String urlStringC = urlArticulos + variables_publicas.rutacargada + "/" + variables_publicas.usuario.getEmpresa_ID();
        String jsonStrC = shC.makeServiceCall(urlStringC);

        if (jsonStrC == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar Articulos, Respuesta nula GET", variables_publicas.info + urlStringC, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        //Log.e(TAG, "Response from url: " + jsonStrC);
        DbOpenHelper.database.beginTransaction();

        ArticulosH.EliminaArticulos();
        JSONObject jsonObjC = new JSONObject(jsonStrC);
        // Getting JSON Array node
        JSONArray articulos = jsonObjC.getJSONArray("BuscarTotalArticuloResult");

        try {
            // looping through All Contacts
            for (int i = 0; i < articulos.length(); i++) {
                JSONObject c = articulos.getJSONObject(i);

                String Codigo = c.getString("CODIGO_ARTICULO");
                String Nombre = c.getString("NOMBRE");
                String COSTO = c.getString("COSTO");
                String UNIDAD = c.getString("UNIDAD");
                String UnidadCaja = c.getString("UnidadCaja");
                String Precio = c.getString("Precio");
                String Precio2 = c.getString("Precio2");
                String Precio3 = c.getString("Precio3");
                String Precio4 = c.getString("Precio4");
                String CodUM = c.getString("codUM");
                String PorIVA = c.getString("PorIVA");
                String DESCUENTO_MAXIMO = c.getString("DESCUENTO_MAXIMO");
                String existencia = c.getString("Existencia");
                String UnidadCajaVenta = c.getString("UnidadCajaVenta");
                String UnidadCajaVenta2 = c.getString("UnidadCajaVenta2");
                String UnidadCajaVenta3 = c.getString("UnidadCajaVenta3");
                String IdProveedor = c.getString("IdProveedor");
                String Escala = c.getString("Escala");
                ArticulosH.GuardarTotalArticulos(Codigo, Nombre, COSTO, UNIDAD, UnidadCaja, Precio,Precio2,Precio3,Precio4,CodUM, PorIVA, DESCUENTO_MAXIMO,  existencia, UnidadCajaVenta,UnidadCajaVenta2,UnidadCajaVenta3, IdProveedor,Escala);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar Articulos, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }

   /* private boolean SincronizarPrecios() throws JSONException {
        HttpHandler shC = new HttpHandler();
        String urlStringC = urlPrecios;
        String jsonStrC = shC.makeServiceCall(urlStringC);

        if (jsonStrC == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar Precios, Respuesta nula GET", variables_publicas.info + urlStringC, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        //Log.e(TAG, "Response from url: " + jsonStrC);
        DbOpenHelper.database.beginTransaction();

        PreciosH.EliminaPrecios();
        JSONObject jsonObjC = new JSONObject(jsonStrC);
        // Getting JSON Array node
        JSONArray precios = jsonObjC.getJSONArray("GetPreciosArticulosResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < precios.length(); i++) {
                JSONObject c = precios.getJSONObject(i);

                String Empresa = c.getString("EMPRESA");
                String Codigo = c.getString("CODIGO");
                String CodTipoPrecio = c.getString("COD_TIPO_PRECIO");
                String TipoPrecio = c.getString("TIPO_PRECIO");
                String CodUM = c.getString("COD_UM");
                String um = c.getString("UM");
                String Unidades = c.getString("UNIDADES");
                String Monto = c.getString("MONTO");
                PreciosH.GuardarPrecios(Empresa, Codigo, CodTipoPrecio, TipoPrecio, CodUM, um, Unidades, Monto);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar Precios, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }
*/
   private boolean SincronizarTPrecios() throws JSONException {
       HttpHandler shC = new HttpHandler();
       String urlStringC = urlPrecios;
       String jsonStrC = shC.makeServiceCall(urlStringC);

       if (jsonStrC == null) {
           new Funciones().SendMail("Ha ocurrido un error al sincronizar TiposPrecios, Respuesta nula GET", variables_publicas.info + urlStringC, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
           return false;
       }
       //Log.e(TAG, "Response from url: " + jsonStrC);
       DbOpenHelper.database.beginTransaction();

       TPreciosH.EliminaTPrecios();
       JSONObject jsonObjC = new JSONObject(jsonStrC);
       // Getting JSON Array node
       JSONArray precios = jsonObjC.getJSONArray("GetPreciosArticulosResult");


       try {
           // looping through All Contacts
           for (int i = 0; i < precios.length(); i++) {
               JSONObject c = precios.getJSONObject(i);

               String CodTipoPrecio = c.getString("COD_TIPO_PRECIO");
               String TipoPrecio = c.getString("TIPO_PRECIO");
               TPreciosH.GuardarTPrecios(CodTipoPrecio, TipoPrecio);
           }
           DbOpenHelper.database.setTransactionSuccessful();
           return true;
       } catch (Exception ex) {
           new Funciones().SendMail("Ha ocurrido un error al sincronizar TiposPrecios, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
           return false;
       } finally {
           DbOpenHelper.database.endTransaction();
       }

   }

    private boolean SincronizarEscalaPrecios() throws JSONException {
        HttpHandler shC = new HttpHandler();
        String urlStringC = urlEscalaPrecios;
        String jsonStrC = shC.makeServiceCall(urlStringC);

        if (jsonStrC == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar las Escalas de Precios Combinados, Respuesta nula GET", variables_publicas.info + urlStringC, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        //Log.e(TAG, "Response from url: " + jsonStrC);
        DbOpenHelper.database.beginTransaction();

        EscalaPreciosH.EliminaEscalaPrecios();
        JSONObject jsonObjC = new JSONObject(jsonStrC);
        // Getting JSON Array node
        JSONArray precios = jsonObjC.getJSONArray("GetEscalaPreciosResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < precios.length(); i++) {
                JSONObject c = precios.getJSONObject(i);

                String codEscala = c.getString("CodEscala");
                String lista = c.getString("ListaArticulos");
                String escala1 = c.getString("Escala1");
                String escala2 = c.getString("Escala2");
                String escala3 = c.getString("Escala3");
                String precio1 = c.getString("Precio1");
                String precio2 = c.getString("Precio2");
                String precio3 = c.getString("Precio3");
                EscalaPreciosH.GuardarEscalaPrecios(codEscala,lista,escala1,escala2,escala3,precio1,precio2,precio3);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar las Escalas de Precios Combinados, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }

    private boolean SincronizarCategorias() throws JSONException {
        HttpHandler shC = new HttpHandler();
        String urlStringC = urlGetCategorias;
        String jsonStrC = shC.makeServiceCall(urlStringC);

        if (jsonStrC == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar lista de Categorias, Respuesta nula GET", variables_publicas.info + urlStringC, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        //Log.e(TAG, "Response from url: " + jsonStrC);
        DbOpenHelper.database.beginTransaction();

        CategoriaH.EliminarCategoria();
        JSONObject jsonObjC = new JSONObject(jsonStrC);
        // Getting JSON Array node
        JSONArray precios = jsonObjC.getJSONArray("GetListaCategoriasResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < precios.length(); i++) {
                JSONObject c = precios.getJSONObject(i);

                String Codigo = c.getString("Cod_Cat");
                String Categoria = c.getString("Categoria");
                CategoriaH.GuardarCategoria(Codigo, Categoria);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar lista de Categorias, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }
    //Cliente
    public boolean SincronizarClientes() throws JSONException {
        /*******************************CLIENTES******************************/
        //************CLIENTES
        DbOpenHelper.database.beginTransaction();

        ObtenerDptosMuniBarrios();
        String urlStringC="";
        HttpHandler shC = new HttpHandler();
        if (variables_publicas.usuario.getTipo().equals("Supervisor") || variables_publicas.usuario.getTipo().equals("User")){
            urlStringC  = urlClientes + "/" + variables_publicas.usuario.getCodigo() + "/" + variables_publicas.rutacargada + "/" + 4;
        }else if (variables_publicas.usuario.getTipo().equals("Cliente")){
            urlStringC = urlClientes + "/" + variables_publicas.usuario.getCodigo()  + "/" + variables_publicas.usuario.getCodigo() + "/" + 5;
        }else{
            urlStringC = urlClientes + "/" + variables_publicas.rutacargada + "/" + variables_publicas.usuario.getCodigo() + "/" + 3;
        }
        String jsonStrC = shC.makeServiceCall(urlStringC);

        if (jsonStrC == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar clientes, Respuesta nula GET", variables_publicas.info + urlStringC, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        //Log.e(TAG, "Response from url: " + jsonStrC);

        ClientesH.EliminaClientes();
        JSONObject jsonObjC = new JSONObject(jsonStrC);
        // Getting JSON Array node
        JSONArray clientes = jsonObjC.getJSONArray("BuscarClientesResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < clientes.length(); i++) {
                JSONObject c = clientes.getJSONObject(i);

                String IdCliente = c.getString("IdCliente");
                String Nombre = c.getString("Nombre");
                String FechaCreacion = c.getString("FechaCreacion");
                String Telefono = c.getString("Telefono");
                String Direccion = c.getString("Direccion");
                String IdDepartamento = c.getString("IdDepartamento");
                String IdMunicipio = c.getString("IdMunicipio");
                String Ciudad = c.getString("Ciudad");
                String Ruc = c.getString("Ruc");
                String Cedula = c.getString("Cedula");
                String LimiteCredito = c.getString("LimiteCredito");
                String IdFormaPago = c.getString("IdFormaPago");
                String IdVendedor = c.getString("IdVendedor");
                String Excento = c.getString("Excento");
                String CodigoLetra = c.getString("CodigoLetra");
                String Ruta = c.getString("Ruta");
                String NombreRuta = c.getString("NombreRuta");
                String Frecuencia = c.getString("Frecuencia");
                String PrecioEspecial = c.getString("PrecioEspecial");
                String FechaUltimaCompra = c.getString("FechaUltimaCompra");
                String Tipo = c.getString("Tipo");
                String TipoPrecio = c.getString("TipoPrecio");
                String Descuento = c.getString("Descuento");
                String Empleado = c.getString("Empleado");
                String IdSupervisor = c.getString("IdSupervisor");
                String Empresa = c.getString("EMPRESA");
                String Cod_Zona = c.getString("COD_ZONA");
                String Cod_SubZona = c.getString("COD_SUBZONA");
                String Pais_Id = c.getString("Pais_Id");
                String Pais_Nombre = c.getString("Pais_Nombre");
                String IdTipoNegocio = c.getString("IdTipoNegocio");
                String TipoNegocio = c.getString("TipoNegocio");
                ClientesH.GuardarClientes(IdCliente, Nombre,  FechaCreacion, Telefono, Direccion, IdDepartamento, IdMunicipio,
                        Ciudad, Ruc, Cedula, LimiteCredito, IdFormaPago, IdVendedor, Excento, CodigoLetra, Ruta,NombreRuta,
                        Frecuencia, PrecioEspecial, FechaUltimaCompra, Tipo,TipoPrecio, Descuento, Empleado, IdSupervisor,Empresa,
                        Cod_Zona,Cod_SubZona,Pais_Id,Pais_Nombre,IdTipoNegocio,TipoNegocio);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar clientes, Excepcion controlada ", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }

    //Vendedor
    public boolean SincronizarVendedores() throws JSONException {
        //************VENDEDORES
        HttpHandler shV = new HttpHandler();
        String urlStringV = urlVendedores;
        String jsonStrV = shV.makeServiceCall(urlStringV);

        if (jsonStrV == null) {
            new Funciones().SendMail("Ha ocurrido un error: al sincronizar vendedores, Respuesta Nula GET", variables_publicas.info + urlStringV, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        //Log.e(TAG, "Response from url: " + jsonStrC);
        DbOpenHelper.database.beginTransaction();
        VendedoresH.EliminaVendedores();
        JSONObject jsonObjV = new JSONObject(jsonStrV);
        // Getting JSON Array node
        JSONArray vendedores = jsonObjV.getJSONArray("ListaVendedoresResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < vendedores.length(); i++) {
                JSONObject c = vendedores.getJSONObject(i);

                String CODIGO = c.getString("CODIGO");
                String NOMBRE = c.getString("NOMBRE");
                String IDRUTA = c.getString("IDRUTA");
                String RUTA = c.getString("RUTA");
                String EMPRESA = c.getString("EMPRESA");
                String codsuper = c.getString("codsuper");
                String Status = c.getString("Status");
                String Supervisor = c.getString("Supervisor");

                VendedoresH.GuardarTotalVendedores(CODIGO, NOMBRE, IDRUTA, RUTA,EMPRESA, codsuper,Supervisor, Status);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar vendedores, Excepcion controlada", variables_publicas.info + urlStringV, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }
    public boolean SincronizarPromociones() throws JSONException {
        HttpHandler shPromociones = new HttpHandler();
        String urlStringPromociones= urlPromociones;
        String jsonStrPromociones = shPromociones.makeServiceCall(urlStringPromociones);

        if (jsonStrPromociones == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronicar CartillasBC, Respuesta nula GET", variables_publicas.info + urlStringPromociones, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }

        DbOpenHelper.database.beginTransaction();
        PromocionesH.EliminaPromociones();
        JSONObject jsonObjPromociones = new JSONObject(jsonStrPromociones);
        // Getting JSON Array node
        JSONArray promociones = jsonObjPromociones.getJSONArray("GetPromocionesResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < promociones.length(); i++) {
                JSONObject c = promociones.getJSONObject(i);

                String codpromo = c.getString("CodPromo");
                String itemv = c.getString("itemV");
                String cantv = c.getString("cantV");
                String itemb = c.getString("itemB");
                String cantb = c.getString("cantB");

                PromocionesH.GuardarPromociones(codpromo, itemv, cantv, itemb, cantb);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronicar Promociones, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }
   /* public boolean SincronizarZonas() throws JSONException {
        HttpHandler shZonas= new HttpHandler();
        String urlStringZonas= urlZonas;
        String jsonStrZonas = shZonas.makeServiceCall(urlStringZonas);

        if (jsonStrZonas == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronicar Zonas, Respuesta nula GET", variables_publicas.info + urlStringZonas, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }

        DbOpenHelper.database.beginTransaction();
        ZonasH.EliminaZonas();
        JSONObject jsonObjZonas = new JSONObject(jsonStrZonas);
        // Getting JSON Array node
        JSONArray zonas = jsonObjZonas.getJSONArray("GetZonasResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < zonas.length(); i++) {
                JSONObject c = zonas.getJSONObject(i);

                String empresa = c.getString("EMPRESA");
                String codZona = c.getString("COD_ZONA");
                String zona = c.getString("ZONA");
                String codSubZona = c.getString("COD_SUB_ZONA");
                String subZona = c.getString("SUBZONA");

                ZonasH.GuardarZonas(empresa, codZona, zona, codSubZona, subZona);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronicar Zonas, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }
*/
   public boolean SincronizarRutas() throws JSONException {
       HttpHandler shRutas= new HttpHandler();
       String urlStringRutas= urlRutas + variables_publicas.usuario.getCodigo();
       String jsonStrRutas = shRutas.makeServiceCall(urlStringRutas);

       if (jsonStrRutas == null) {
           new Funciones().SendMail("Ha ocurrido un error al sincronicar las Rutas, Respuesta nula GET", variables_publicas.info + urlStringRutas, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
           return false;
       }

       DbOpenHelper.database.beginTransaction();
       RutasH.EliminaRutas();
       JSONObject jsonObjRutas = new JSONObject(jsonStrRutas);
       // Getting JSON Array node
       JSONArray rutas = jsonObjRutas.getJSONArray("GetRutasResult");


       try {
           // looping through All Contacts
           for (int i = 0; i < rutas.length(); i++) {
               JSONObject c = rutas.getJSONObject(i);

               String idruta = c.getString("IDRUTA");
               String ruta = c.getString("RUTA");
               String vendedor = c.getString("VENDEDOR");

               RutasH.GuardarRutas(idruta, ruta, vendedor);
           }
           DbOpenHelper.database.setTransactionSuccessful();
           return true;
       } catch (Exception ex) {
           new Funciones().SendMail("Ha ocurrido un error al sincronicar las Rutas, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
           return false;
       } finally {
           DbOpenHelper.database.endTransaction();
       }

   }
    //FormaPago
    public boolean SincronizarFormaPago() throws JSONException {
        HttpHandler shFormaPago = new HttpHandler();
        String urlStringFormaPago = urlFormasPago;
        String jsonStrFormaPago = shFormaPago.makeServiceCall(urlStringFormaPago);

        if (jsonStrFormaPago == null) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar Forma de pago, Respuesta nula GET", variables_publicas.info + urlStringFormaPago, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        DbOpenHelper.database.beginTransaction();
        FormaPagoH.EliminaFormaPago();
        JSONObject jsonObjFormaPago = new JSONObject(jsonStrFormaPago);
        // Getting JSON Array node
        JSONArray FormaPago = jsonObjFormaPago.getJSONArray("FormasPagoResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < FormaPago.length(); i++) {
                JSONObject c = FormaPago.getJSONObject(i);

                String CODIGO = c.getString("CODIGO");
                String NOMBRE = c.getString("NOMBRE");
                String DIAS = c.getString("DIAS");
                String EMPRESA = c.getString("EMPRESA");

                FormaPagoH.GuardarTotalFormaPago(CODIGO, NOMBRE, DIAS, EMPRESA);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar Forma de pago, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }

    }
    //ConfiguracionSistema
    public boolean SincronizarConfiguracionSistema() throws JSONException {
        HttpHandler shConfiguracionSistema = new HttpHandler();
        String urlStringConfiguracionSistema = urlGetConfiguraciones;
        String jsonStrConfiguracionSistema = shConfiguracionSistema.makeServiceCall(urlStringConfiguracionSistema);

        if (jsonStrConfiguracionSistema == null) {
            new Funciones().SendMail("Ha ocurrido un error al Sincronizar ConfiguracionSistema, Respuesta nula GET", variables_publicas.info + urlStringConfiguracionSistema, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }

        DbOpenHelper.database.beginTransaction();
        ConfigSistemasH.EliminaConfigSistema();
        JSONObject jsonObjConfiguracionSistema = new JSONObject(jsonStrConfiguracionSistema);
        // Getting JSON Array node
        JSONArray FormaPago = jsonObjConfiguracionSistema.getJSONArray("GetConfiguracionesResult");


        try {
            // looping through All Contacts
            for (int i = 0; i < FormaPago.length(); i++) {
                JSONObject c = FormaPago.getJSONObject(i);

                String Id = c.getString("Id");
                String Sistema = c.getString("Sistema");
                String Configuracion = c.getString("Configuracion");
                String Valor = c.getString("Valor");
                String Activo = c.getString("Activo");

                if (Configuracion.equalsIgnoreCase("VersionDatos")) {
                    variables_publicas.ValorConfigServ = Valor;
                }
                if (Configuracion.equalsIgnoreCase("AplicarPrecioMayoristaXCaja")) {
                    variables_publicas.AplicarPrecioMayoristaXCaja = Valor;
                }
                if (Configuracion.equalsIgnoreCase("PermitirVentaDetAMayoristaXCaja")) {
                    variables_publicas.PermitirVentaDetAMayoristaXCaja = Valor;
                }
                if (Configuracion.equalsIgnoreCase("lstDepartamentosForaneo1")) {
                    variables_publicas.lstDepartamentosForaneo1 = Valor.split(",");
                }


                ConfigSistemasH.GuardarConfiguracionSistema(Id, Sistema, Configuracion, Valor, Activo);
                variables_publicas.Configuracion = ConfigSistemasH.BuscarValorConfig("VersionDatos");
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al Sincronizar ConfiguracionSistema, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }
    }

    //ClientesSucursal
    public boolean SincronizarClientesSucursal() throws JSONException {
        HttpHandler shClientesSucursal = new HttpHandler();
        String urlStringClientesSucursal = urlGetClienteSucursales;
        String jsonStrClientesSucursal = shClientesSucursal.makeServiceCall(urlStringClientesSucursal);

        if (jsonStrClientesSucursal == null) {
            new Funciones().SendMail("Ha ocurrido un error al SincronizarClientesSucursal, Respuesta nula", variables_publicas.info + urlStringClientesSucursal, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }

        DbOpenHelper.database.beginTransaction();
        ClientesSucH.EliminaClientesSucursales();
        JSONObject jsonObjClientesSucursal = new JSONObject(jsonStrClientesSucursal);
        // Getting JSON Array node
        JSONArray PrecioEspecial = jsonObjClientesSucursal.getJSONArray("GetClienteSucursalesResult");



        try {
            // looping through All Contacts
            for (int i = 0; i < PrecioEspecial.length(); i++) {
                JSONObject c = PrecioEspecial.getJSONObject(i);

                String CodSuc = c.getString("CodSuc");
                String CodCliente = c.getString("CodCliente");
                String Sucursal = c.getString("Sucursal");
                String Ciudad = c.getString("Ciudad");
                String DeptoID = c.getString("DeptoID");
                String Direccion = c.getString("Direccion");
                String FormaPagoID = c.getString("FormaPagoID");
                String Descuento = c.getString("Descuento");

                ClientesSucH.GuardarTotalClientesSucursal(CodSuc, CodCliente, Sucursal, Ciudad, DeptoID, Direccion, FormaPagoID,Descuento);
            }
            DbOpenHelper.database.setTransactionSuccessful();
            return true;
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al SincronizarClientesSucursal,Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        } finally {
            DbOpenHelper.database.endTransaction();
        }
    }

    public boolean SincronizarTodo() throws JSONException {

        if (SincronizarArticulos()) {
             if (SincronizarClientes()) {
                 if (SincronizarTPrecios()) {
                     if (SincronizarEscalaPrecios()) {
                         if (SincronizarCategorias()) {
                             if (SincronizarVendedores()) {
                                 if (SincronizarPromociones()) {
                                     if (SincronizarFormaPago()) {
                                         if (SincronizarRutas()) {
                                             if (SincronizarClientesSucursal()) {
                                                 if (SincronizarConfiguracionSistema()) {
                                                     if (ActualizarUsuario()) {
                                                         SincronizarPedidosLocales();
                                                         return true;
                                                     }
                                                 }
                                             }
                                         }
                                     }
                                 }
                             }
                         }
                     }
                 }
            }
        }
        return false;
    }


    public void SincronizarTablas() throws JSONException {
        SincronizarArticulos();
        SincronizarClientes();
        SincronizarRutas();
        SincronizarEscalaPrecios();
        SincronizarPromociones();
        SincronizarClientesSucursal();
        SincronizarConfiguracionSistema();
    }

    public void SincronizarTablaClientes() throws JSONException {
        SincronizarClientes();
    }
    public boolean SincronizarPedidosLocales() {

        boolean guardadoOK = true;
        List<HashMap<String, String>> PedidosLocal = PedidosH.ObtenerPedidosLocales(Funciones.GetLocalDateTime(), "");
        for (HashMap<String, String> item : PedidosLocal) {
            if (guardadoOK == false) {
                break;
            }
            Gson gson = new Gson();
            Vendedor vendedor = VendedoresH.ObtenerVendedor(item.get(variables_publicas.PEDIDOS_COLUMN_IdVendedor));
            Cliente cliente = ClientesH.BuscarCliente(item.get(variables_publicas.PEDIDOS_COLUMN_IdCliente));
            String jsonPedido = gson.toJson(PedidosH.ObtenerPedido(item.get(variables_publicas.PEDIDOS_COLUMN_CodigoPedido)));
            guardadoOK = Boolean.parseBoolean(SincronizarDatos.SincronizarPedido(PedidosH, PedidosDetalleH, vendedor, cliente, item.get(variables_publicas.PEDIDOS_COLUMN_CodigoPedido), jsonPedido, false).split(",")[0]);
        }
        return guardadoOK;
    }

    private boolean ActualizarUsuario() {

        HttpHandler sh = new HttpHandler();
        String urlString = url + variables_publicas.usuario.getUsuario() + "/" + Funciones.Codificar(variables_publicas.usuario.getContrasenia());
        String encodeUrl = "";
        try {
            URL Url = new URL(urlString);
            URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
            encodeUrl = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonStr = sh.makeServiceCall(encodeUrl);

        /**********************************USUARIOS**************************************/
        if (jsonStr != null) {

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray Usuarios = jsonObj.getJSONArray("BuscarUsuarioResult");
                if (Usuarios.length() == 0) {
                    return false;
                }
                UsuariosH.EliminaUsuarios();
                // looping through All Contacts

                for (int i = 0; i < Usuarios.length(); i++) {
                    JSONObject c = Usuarios.getJSONObject(i);
                    variables_publicas.CodigoVendedor = c.getString("Codigo");
                    variables_publicas.NombreVendedor = c.getString("Nombre");
                    variables_publicas.UsuarioLogin = c.getString("Usuario");
                    variables_publicas.TipoUsuario = c.getString("Tipo");
                    String Contrasenia = c.getString("Contrasenia");
                    String Tipo = c.getString("Tipo");
                    variables_publicas.RutaCliente = c.getString("Ruta");
                    variables_publicas.Canal = c.getString("Canal");
                    String TasaCambio = c.getString("TasaCambio");
                    String RutaForanea = c.getString("RutaForanea");
                    String FechaActualiza = Funciones.getDatePhone();
                    String EsVendedor = c.getString("EsVendedor");
                    String Empresa_ID = c.getString("Empresa_ID");
                    String AddCliente = c.getString("AddCliente");
                    UsuariosH.GuardarUsuario(variables_publicas.CodigoVendedor, variables_publicas.NombreVendedor,
                            variables_publicas.UsuarioLogin, Contrasenia, Tipo, variables_publicas.RutaCliente, variables_publicas.Canal, TasaCambio, RutaForanea, FechaActualiza,EsVendedor,Empresa_ID,AddCliente);

                    variables_publicas.usuario = UsuariosH.BuscarUsuarios(variables_publicas.usuario.getUsuario(), Contrasenia);
                    return true;
                }
            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                new Funciones().SendMail("Ha ocurrido un error al obtener los datos del usuario,Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                return false;
            }

        } else {
            new Funciones().SendMail("Ha ocurrido un error al obtener los datos del usuario,Respuesta nula", variables_publicas.info + urlString, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
       return false;
    }

    private boolean ObtenerDptosMuniBarrios() {

        HttpHandler sh = new HttpHandler();
        String urlString = urlDptoMuniBarrios;
        String encodeUrl = "";
        try {
            URL Url = new URL(urlString);
            URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
            encodeUrl = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonStr = sh.makeServiceCall(encodeUrl);

        /**********************************DEPARTAMENTOS**************************************/
        if (jsonStr != null) {

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray dptosMunBarr = jsonObj.getJSONArray("ObtenerDptoMuniBarriosResult");
                if (dptosMunBarr.length() == 0) {
                    return false;
                }
                ClientesH.EliminarDptosMuniBarrios();
                // looping through All Contacts

                for (int i = 0; i < dptosMunBarr.length(); i++) {
                    JSONObject c = dptosMunBarr.getJSONObject(i);
                    ClientesH.GuardarDptosMuniBarrios(c.get("CodDpto").toString(),c.get("Dpto").toString(),c.get("CodMuni").toString(),c.get("Muni").toString());
                }
            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                new Funciones().SendMail("Ha ocurrido un error al obtener los Departamentos y Municipios,Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                return false;
            }

        } else {
            new Funciones().SendMail("Ha ocurrido un error al obtener los Departamentos y Municipios,Respuesta nula", variables_publicas.info + urlString, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
        return false;
    }

    public static String SincronizarPedido(PedidosHelper PedidoH, PedidosDetalleHelper PedidoDetalleH, Vendedor vendedor, Cliente cliente, String IdPedido, String jsonPedido, boolean Editar) {

        HttpHandler sh = new HttpHandler();
        String encodeUrl = "";
        Gson gson = new Gson();
        List<HashMap<String, String>> pedidoDetalle = PedidoDetalleH.ObtenerPedidoDetalle(IdPedido);
        for (HashMap<String, String> item : pedidoDetalle) {
            item.put("SubTotal", item.get("SubTotal").replace(",", ""));
            item.put("Costo", item.get("Costo").replace(",", ""));
            item.put("Total", item.get("Total").replace(",", ""));
            item.put("Iva", item.get("Iva").replace(",", ""));
            item.put("Precio", item.get("Precio").replace(",", ""));
            item.put("Descuento", item.get("Descuento").replace(",", ""));
            item.put("Descripcion", Codificar(item.get("Descripcion")));
        }
        String jsonPedidoDetalle = gson.toJson(pedidoDetalle);
        final String urlDetalle = variables_publicas.direccionIp + "/ServicioPedidos.svc/SincronizarPedidoTotal/";
        final String urlStringDetalle = urlDetalle + cliente.getIdCliente() + "/" + String.valueOf(Editar) + "/" + vendedor.getCODIGO() + "/" + jsonPedido + "/" + jsonPedidoDetalle+ "/" + cliente.getEmpresa();

        HashMap<String,String> postData = new HashMap<>();
        postData.put("CodigoCliente",cliente.getIdCliente());
        postData.put("Editar",String.valueOf(Editar));
        postData.put("IdVendedor",vendedor.getCODIGO());
        postData.put("pedido",jsonPedido);
        postData.put("Detalle",jsonPedidoDetalle)   ;
        postData.put("Empresa",cliente.getEmpresa())   ;

        String jsonStrPedido = sh.performPostCall(urlDetalle,postData);

      //  String jsonStrPedido = sh.makeServiceCallPost(encodeUrl);
        if (jsonStrPedido == null || jsonPedido.isEmpty()) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar el pedido,Respuesta nula POST", variables_publicas.info + urlStringDetalle, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return "false,Ha ocurrido un error al sincronizar el detalle del pedido,Respuesta nula";
        } else {
            try {
                JSONObject result = new JSONObject(jsonStrPedido);
                String resultState = (String) ((String) result.get("SincronizarPedidoTotalResult")).split(",")[0];
                String NoPedido = (String) ((String) result.get("SincronizarPedidoTotalResult")).split(",")[1];
                if (resultState.equals("false")) {

                    if (NoPedido.equalsIgnoreCase("Pedido ya existe en base de datos")) {
                        NoPedido =  ((String) result.get("SincronizarPedidoTotalResult")).split(",")[1];
                    } else {
                        new Funciones().SendMail("Ha ocurrido un error al sincronizar el pedido ,Respuesta false", variables_publicas.info + NoPedido +" *** "+urlStringDetalle, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                        return "false," + NoPedido;
                    }
                }
                PedidoH.ActualizarPedido(IdPedido, NoPedido);
                PedidoDetalleH.ActualizarCodigoPedido(IdPedido, NoPedido);
                return "true";
            } catch (Exception ex) {
                new Funciones().SendMail("Ha ocurrido un error al sincronizar el pedido, Excepcion controlada ", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                Log.e("Error", ex.getMessage());
                return "false," + ex.getMessage() + "";
            }

        }

    }

    public static String ConsultarExistencias(final Activity activity, PedidosHelper PedidoH, ArticulosHelper ArticulosH, String CodigoArticulo) {
        HttpHandler sh = new HttpHandler();
        String encodeUrl = "";

        final String urlConsulta = urlConsultarExistencias + CodigoArticulo;

        try {
            URL Url = new URL(urlConsulta);
            URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
            encodeUrl = uri.toURL().toString();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            new Funciones().SendMail("Ha ocurrido un error al obtener las existencias, Codificar URL", variables_publicas.info + e.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            e.printStackTrace();
            return "N/A";
        }

        String jsonExistencia = sh.makeServiceCall(encodeUrl);
        if (jsonExistencia == null) {
            return "N/A";
        } else {
            try {
                JSONObject result = new JSONObject(jsonExistencia);
                String resultState = (String) ((String) result.get("ObtenerInventarioArticuloResult")).split(",")[0];
                final String existencia = (String) ((String) result.get("ObtenerInventarioArticuloResult")).split(",")[1];
                if (resultState.equals("false")) {

                    new Funciones().SendMail("Ha ocurrido un error al obtener las existencias ,Respuesta false", variables_publicas.info + " --- " + existencia, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                    return "N/A";
                }
                /*Si no hubo ningun problema procedemos a actualizar las existencias locales*/
                ArticulosH.ActualizarExistencias(CodigoArticulo, existencia);
                return existencia;
            } catch (Exception ex) {
                new Funciones().SendMail("Ha ocurrido un error al obtener las existencias, Excepcion controlada ", variables_publicas.info + ex.getMessage() + " ---json: " + urlConsulta + " ---Response: " + jsonExistencia, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                Log.e("Error", ex.getMessage());
                return "N/A";
            }

        }

    }

    public static String SincronizarClientesTotal(Cliente cliente, String jsonCliente) {
        boolean Editar=false;
        if (variables_publicas.vEditando){
            Editar=true;
        }else {
            Editar=false;
        }
        HttpHandler sh = new HttpHandler();
        String encodeUrl = "";
        Gson gson = new Gson();

        final String urlCliente = variables_publicas.direccionIp + "/ServicioClientes.svc/SincronizarClienteTotal/";
        final String urlStringDetalle = urlCliente + cliente.getIdCliente() + "/" + String.valueOf(Editar) + "/" + cliente.getEmpresa() + "/" + jsonCliente;


        HashMap<String,String> postData = new HashMap<>();
        postData.put("Editar",String.valueOf(Editar));
        postData.put("IdCliente",cliente.getIdCliente());
        postData.put("Empresa",cliente.getEmpresa());
        postData.put("cliente",jsonCliente);


        String jsonStrCliente = sh.performPostCall(urlCliente,postData);

        //  String jsonStrPedido = sh.makeServiceCallPost(encodeUrl);
        if (jsonStrCliente == null || jsonCliente.isEmpty()) {
            new Funciones().SendMail("Ha ocurrido un error al sincronizar el Cliente,Respuesta nula POST", variables_publicas.info + urlStringDetalle, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return "false,Ha ocurrido un error al sincronizar el cliente,Respuesta nula";
        } else {
            try {
                JSONObject result = new JSONObject(jsonStrCliente);
                String resultState = (String) ((String) result.get("SincronizarClienteTotalResult")).split(",")[0];
                String NoCliente = (String) ((String) result.get("SincronizarClienteTotalResult")).split(",")[1];
                if (resultState.equals("false")) {

                    if (NoCliente.equalsIgnoreCase("Cliente ya existe en base de datos")) {
                        NoCliente =  ((String) result.get("SincronizarClienteTotalResult")).split(",")[1];
                    } else {
                        new Funciones().SendMail("Ha ocurrido un error al sincronizar el Cliente ,Respuesta false", variables_publicas.info + NoCliente +" *** "+urlStringDetalle, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                        return "false," + NoCliente;
                    }
                }
               // PedidoH.ActualizarPedido(IdPedido, NoPedido);
//
                return "true";
            } catch (Exception ex) {
                new Funciones().SendMail("Ha ocurrido un error al sincronizar el Cliente, Excepcion controlada ", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                Log.e("Error", ex.getMessage());
                return "false," + ex.getMessage() + "";
            }

        }

    }

    public static String SincronizarClientesInactivo(Cliente cliente, String jsonCliente) {
        HttpHandler sh = new HttpHandler();
        String encodeUrl = "";
        Gson gson = new Gson();

        final String urlCliente = variables_publicas.direccionIp + "/ServicioClientes.svc/SincronizarClienteInactivo/";
        final String urlStringDetalle = urlCliente + cliente.getIdCliente() + "/" + cliente.getEmpresa() + "/" + jsonCliente;


        HashMap<String,String> postData = new HashMap<>();
        postData.put("IdCliente",cliente.getIdCliente());
        postData.put("Empresa",cliente.getEmpresa());
        postData.put("cliente",jsonCliente);

        String jsonStrCliente = sh.performPostCall(urlCliente,postData);

        //  String jsonStrPedido = sh.makeServiceCallPost(encodeUrl);
        if (jsonStrCliente == null || jsonCliente.isEmpty()) {
            new Funciones().SendMail("Ha ocurrido un error al activar al cliente. Respuesta nula POST", variables_publicas.info + urlStringDetalle, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return "false,Ha ocurrido un error al activar al cliente. Respuesta nula";
        } else {
            try {
                JSONObject result = new JSONObject(jsonStrCliente);
                String resultState = (String) ((String) result.get("SincronizarClienteInactivoResult")).split(",")[0];
                String NoCliente = (String) ((String) result.get("SincronizarClienteInactivoResult")).split(",")[1];
                if (resultState.equals("false")) {

                    if (NoCliente.equalsIgnoreCase("Cliente ya existe en base de datos")) {
                        NoCliente =  ((String) result.get("SincronizarClienteInactivoResult")).split(",")[1];
                    } else {
                        new Funciones().SendMail("Ha ocurrido un error al activar el Cliente. Respuesta false", variables_publicas.info + NoCliente +" *** "+urlStringDetalle, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                        return "false," + NoCliente;
                    }
                }
                // PedidoH.ActualizarPedido(IdPedido, NoPedido);
//
                return "true";
            } catch (Exception ex) {
                new Funciones().SendMail("Ha ocurrido un error al activar el Cliente. Excepcion controlada ", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                Log.e("Error", ex.getMessage());
                return "false," + ex.getMessage() + "";
            }

        }

    }

         public static boolean ObtenerPedidoGuardado(String vPedido, PedidosHelper vpedidoh) {

        HttpHandler sh = new HttpHandler();
        String urlString = variables_publicas.direccionIp + "/ServicioPedidos.svc/ObtenerPedidoCabecera/" + vPedido;
        String encodeUrl = "";
        try {
            URL Url = new URL(urlString);
            URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
            encodeUrl = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonStr = sh.makeServiceCall(encodeUrl);

        /**********************************PEDIDOS**************************************/
        if (jsonStr != null) {

            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray pedido = jsonObj.getJSONArray("ObtenerPedidoCabeceraResult");
                if (pedido.length() == 0) {
                    return false;
                }
                vpedidoh.EliminaPedido(vPedido);
                for (int i = 0; i < pedido.length(); i++) {
                    JSONObject c = pedido.getJSONObject(i);
                    vpedidoh.GuardarPedido(c.get("CodigoPedido").toString(),c.get("IdVendedor").toString(),c.get("IdCliente").toString(),c.get("Tipo").toString(),c.get("Observacion").toString(),c.get("IdFormaPago").toString(),c.get("IdSucursal").toString(),c.get("Fecha").toString(),c.get("Usuario").toString(),c.get("IMEI").toString(),c.get("Subtotal").toString(),c.get("Total").toString(),c.get("TCambio").toString(),c.get("Empresa").toString());
                }
                return true;
            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                new Funciones().SendMail("Ha ocurrido un error al obtener el Pedido. Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                return false;
            }

        } else {
            new Funciones().SendMail("Ha ocurrido un error al obtener el Pedido. Respuesta nula", variables_publicas.info + urlString, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
    }

    public static boolean ObtenerPedidoGuardadoDetalle(String vPedido,PedidosDetalleHelper vpedidodetalleh) {

        HttpHandler sh = new HttpHandler();
        String urlString = variables_publicas.direccionIp + "/ServicioPedidos.svc/ObtenerPedidoDetalle/" + vPedido;
        String encodeUrl = "";
        try {
            URL Url = new URL(urlString);
            URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
            encodeUrl = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String jsonStr = sh.makeServiceCall(encodeUrl);

        /**********************************PEDIDOS DETALLE**************************************/
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray pedido = jsonObj.getJSONArray("ObtenerPedidoDetalleResult");
                if (pedido.length() == 0) {
                    return false;
                }
                vpedidodetalleh.EliminarDetallePedido(vPedido);
                for (int i = 0; i < pedido.length(); i++) {
                    JSONObject c = pedido.getJSONObject(i);
                    vpedidodetalleh.GuardarDetallePedido(c.get("CodigoPedido").toString(),c.get("CodigoArticulo").toString(),c.get("Descripcion").toString(),c.get("Cantidad").toString().substring(0,c.get("Cantidad").toString().indexOf(".")),c.get("BonificaA").toString(),c.get("TipoArt").toString(),c.get("PorDescuento").toString(),c.get("Descuento").toString(),c.get("CodUM").toString(),c.get("Unidades").toString(),c.get("Costo").toString(),c.get("Precio").toString(),c.get("TipoPrecio").toString(),c.get("PorcentajeIva").toString(),c.get("Iva").toString(),c.get("Um").toString(),c.get("Subtotal").toString(),c.get("Total").toString(),c.get("Bodega").toString());
                }
                return true;
            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
                new Funciones().SendMail("Ha ocurrido un error al obtener el detalle del pedido. Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                return false;
            }

        } else {
            new Funciones().SendMail("Ha ocurrido un error al obtener el detalle del pedido. Respuesta nula", variables_publicas.info + urlString, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            return false;
        }
    }

}