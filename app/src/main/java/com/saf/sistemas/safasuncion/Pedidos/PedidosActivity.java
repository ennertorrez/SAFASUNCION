package com.saf.sistemas.safasuncion.Pedidos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.IdRes;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.saf.sistemas.safasuncion.AccesoDatos.ArticulosHelper;
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
import com.saf.sistemas.safasuncion.Auxiliar.Funciones;
import com.saf.sistemas.safasuncion.Auxiliar.SincronizarDatos;
import com.saf.sistemas.safasuncion.Auxiliar.variables_publicas;
import com.saf.sistemas.safasuncion.Entidades.Articulo;
import com.saf.sistemas.safasuncion.Entidades.Cliente;
import com.saf.sistemas.safasuncion.Entidades.ClienteSucursal;
import com.saf.sistemas.safasuncion.Entidades.FormaPago;
import com.saf.sistemas.safasuncion.Entidades.Model;
import com.saf.sistemas.safasuncion.Entidades.MyAdapter;
import com.saf.sistemas.safasuncion.Entidades.Pedido;
import com.saf.sistemas.safasuncion.Entidades.PedidoDetalle;
import com.saf.sistemas.safasuncion.Entidades.Precios;
import com.saf.sistemas.safasuncion.Entidades.TipoPrecio;
import com.saf.sistemas.safasuncion.Entidades.Vendedor;
import com.saf.sistemas.safasuncion.HttpHandler;
import com.saf.sistemas.safasuncion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class PedidosActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String TAG = PedidosActivity.class.getSimpleName();
    private boolean MensajeCaja;
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    //region Declaracion de controles
    private EditText txtCodigoArticulo;
    private Spinner cboTPrecio;
    private EditText txtObservaciones;
    private TextView txtPrecioArticulo;
    private TextView lblNombCliente;
    private TextView lblCodCliente;
    private TextView lblNoPedido;
    private TextView lblDescripcionArticulo;
    private TextView lblTc;
    private TextView lblSubTotalCor;
    private TextView lblIvaCor;
    private TextView lblTotalCor;
    private TextView lblSubTotalDol;
    private TextView lblIvaDol;
    private TextView lblTotalDol;
    private TextView lblFooter;
    private TextView lblFooterItem;
    private TextView lblUM; //Unidad de medida por caja
    private TextView lblUMV; // Unidad minima de venta
    private TextView lblExistentias;
    private Button btnAgregar;
    private Button btnBuscar;
    private Button btnOK;
    private Button btnGuardar;
    private Button btnCancelar;
    private EditText txtCantidad;
    private Spinner cboVendedor;
    private Spinner cboSucursal;
    private Spinner cboCondicion;
    private ListView lv;
    private ListView lvItem;
    private SimpleAdapter adapter;
    private ProgressDialog pDialog;
    AlertDialog alertDialog;
    private String existencia = "N/A";
    private SincronizarDatos sd;
    private boolean isOnline = false;
    private String visualizando="False";
    final String urlGetConfiguraciones = variables_publicas.direccionIp + "/ServicioPedidos.svc/GetConfiguraciones";
    //endregion

    String IMEI = "";
    private String focusedControl = "";
    static final String KEY_IdCliente = "IdCliente";
    static final String KEY_NombreCliente = "Nombre";

    private Articulo articulo;
    private DecimalFormat df;
    private FormaPago condicion;
    private ClienteSucursal sucursal;
    private TipoPrecio codTipoPrecio;
    public static ArrayList<HashMap<String, String>> listaArticulos;
    public static List<Model> selectedItems;
    public static List<Model> listaArticulosItem2;
    public double total;
    private int idRuta=0;
    private String vUM="";
    private String vCodUM="";
    private String vTipoPrecio="";
    private int vUnidades=0;
    private int idTipo=0;
    public double subtotal;
    private Cliente cliente;
    private double tasaCambio = 0;
    private double subTotalPrecioSuper = 0;
    private Pedido pedido;
    private DataBaseOpenHelper DbOpenHelper;
    private VendedoresHelper VendedoresH;
    private ClientesSucursalHelper ClientesSucursalH;
    private FormaPagoHelper FormaPagoH;
    private ArticulosHelper ArticulosH;
    private UsuariosHelper UsuariosH;
    private ClientesHelper ClientesH;
    private TPreciosHelper TPreciosH;
    private EscalaPreciosHelper EscalaPreciosH;
    private RutasHelper RutasH;
    private PromocionesHelper PromocionesH;
    private PedidosDetalleHelper PedidoDetalleH;
    private ConfiguracionSistemaHelper ConfiguracionSistemaH;
    private PedidosHelper PedidoH;
    private String CodigoLetra = "";
    private String jsonPedido = "";
    private boolean finalizar = false;
    private String TipoPrecio = "";
    private boolean guardadoOK = false;
    private Vendedor vendedor = null;
    private double PrecioItem = 0;
    private double Precio1 = 0;
    private double Precio2 = 0;
    private double Precio3 = 0;
    private double Precio4 = 0;

    private String busqueda = "1";
    private int tipoBusqueda = 1;
    private boolean validarTipoBusqueda;
    private int IdDepartamento;
    private String Nombre;
    private boolean editar = false;
    private boolean pedidoLocal;
    static final int DEFAULT_THREAD_POOL_SIZE = 4;

    View getlistview;
    AlertDialog.Builder builder;
    ArrayAdapter<Model> adapter3;
    List<Model> list = new ArrayList<Model>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos);

        pedido = new Pedido();

        DbOpenHelper = new DataBaseOpenHelper(PedidosActivity.this);
        ClientesH = new ClientesHelper(DbOpenHelper.database);
        VendedoresH = new VendedoresHelper(DbOpenHelper.database);
        ClientesSucursalH = new ClientesSucursalHelper(DbOpenHelper.database);
        PromocionesH = new PromocionesHelper(DbOpenHelper.database);
        FormaPagoH = new FormaPagoHelper(DbOpenHelper.database);
        ArticulosH = new ArticulosHelper(DbOpenHelper.database);
        UsuariosH = new UsuariosHelper(DbOpenHelper.database);
        PedidoH = new PedidosHelper(DbOpenHelper.database);
        TPreciosH = new TPreciosHelper(DbOpenHelper.database);
        RutasH = new RutasHelper(DbOpenHelper.database);
        PedidoDetalleH = new PedidosDetalleHelper(DbOpenHelper.database);
        EscalaPreciosH = new EscalaPreciosHelper(DbOpenHelper.database);
        ConfiguracionSistemaH = new ConfiguracionSistemaHelper(DbOpenHelper.database);

        sd = new SincronizarDatos(DbOpenHelper, ClientesH, VendedoresH, PromocionesH,
                FormaPagoH,
                ConfiguracionSistemaH, ClientesSucursalH, ArticulosH, UsuariosH, PedidoH, PedidoDetalleH, TPreciosH,RutasH,EscalaPreciosH);


        ValidarUltimaVersion();
        if (isOnline) {
            SincronizarConfig();
        }

        df = new DecimalFormat("#0.00");
        DecimalFormatSymbols fmts = new DecimalFormatSymbols();
        fmts.setGroupingSeparator(',');
        df.setGroupingSize(3);
        df.setGroupingUsed(true);
        df.setDecimalFormatSymbols(fmts);

        listaArticulos = new ArrayList<HashMap<String, String>>();
        listaArticulosItem2 = new ArrayList<Model>();
        selectedItems = new ArrayList<Model>();
        cboVendedor = (Spinner) findViewById(R.id.cboVendedor);
        cboSucursal = (Spinner) findViewById(R.id.cboSucursal);
        cboCondicion = (Spinner) findViewById(R.id.cboCondicion);
        cboTPrecio = (Spinner) findViewById(R.id.cboTPrecio);
        lblFooter = (TextView) findViewById(R.id.lblFooter);
        lblTc = (TextView) findViewById(R.id.lblTC);
        tasaCambio = Double.parseDouble(variables_publicas.usuario.getTasaCambio());
        final TextView lblCodigoCliente = (TextView) findViewById(R.id.lblCodigoCliente);
        TextView lblRuta = (TextView) findViewById(R.id.lblRuta);
        TextView lblCanal = (TextView) findViewById(R.id.lblCanal);
        final Spinner cboVendedor = (Spinner) findViewById(R.id.cboVendedor);
        TextView lblNombre = (TextView) findViewById(R.id.lblNombreCliente);
        txtCodigoArticulo = (EditText) findViewById(R.id.txtCodigoArticulo);
        lblDescripcionArticulo = (TextView) findViewById(R.id.lblDescripcionArticulo);
        lblUM = (TextView) findViewById(R.id.lblUMArt);
        lblUMV = (TextView) findViewById(R.id.lblUMArticulo);
        lblExistentias = (TextView) findViewById(R.id.lblExistencia);
        lblNoPedido = (TextView) findViewById(R.id.lblNoPedido);
        txtCantidad = (EditText) findViewById(R.id.txtCantidad);
        txtCantidad.setFocusable(true);
        txtCantidad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnAgregar.performClick();
                    focusedControl = "txtCantidad";
                    return false;
                }
                return true;
            }
        });



        variables_publicas.AplicaIVAGral = ConfiguracionSistemaH.BuscarValorConfig("aplicaIVA").getValor();
        variables_publicas.ValorIVAGral = ConfiguracionSistemaH.BuscarValorConfig("valorIVA").getValor();

        lv = (ListView) findViewById(R.id.listPedido);

        registerForContextMenu(lv);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);

        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

// Disallow the touch request for parent scroll on touch of child view
                scrollView.requestDisallowInterceptTouchEvent(true);

                int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        scrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);
            }
        });

        txtObservaciones = (EditText) findViewById(R.id.txtObservacion);
        txtPrecioArticulo = (TextView) findViewById(R.id.txtPrecioArticulo);
        lblTc.setText(df.format(Double.parseDouble(variables_publicas.usuario.getTasaCambio())));
        lblSubTotalCor = (TextView) findViewById(R.id.lblSubTotalCor);
        lblIvaCor = (TextView) findViewById(R.id.lblIvaCor);
        lblTotalCor = (TextView) findViewById(R.id.lblTotalCor);
        lblSubTotalDol = (TextView) findViewById(R.id.lblSubTotalDol);
        lblIvaDol = (TextView) findViewById(R.id.lblIvaDol);
        lblTotalDol = (TextView) findViewById(R.id.lblTotalDol);

        Intent in = getIntent();

        pedido.setIdCliente(in.getStringExtra(KEY_IdCliente));
        Nombre = in.getStringExtra(KEY_NombreCliente);

        if (in.getSerializableExtra(variables_publicas.PEDIDOS_COLUMN_CodigoPedido) != null) {

            if (in.getSerializableExtra(variables_publicas.PEDIDOS_COLUMN_CodigoPedido).toString().startsWith("-")) {
                pedidoLocal = true;

            } else {

                pedidoLocal = false;
            }

            visualizando=in.getSerializableExtra(variables_publicas.vVisualizar).toString();

            editar = true;

            listaArticulos.clear();
            pedido = PedidoH.GetPedido(in.getStringExtra(variables_publicas.PEDIDOS_COLUMN_CodigoPedido));
            listaArticulos = PedidoDetalleH.ObtenerPedidoDetalleArrayList(pedido.getCodigoPedido());
            for (HashMap<String, String> item : listaArticulos) {
                Articulo art = ArticulosH.BuscarArticulo(item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo));
                if (art==null) {
                }else{
                    //item.put("Cod", item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo).substring(item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo).length() - 3));
                    item.put("Cod", item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo));
                    item.put("IdProveedor", art.getIdProveedor());
                    item.put("UnidadCajaVenta", art.getUnidadCajaVenta());
                    item.put("UnidadCaja",art.getUnidadCaja());
                }
            }
            txtObservaciones.setText(pedido.getObservacion());
            lblNoPedido.setText("PEDIDO N°: " + pedido.getCodigoPedido());

            List<ClienteSucursal> sucursales = ClientesSucursalH.ObtenerClienteSucursales(pedido.getIdCliente());
            int indice;
            for (int i = 0; i < sucursales.size(); i++) {
                if (sucursales.get(i).getCodSuc().equals(pedido.getIdSucursal())) {
                    final int finalI = i;
                    cboSucursal.post(new Runnable() {
                        public void run() {
                            cboSucursal.setSelection(finalI);
                        }
                    });
                    break;
                }
            }


            RefrescarGrid();
            CalcularTotales();
        } else {
            cboSucursal.setSelection(0);
        }

        // Loading spinner data from database
        CargaDatosCombo();

        lblCodigoCliente.setText(cliente.getCodigoLetra());
        lblNombre.setText(Nombre);
        lblRuta.setText(cliente.getNombreRuta());
        idRuta= Integer.parseInt(cliente.getRuta());
        lblCanal.setText(cliente.getTipoPrecio());
        idTipo=Integer.parseInt(cliente.getTipo());

        cboTPrecio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                // On selecting a spinner item
                codTipoPrecio = (TipoPrecio) adapter.getItemAtPosition(position);

                if (!codTipoPrecio.getCod_Tipo_Precio().equals("0")) {
                    String tmpcodigoart =txtCodigoArticulo.getText().toString();
                    List<Articulo> precios = TPreciosH.ObtenerPrecioPorUM(tmpcodigoart);
                    if (precios.size()==0){
                        vTipoPrecio = cliente.getTipoPrecio();
                        //vUM = ClientesH.ObtenerDescripcion(variables_publicas.PRECIOS_COLUMN_UM,variables_publicas.TABLE_PRECIOS,variables_publicas.PRECIOS_COLUMN_COD_UM,codum.getCod_UM());
                        vUnidades = Integer.parseInt(lblUM.getText().toString().replace("N/A","1"));
                        vCodUM = "1";
                    }else {
                        for (int i = 0; i < precios.size(); i++) {
                            if (codTipoPrecio.getCod_Tipo_Precio().toString().equalsIgnoreCase("1")){
                                txtPrecioArticulo.setText(precios.get(i).getPrecio());
                            }else if (codTipoPrecio.getCod_Tipo_Precio().toString().equalsIgnoreCase("2")){
                                txtPrecioArticulo.setText(precios.get(i).getPrecio2());
                            }else if (codTipoPrecio.getCod_Tipo_Precio().toString().equalsIgnoreCase("3")){
                                txtPrecioArticulo.setText(precios.get(i).getPrecio3());
                            }else if (codTipoPrecio.getCod_Tipo_Precio().toString().equalsIgnoreCase("4")){
                                txtPrecioArticulo.setText(precios.get(i).getPrecio4());
                            }

                            vTipoPrecio = codTipoPrecio.getTipo_Precio();
                            //vUM = precios.get(i).getUM();
                            vUnidades = Integer.parseInt(precios.get(i).getUnidadCaja());
                            vCodUM = precios.get(i).getCodUM();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        btnAgregar = (Button) findViewById(R.id.btnAgregar);
        btnBuscar = (Button) findViewById(R.id.btnBuscar);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PedidosActivity.this.onBackPressed();
            }
        });
        txtCodigoArticulo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) || (actionId == EditorInfo.IME_ACTION_GO) || (actionId == EditorInfo.IME_ACTION_SEND)) {
                    btnBuscar.performClick();
                    focusedControl = "txtCodigoArticulo";
                    return false;
                }
                return true;
            }
        });

        txtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //System.out.println(s.toString() + " " + start + " " + count + " " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1=s.toString();
                if (s.length() > 0 && !s.toString().equalsIgnoreCase("")) {
                    String tmpcodigoart =txtCodigoArticulo.getText().toString();
                    List<Articulo> precios = TPreciosH.ObtenerPrecioPorUM(tmpcodigoart);
                    if (precios.size()==0){
                        vTipoPrecio = cliente.getTipoPrecio();
                        //vUM = ClientesH.ObtenerDescripcion(variables_publicas.PRECIOS_COLUMN_UM,variables_publicas.TABLE_PRECIOS,variables_publicas.PRECIOS_COLUMN_COD_UM,codum.getCod_UM());
                        vUnidades = Integer.parseInt(lblUM.getText().toString().replace("N/A","1"));
                        vCodUM = "1";
                    }else {
                        vTipoPrecio = cliente.getTipoPrecio();
                        for (int i = 0; i < precios.size(); i++) {
                            /*if (idTipo==1){
                                if ((double) Double.parseDouble(s1.toString())>=Double.parseDouble(precios.get(i).getUnidadCajaVenta3())){
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio4());
                                    codTipoPrecio.setCod_Tipo_Precio("4");
                                }else if  ((double) Double.parseDouble(s1.toString())>=Double.parseDouble(precios.get(i).getUnidadCajaVenta2())){
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio3());
                                    codTipoPrecio.setCod_Tipo_Precio("3");
                                }else if ((double) Double.parseDouble(s1.toString())>=Double.parseDouble(precios.get(i).getUnidadCajaVenta())){
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio2());
                                    codTipoPrecio.setCod_Tipo_Precio("2");
                                }else{
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio());
                                    codTipoPrecio.setCod_Tipo_Precio("1");
                                }
                            }else if (idTipo==2){
                                if ((double) Double.parseDouble(s1.toString())>=Double.parseDouble(precios.get(i).getUnidadCajaVenta3())){
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio4());
                                    codTipoPrecio.setCod_Tipo_Precio("4");
                                }else if  ((double) Double.parseDouble(s1.toString())>=Double.parseDouble(precios.get(i).getUnidadCajaVenta2())){
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio3());
                                    codTipoPrecio.setCod_Tipo_Precio("3");
                                }else {
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio2());
                                    codTipoPrecio.setCod_Tipo_Precio("2");
                                }
                            }else if (idTipo==3){
                                if ((double) Double.parseDouble(s1.toString())>=Double.parseDouble(precios.get(i).getUnidadCajaVenta3())){
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio4());
                                    codTipoPrecio.setCod_Tipo_Precio("4");
                                }else {
                                    txtPrecioArticulo.setText(precios.get(i).getPrecio3());
                                    codTipoPrecio.setCod_Tipo_Precio("3");
                                }
                            }else{
                                txtPrecioArticulo.setText(precios.get(i).getPrecio4());
                                codTipoPrecio.setCod_Tipo_Precio("4");
                            }*/

                            String vValorFiltro = ClientesH.ObtenerDescripcion(variables_publicas.TPRECIOS_COLUMN_TIPO_PRECIO,variables_publicas.TABLE_TPRECIOS,variables_publicas.TPRECIOS_COLUMN_COD_TIPO_PRECIO,cliente.getTipo());
                            cboTPrecio.setSelection(getIndex(cboTPrecio, vValorFiltro));
                            vTipoPrecio = cboTPrecio.getSelectedItem().toString();
                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnBuscar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
//
                //BuscarArticulo();
                CreateDialog();// Click to create Dialog

                validarTipoBusqueda = true;
                //btnOK.performClick();
                txtCantidad.requestFocus();
                focusedControl = "";
                // }


            }
        });
        final List<PedidoDetalle> lstPedidoDetalle = new ArrayList<>();
        btnAgregar.setOnClickListener(new OnClickListener() {
                                          public void onClick(View v) {

                                              try {

                                                  if (TextUtils.isEmpty(txtCantidad.getText().toString())) {
                                                      txtCantidad.setError("Ingrese un valor");
                                                      return;
                                                  }

                                                  if (Double.parseDouble(txtCantidad.getText().toString()) <= 0) {
                                                      txtCantidad.setError("Ingrese un valor mayor a 0");
                                                      return;
                                                  }
                                                  if (articulo == null) {
                                                      txtCodigoArticulo.setError("Ingrese un valor");
                                                      return;
                                                  }
                                                  boolean repetido = EsArticuloRepetido(txtCodigoArticulo.getText().toString());
                                                  if (repetido) {
                                                      MensajeAviso("Este artículo ya ha sigo agregado al pedido.");
                                                      return;
                                                  }

                                                  double cantidad = Double.parseDouble(txtCantidad.getText().toString());

                                                  if (Double.parseDouble(txtPrecioArticulo.getText().toString()) == 0) {
                                                      MensajeAviso("Solamente se pueden agregar productos con precio mayor a cero.");
                                                      return;
                                                  }
                                                  HashMap<String, String> itemPedidos = new HashMap<>();
                                                  if (AgregarDetalle(itemPedidos)) {
                                                      MensajeCaja = true;
                                                      LimipiarDatos(MensajeCaja);
                                                      subTotalPrecioSuper = 0;
                                                      for (HashMap<String, String> item : listaArticulos) {
                                                          subTotalPrecioSuper += Double.parseDouble(item.get("SubTotal").replace(",", ""));
                                                      }
                                                      RefrescarGrid();
                                                      ValidarPreciosEscala();
                                                      AplicarBonificacionCombinada();
                                                      RefrescarGrid();
                                                      CalcularTotales();
                                                      InputMethodManager inputManager = (InputMethodManager)
                                                              getSystemService(Context.INPUT_METHOD_SERVICE);

                                                      inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                                              InputMethodManager.RESULT_SHOWN);
                                                  }


                                              } catch (Exception e) {
                                                  cliente = ClientesH.BuscarCliente(pedido.getIdCliente());
                                                  MensajeAviso(e.getMessage());
                                              }
                                          }
                                      }
        );
        btnGuardar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CodigoLetra = lblCodigoCliente.getText().toString();

                    Guardar();
                } catch (Exception e) {
                    DbOpenHelper.database.endTransaction();
                    MensajeAviso(e.getMessage());
                }
            }
        });

        variables_publicas.PermitirVentaDetAMayoristaXCaja = ConfiguracionSistemaH.BuscarValorConfig("PermitirVentaDetAMayoristaXCaja").getValor();
        variables_publicas.AplicarPrecioMayoristaXCaja = ConfiguracionSistemaH.BuscarValorConfig("AplicarPrecioMayoristaXCaja").getValor();

        if (variables_publicas.usuario.getTipo().equalsIgnoreCase("Vendedor")) {
            cboVendedor.setEnabled(false);
        } else {
            cboVendedor.setEnabled(true);

        }

        if (visualizando.equals("True")){
            btnBuscar.setEnabled(false);
            btnAgregar.setEnabled(false);
            btnGuardar.setEnabled(false);
            lv.setContextClickable(false);
        }

    }

    public void CreateDialog() {

        // Dynamically load a listview layout file
        LayoutInflater inflater = LayoutInflater.from(PedidosActivity.this);
        getlistview = inflater.inflate(R.layout.masterproductospedidos_layout, null);
        selectedItems = new ArrayList<Model>();
        btnOK = (Button) getlistview.findViewById(R.id.btnBuscar);
        final RadioGroup rgGrupo = (RadioGroup) getlistview.findViewById(R.id.rgGrupo);
        rgGrupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

            }
        });

        final EditText txtBusquedaItem = (EditText) getlistview.findViewById(R.id.txtBusqueda);
        lvItem = (ListView) getlistview.findViewById(R.id.list);
        lblFooterItem = (TextView) getlistview.findViewById(R.id.lblFooter);
        txtBusquedaItem.setText(txtCodigoArticulo.getText());

        busqueda = txtBusquedaItem.getText().toString();
        if (validarTipoBusqueda) {
            try {
                int busquedaText = Integer.parseInt(busqueda);
                rgGrupo.check(R.id.rbCodigo);

            } catch (Exception ex) {

                if (busqueda.contains("-")) {
                    rgGrupo.check(R.id.rbCodigo);
                } else {
                    rgGrupo.check(R.id.rbDescripcion);
                }
            }
            validarTipoBusqueda = false;
        }
        int boton = rgGrupo.getCheckedRadioButtonId();// == R.id.rbCodigo ? "1" : "2";
        switch (boton) {
            case R.id.rbCodigo:
                tipoBusqueda = 1;
                break;
            case R.id.rbDescripcion:
                tipoBusqueda = 2;
                break;
        }
        try {
            switch (tipoBusqueda) {
                case 1:
                    listaArticulosItem2 = ArticulosH.BuscarArticuloCodigoNew(busqueda);
                    break;
                case 2:
                    listaArticulosItem2 = ArticulosH.BuscarArticuloNombreNew(busqueda);
                    break;
            }
        } catch (Exception ex) {
            MensajeAviso(ex.getMessage());
        }
        if (listaArticulosItem2.size() == 0) {
            MensajeAviso("El codigo de articulo ingresado no existe en la base de datos o esta deshabilitado para su venta");
        }


        ListView listview = (ListView) getlistview.findViewById(R.id.list);
        adapter3=new MyAdapter(this,listaArticulosItem2);
        listview.setAdapter(adapter3);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setOnItemClickListener(new  ItemOnClick());


        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(txtBusquedaItem.getWindowToken(), 0);
                busqueda = txtBusquedaItem.getText().toString();
                if (validarTipoBusqueda) {
                    try {
                        int busquedaText = Integer.parseInt(busqueda);
                        rgGrupo.check(R.id.rbCodigo);

                    } catch (Exception ex) {

                        if (busqueda.contains("-")) {
                            rgGrupo.check(R.id.rbCodigo);
                        } else {
                            rgGrupo.check(R.id.rbDescripcion);
                        }
                    }
                    validarTipoBusqueda = false;
                }
                int boton = rgGrupo.getCheckedRadioButtonId();// == R.id.rbCodigo ? "1" : "2";
                switch (boton) {
                    case R.id.rbCodigo:
                        tipoBusqueda = 1;
                        break;
                    case R.id.rbDescripcion:
                        tipoBusqueda = 2;
                        break;
                }
                try {
                    switch (tipoBusqueda) {
                        case 1:
                            listaArticulosItem2 = ArticulosH.BuscarArticuloCodigoNew(busqueda);
                            break;
                        case 2:
                            listaArticulosItem2 = ArticulosH.BuscarArticuloNombreNew(busqueda);
                            break;
                    }
                } catch (Exception ex) {
                    MensajeAviso(ex.getMessage());
                }
                if (listaArticulosItem2.size() == 0) {
                    MensajeAviso("El codigo de articulo ingresado no existe en la base de datos o esta deshabilitado para su venta");
                }
                ListView listview = (ListView) getlistview.findViewById(R.id.list);
                adapter3=new MyAdapter(PedidosActivity.this,listaArticulosItem2);
                listview.setAdapter(adapter3);
                listview.setItemsCanFocus(false);
                listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                listview.setOnItemClickListener(new  ItemOnClick());
                lblFooterItem.setText("Productos encontrados: " + String.valueOf(listaArticulosItem2.size()));
            }
        });
        builder = new AlertDialog.Builder(this);
        //Set the loaded listview
        builder.setView(getlistview);
        builder.setPositiveButton("Ok", new DialogOnClick());
        builder.setNegativeButton("No", new DialogOnClick());
        builder.create().show();
        lblFooterItem.setText("Productos encontrados: " + String.valueOf(listaArticulosItem2.size()));
    }
    class ItemOnClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {

            CheckBox cBox = (CheckBox) view.findViewById(R.id.checkbox);
            //Toast.makeText(view.getContext(), label.getText().toString()+" "+isCheckedOrNot(checkbox), Toast.LENGTH_LONG).show();
            if (cBox.isChecked()) {
                cBox.setChecked(false);
                for(int indice = 0;indice<selectedItems.size();indice++)
                {
                    if (selectedItems.get(indice).getCodigo().equalsIgnoreCase(((TextView) view.findViewById(R.id.Codigo)).getText().toString())){
                        selectedItems.remove(indice);
                    }
                }

            } else {
                Log.i("TAG", "Cancel this option");
                cBox.setChecked(true);
                selectedItems.add(new Model(((TextView) view.findViewById(R.id.Codigo)).getText().toString(),((TextView) view.findViewById(R.id.Precio)).getText().toString(),((TextView) view.findViewById(R.id.Nombre)).getText().toString()));
            }

        }

    }

    class DialogOnClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:

                    txtCodigoArticulo.setText("");
                    lblDescripcionArticulo.setText("");
                    if (selectedItems!=null){
                        if (selectedItems.size()>0){
                            if (selectedItems.size()==1){
                                String CodigoArticulo = selectedItems.get(0).getCodigo();

                                articulo = ArticulosH.BuscarArticulo(CodigoArticulo);

                                txtCodigoArticulo.setText(CodigoArticulo);
                                lblDescripcionArticulo.setText(articulo.getNombre());

                                if (cliente.getTipo().equalsIgnoreCase("1")){
                                    txtPrecioArticulo.setText(articulo.getPrecio());
                                }else if (cliente.getTipo().equalsIgnoreCase("2")){
                                    txtPrecioArticulo.setText(articulo.getPrecio2());
                                }else if (cliente.getTipo().equalsIgnoreCase("3")){
                                    txtPrecioArticulo.setText(articulo.getPrecio3());
                                }else {
                                    txtPrecioArticulo.setText(articulo.getPrecio4());
                                }
                                String vDesTPrecio= ClientesH.ObtenerDescripcion(variables_publicas.TPRECIOS_COLUMN_TIPO_PRECIO,variables_publicas.TABLE_TPRECIOS,variables_publicas.TPRECIOS_COLUMN_COD_TIPO_PRECIO,cliente.getTipo());
                                vUM=articulo.getUnidad();
                                lblUM.setText(articulo.getUnidadCaja());
                                lblUMV.setText(articulo.getUnidadCajaVenta());
                                existencia = articulo.getExistencia();
                                lblExistentias.setText(String.valueOf((int) (Double.parseDouble(existencia))));

                                cboTPrecio.setSelection(getIndex(cboTPrecio, vDesTPrecio));

                                MensajeCaja = true;
                                //alertDialog.dismiss();
                            }else{
                                int Total = listaArticulos.size()+selectedItems.size();
                                int MaxSKU=0;
                                MaxSKU= Integer.parseInt(variables_publicas.usuario.getRutaForanea()== null ? "1" :variables_publicas.usuario.getRutaForanea());
                                if (Total>MaxSKU){
                                    Total= Math.abs(MaxSKU-listaArticulos.size());
                                }else{
                                    Total=selectedItems.size();
                                }
                                for(int indice = 0;indice<Total;indice++)
                                {
                                    String CodigoArticulo = selectedItems.get(indice).getCodigo();

                                    articulo = ArticulosH.BuscarArticulo(CodigoArticulo);

                                    try{
                                        boolean repetido = EsArticuloRepetido(CodigoArticulo);
                                        if (!repetido) {
                                            if ((listaArticulos.size()+selectedItems.size())<=40){

                                            }
                                            HashMap<String, String> itemPedidos = new HashMap<>();
                                            double Precio = Double.parseDouble(articulo.getPrecio());
                                            String DescripcionArt = articulo.getNombre();
                                            vTipoPrecio=ClientesH.ObtenerDescripcion(variables_publicas.TPRECIOS_COLUMN_TIPO_PRECIO,variables_publicas.TABLE_TPRECIOS,variables_publicas.TPRECIOS_COLUMN_COD_TIPO_PRECIO,codTipoPrecio.getCod_Tipo_Precio());
                                            itemPedidos.put("CodigoPedido", pedido.getCodigoPedido());
                                            itemPedidos.put("CodigoArticulo", articulo.getCodigo());
                                            itemPedidos.put("Cod", articulo.getCodigo().split("-")[articulo.getCodigo().split("-").length - 1]);
                                            //itemPedidos.put("Cod", articulo.getCodigo());
                                            itemPedidos.put("Cantidad", "1");
                                            itemPedidos.put("Um", articulo.getUnidad().trim());
                                            itemPedidos.put("Precio", String.valueOf(Precio));
                                            itemPedidos.put("TipoPrecio", vTipoPrecio);
                                            itemPedidos.put("Descripcion", DescripcionArt);
                                            itemPedidos.put("CodUM", "1");
                                            itemPedidos.put("Unidades",String.valueOf(Double.parseDouble("1")));
                                            itemPedidos.put("Costo", String.valueOf(Double.parseDouble(articulo.getCosto())));
                                            itemPedidos.put("PorDescuento", String.valueOf(Double.parseDouble("0")));
                                            itemPedidos.put("PorDescuentoOriginal", String.valueOf(Double.parseDouble("0")));
                                            itemPedidos.put("TipoArt", "P");
                                            itemPedidos.put("BonificaA", "");
                                            //itemPedidos.put("PorIva", articulo.getPorIva());
                                            double subtotal, iva, total, descuento, porIva;
                                            subtotal = Double.parseDouble(itemPedidos.get("Precio")) * Double.parseDouble(itemPedidos.get("Cantidad"));
                                            descuento = subtotal * (Double.parseDouble(itemPedidos.get("PorDescuento")) / 100);
                                            subtotal = subtotal - descuento;

                                            if (variables_publicas.AplicaIVAGral.equalsIgnoreCase("1")){
                                                if (cliente.getExcento().equalsIgnoreCase("1")){
                                                    porIva=0;
                                                }else{
                                                    if (Double.parseDouble(articulo.getPorIva())==0){
                                                        porIva=0;
                                                    }else{
                                                        porIva=Double.parseDouble(articulo.getPorIva());
                                                    }
                                                }
                                            }else{
                                                porIva=0;
                                            }


                                            //porIva = Double.parseDouble(articulo.getPorIva());
                                            iva = subtotal * porIva;
                                            total = subtotal + iva;
                                            itemPedidos.put("Descuento", df.format(descuento));
                                            itemPedidos.put("PorcentajeIva",String.valueOf(porIva));
                                            itemPedidos.put("Iva", df.format(iva));
                                            itemPedidos.put("SubTotal", df.format(subtotal));
                                            itemPedidos.put("Total", df.format(total));
                                            itemPedidos.put("IdProveedor", articulo.getIdProveedor());
                                            itemPedidos.put("UnidadCajaVenta", articulo.getUnidadCajaVenta());
                                            itemPedidos.put("Bodega", "01");
                                            listaArticulos.add(itemPedidos);

                                            PrecioItem = 0;
                                            articulo = null;
                                            //RefrescarGrid();
                                            CalcularTotales();
                                        }
                                    }catch (Exception e){
                                        MensajeAviso(e.getMessage());
                                    }
                                    MensajeCaja = true;
                                }

                                subTotalPrecioSuper = 0;
                                for (HashMap<String, String> item : listaArticulos) {
                                    subTotalPrecioSuper += Double.parseDouble(item.get("SubTotal").replace(",", ""));
                                }
                                RefrescarGrid();
                                ValidarPreciosEscala();
                                AplicarBonificacionCombinada();
                                RefrescarGrid();
                                CalcularTotales();
                                InputMethodManager inputManager = (InputMethodManager)
                                        getSystemService(Context.INPUT_METHOD_SERVICE);

                                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                        InputMethodManager.RESULT_SHOWN);
                            }
                        }
                    }

                    selectedItems=null;
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    //Event of cancel button
                    break;
                default:
                    break;
            }
        }
    }

    private void CheckConnectivity() {
        isOnline = Funciones.TestServerConectivity();
    }

    private void AplicarBonificacionCombinada() {


        ArrayList<HashMap<String, String>> listaTemp = new ArrayList<HashMap<String, String>>();
        ;
        /*Primero eliminamos todas la bonificaciones para poder recalcular*/
        for (int i = 0; i < listaArticulos.size(); i++) {
            HashMap<String, String> item = listaArticulos.get(i);
            if (item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                listaTemp.add(item);
            }
        }
        listaArticulos = listaTemp;
        int vCodPromo=0;
        int vCodPromotemp=0;
        List myListPromo = new ArrayList();
        for (int i = 0; i < listaArticulos.size(); i++) {
            HashMap<String, String> itemPedidos = listaArticulos.get(i);
            if (itemPedidos.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                vCodPromo=PromocionesH.BuscarCodPromocion(itemPedidos.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo));
                if (vCodPromo>0){

                    if (vCodPromotemp!=vCodPromo){
                        myListPromo.add(String.valueOf(vCodPromo));
                    }
                    vCodPromotemp=vCodPromo;
                }
            }

        }

        int cantidad=0;
        boolean existe = false;
        for(int indice = 0;indice<myListPromo.size();indice++)
        {
            HashMap<String, String> promocion= PromocionesH.BuscarPromocion(Integer.parseInt(myListPromo.get(indice).toString()));
            if (promocion.size() > 0) {
                String vArticulos= promocion.get("Articulos");
                double vCantV= Double.parseDouble( promocion.get("CantidadV"));
                String vArticuloB= promocion.get("ItemB");
                double vCantB= Double.parseDouble( promocion.get("CantidadB"));
                Articulo articuloB = ArticulosH.BuscarArticulo(vArticuloB);
                /*Primero sumamos las cantidades de los items promocionados*/
                for (HashMap<String, String> item : listaArticulos) {
                    if (vArticulos.contains(item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo)) && item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                        cantidad += (int) Double.parseDouble(item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_Cantidad));
                    }

                }
                int factor=0;

                if (cantidad >= vCantV ) {
                    factor = (int) Math.floor(cantidad / vCantV);
                    vCantB = factor * vCantB;
                } else {
                    vCantB = 0;
                }
                for (HashMap<String, String> item : listaArticulos) {
                    /*Si ya existe actualizamos la cantidad bonificada actualizamos el valor o borramos segun si aplica a la bonificacion*/
                    if (item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo).equals(articuloB.getCodigo()) && item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equals("B") && vCantB >= 1) {
                        existe = true;
                        item.put(variables_publicas.PEDIDOS_DETALLE_COLUMN_Cantidad, String.valueOf(vCantB));
                        break;
                    }
                }

                if (vCantB <=0 ) {

                } else {
                    /*Si no existe lo agregamos*/
                    if (existe == false && vCantB>0) {

                        int MaxSKU=0;
                        MaxSKU= Integer.parseInt(variables_publicas.usuario.getRutaForanea()== null ? "1" :variables_publicas.usuario.getRutaForanea());

                        //Validamos que solamente se puedan ingresar 40 articulos
                        if (listaArticulos.size() >= MaxSKU) {

                            /*Eliminamos el ultimo item NO BONIFICADO agregado*/
                            HashMap<String, String> ultimoItemP = null;
                            for (HashMap<String, String> itemsel : listaArticulos) {
                                if (itemsel.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                                    ultimoItemP = itemsel;
                                }
                            }
                            if (ultimoItemP != null) {
                                listaArticulos.remove(ultimoItemP);
                            }
                            MensajeAviso("No se puede agregar el producto seleccionado,ya que posee bonificacion y excede el limite de "+ String.valueOf(MaxSKU) +" productos para un pedido.");
                            break;
                        }
                        HashMap<String, String> articuloBonificado = new HashMap<>();
                        articuloBonificado.put("CodigoPedido", pedido.getCodigoPedido());
                        articuloBonificado.put("Cod", articuloB.getCodigo().split("-")[articuloB.getCodigo().split("-").length - 1]);
                        articuloBonificado.put("CodigoArticulo", articuloB.getCodigo());
                        articuloBonificado.put("Um", articuloB.getUnidad());
                        articuloBonificado.put("Cantidad", String.valueOf(vCantB)); //
                        articuloBonificado.put("Precio", "0");
                        articuloBonificado.put("CodUM", "1");
                        articuloBonificado.put("Descripcion",  "**" + articuloB.getNombre());
                        articuloBonificado.put("Costo", "0");
                        articuloBonificado.put("PorDescuento", "0");
                        articuloBonificado.put("TipoArt", "B");
                        articuloBonificado.put("BonificaA", "");
                        articuloBonificado.put("Unidades", String.valueOf(vCantB));
                        articuloBonificado.put("PorcentajeIva", "0");
                        articuloBonificado.put("Descuento", "0");
                        articuloBonificado.put("Iva", "0");
                        articuloBonificado.put("SubTotal", "0");
                        articuloBonificado.put("Total", "0");
                        articuloBonificado.put("TipoPrecio", "Bonificacion");
                        articuloBonificado.put("IdProveedor", articuloB.getIdProveedor());
                        articuloBonificado.put("UnidadCajaVenta", articuloB.getUnidadCajaVenta());
                        articuloBonificado.put("Bodega", "01");
                        listaArticulos.add(articuloBonificado);
                    }
                }
                RefrescarGrid();
                CalcularTotales();
            }
            cantidad=0;
            existe = false;
        }
    }

    private void ValidarPreciosEscala() {


        ArrayList<HashMap<String, String>> listaTemp = new ArrayList<HashMap<String, String>>();
        ;
        /*Primero eliminamos todas la bonificaciones para poder recalcular*/
        for (int i = 0; i < listaArticulos.size(); i++) {
            HashMap<String, String> item = listaArticulos.get(i);
            if (item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                listaTemp.add(item);
            }
        }
        listaArticulos = listaTemp;
        int vCodEscala=0;
        int vCodEscalatemp=0;
        List myListEscala = new ArrayList();
        for (int i = 0; i < listaArticulos.size(); i++) {
            HashMap<String, String> itemPedidos = listaArticulos.get(i);
            if (itemPedidos.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                vCodEscala=EscalaPreciosH.BuscarCodEscala(itemPedidos.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo));
                if (vCodEscala>0){

                    if (vCodEscalatemp!=vCodEscala){
                        myListEscala.add(String.valueOf(vCodEscala));
                    }
                    vCodEscalatemp=vCodEscala;
                }
            }

        }

        int cantidad=0;
        double precio=0;
        boolean entra=false;
        for(int indice = 0;indice<myListEscala.size();indice++)
        {
            HashMap<String, String> escala= EscalaPreciosH.ObtenerEscala(Integer.parseInt(myListEscala.get(indice).toString()));
            if (escala.size() > 0) {
                String vArticulos= escala.get("ListaArticulos");
                double vCant1= Double.parseDouble( escala.get("Escala1"));
                double vCant2= Double.parseDouble( escala.get("Escala2"));
                double vCant3= Double.parseDouble( escala.get("Escala3"));
                double vPrecio1= Double.parseDouble( escala.get("Precio1"));
                double vPrecio2= Double.parseDouble( escala.get("Precio2"));
                double vPrecio3= Double.parseDouble( escala.get("Precio3"));

                /*Primero sumamos las cantidades de los items para la escala*/
                for (HashMap<String, String> item : listaArticulos) {
                    if (vArticulos.contains(item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo)) && item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                        cantidad += (int) Double.parseDouble(item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_Cantidad));
                    }

                }
                if (cantidad >= vCant3 ) {
                    precio=vPrecio3;
                    entra=true;
                } else if (cantidad >= vCant2 ) {
                    precio=vPrecio2;
                    entra=true;
                }else if (cantidad >= vCant1 ) {
                    precio=vPrecio1;
                    entra=true;
                }else{
                    entra=false;
                }
                if(entra){
                    double  subtotaldetalle=0;
                    double  descuento=0;
                    double  porIva=0;
                    double  iva=0;
                    double tot=0;
                    for (HashMap<String, String> item : listaArticulos) {
                        if (vArticulos.contains(item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo)) && item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equals("P")) {
                            item.put(variables_publicas.PEDIDOS_DETALLE_COLUMN_Precio, String.valueOf(precio));
                            item.put(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoPrecio, "Escala");
                            subtotaldetalle = Double.parseDouble(item.get("Precio")) * Double.parseDouble(item.get("Cantidad"));
                            descuento = subtotaldetalle * (Double.parseDouble(item.get("PorDescuento")) / 100);
                            subtotaldetalle = subtotaldetalle - descuento;

                            if (variables_publicas.AplicaIVAGral.equalsIgnoreCase("1")){
                                if (cliente.getExcento().equalsIgnoreCase("1")){
                                    porIva=0;
                                }else{
                                    if (Double.parseDouble(articulo.getPorIva())==0){
                                        porIva=0;
                                    }else{
                                        porIva=Double.parseDouble(articulo.getPorIva());
                                    }
                                }
                            }else{
                                porIva=0;
                            }
                            iva = subtotaldetalle * porIva;
                            tot = subtotaldetalle + iva;
                            item.put("Descuento", df.format(descuento));
                            item.put("PorcentajeIva",String.valueOf(porIva));
                            item.put("Iva", df.format(iva));
                            item.put("SubTotal", df.format(subtotaldetalle));
                            item.put("Total", df.format(tot));
                        }
                    }
                }
                RefrescarGrid();
                CalcularTotales();
            }
            cantidad=0;
            precio=0;
            entra=false;
        }
    }

    private void SincronizarConfig() {
        if (Build.VERSION.SDK_INT >= 11) {
            //--post GB use serial executor by default --
            new GetValorConfig().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            //--GB uses ThreadPoolExecutor by default--
            new GetValorConfig().execute();
        }
    }

    private void ValidarUltimaVersion() {

        String latestVersion = "";
        String currentVersion = getCurrentVersion();
        variables_publicas.VersionSistema = currentVersion;
        try {

            if (Build.VERSION.SDK_INT >= 11) {
                //--post GB use serial executor by default --
                new GetLatestVersion().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else {
                //--GB uses ThreadPoolExecutor by default--
                new GetLatestVersion().execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getCurrentVersion() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pInfo = null;

        try {
            pInfo = pm.getPackageInfo(this.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e1) {
            e1.printStackTrace();
        }
        String currentVersion = pInfo.versionName;

        return currentVersion;
    }


    private void scrollMyListViewToBottom() {
        lv.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                lv.setSelection(adapter.getCount() - 1);
            }
        });
    }
    //endregion

    //region Metodos

    private boolean SincronizarPedido(HashMap<String, String> pedido) {
        Gson gson = new Gson();

        jsonPedido = gson.toJson(pedido);

        try {
            if (Build.VERSION.SDK_INT >= 11) {
                //--post GB use serial executor by default --
                new SincronizardorPedidos().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else {
                //--GB uses ThreadPoolExecutor by default--
                new SincronizardorPedidos().execute();
            }
        } catch (final Exception ex) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            ex.getMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            });
            //MensajeAviso(ex.getMessage());
        }

        return false;
    }

    private boolean Guardar() {
        if (lv.getCount() <= 0) {
            MensajeAviso("No se puede guardar el pedido. Debe ingresar al menos 1 item");
            return false;
        }

        String mensaje = "";

        pedido.setTipo(cliente.getTipo());
        mensaje = "Esta seguro que desea guardar el pedido?";

        new AlertDialog.Builder(this)
                .setTitle("Confirmación Requerida")
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DbOpenHelper.database.beginTransaction();
                        if (GuardarPedido()) {
                            DbOpenHelper.database.setTransactionSuccessful();
                            DbOpenHelper.database.endTransaction();
                            SincronizarPedido(PedidoH.ObtenerPedido(pedido.getCodigoPedido()));
                        } else {
                            DbOpenHelper.database.endTransaction();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

        return true;
    }

    private boolean GuardarPedido() {
        String codSuc = sucursal == null ? "0" : sucursal.getCodSuc();
        pedido.setIdSucursal(codSuc);
        IMEI = variables_publicas.IMEI;
        pedido.setIdVendedor(String.valueOf(pedido.getIdVendedor()));
        pedido.setIdCliente(String.valueOf(pedido.getIdCliente()));
        pedido.setObservacion(Funciones.Codificar(txtObservaciones.getText().toString()));
        pedido.setIdFormaPago(condicion.getCODIGO());
        pedido.setFecha(variables_publicas.FechaActual);
        pedido.setUsuario(variables_publicas.usuario.getUsuario());
        pedido.setIMEI(IMEI);
        pedido.setTCambio(lblTc.getText().toString());
        pedido.setEmpresa(cliente.getEmpresa());
        //Esto lo ponemos para cuando es editar
        PedidoH.EliminaPedido(pedido.getCodigoPedido());
        PedidoDetalleH.EliminarDetallePedido(pedido.getCodigoPedido());

        if (IMEI == null) {

            new AlertDialog.Builder(this)
                    .setTitle("Confirmación Requerida")
                    .setMessage("Es necesario configurar el permiso \"Administrar llamadas telefonicas\" para porder guardar un pedido, Desea continuar ? ")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            loadIMEI();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            return false;

        }
        Funciones.GetLocalDateTime();


        boolean saved = PedidoH.GuardarPedido(pedido.getCodigoPedido(), pedido.getIdVendedor(), pedido.getIdCliente(), pedido.getTipo(),
                txtObservaciones.getText().toString(), condicion.getCODIGO(), pedido.getIdSucursal(),
                variables_publicas.FechaActual, variables_publicas.usuario.getUsuario(), IMEI, String.valueOf(subtotal), String.valueOf(total),df.format(Double.parseDouble(pedido.getTCambio())),pedido.getEmpresa());

        if (!saved) {
            MensajeAviso("Ha Ocurrido un error al guardar los datos");
            return false;
        }
        //Guardamos el detalle del pedido
        for (HashMap<String, String> item : listaArticulos) {
            saved = PedidoDetalleH.GuardarDetallePedido(item);
            if (!saved) {
                break;
            }
        }

        return true;
    }

    public void loadIMEI() {
        // Check if the READ_PHONE_STATE permission is already available.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // READ_PHONE_STATE permission has not been granted.
            requestReadPhoneStatePermission();
        } else {
            // READ_PHONE_STATE permission is already been granted.
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                variables_publicas.IMEI = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                variables_publicas.IMEI = tm.getImei();
            } else {
                variables_publicas.IMEI = tm.getDeviceId();
            }
            if (variables_publicas.IMEI  == null || variables_publicas.IMEI.isEmpty()) {
                variables_publicas.IMEI = android.os.Build.SERIAL;
            }
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(PedidosActivity.this)
                    .setTitle("Permission Request")
                    .setMessage("Se necesita permiso para acceder al estado del telefono")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(PedidosActivity.this,
                                    new String[]{Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            // READ_PHONE_STATE permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
    }

    private void CargaDatosCombo() {
        List<Vendedor> vendedores = VendedoresH.ObtenerListaVendedores();
        ArrayAdapter<Vendedor> adapterVendedor = new ArrayAdapter<Vendedor>(this, android.R.layout.simple_spinner_item, vendedores);
        adapterVendedor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboVendedor.setAdapter(adapterVendedor);


        cliente = ClientesH.BuscarCliente(pedido.getIdCliente());
        IdDepartamento = Integer.parseInt(cliente.getIdDepartamento());
        /*Si no es vendedor o es ventas oficina*/
        if (variables_publicas.usuario.getCodigo().equals("0") || cliente.getIdVendedor().equals("1") || cliente.getEmpleado().equals("1")) {
            pedido.setIdVendedor(cliente.getIdVendedor());
        } else {
            pedido.setIdVendedor(variables_publicas.usuario.getCodigo());
        }

        if (cliente == null) {
            MensajeAviso("El cliente no se encuentra en la base de datos");
            finish();
        }
        if (editar == false) {
            GenerarCodigoPedido();
        }

        if (variables_publicas.usuario.getTipo().equals("Vendedor")) {
            Vendedor vendedor = vendedores.get(0);
            for (int i = 0; Integer.parseInt(vendedor.getCODIGO()) != Integer.parseInt(pedido.getIdVendedor()); i++)
                try {
                    this.vendedor = vendedor;
                    vendedor = vendedores.get(i);
                } catch (Exception ex) {
                    new Funciones().SendMail("Ha ocurrido un error al seleccionar el vendedor en CargarDatosCombo PedidosActivity Tipo 'Vendedor', Excepcion controlada", ex.getStackTrace().toString() + " *** " + variables_publicas.info, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                }
            cboVendedor.setSelection(adapterVendedor.getPosition(vendedor));
        } else {

            Vendedor vendedor = vendedores.get(0);
            for (int i = 0; Integer.parseInt(vendedor.getCODIGO()) != Integer.parseInt(cliente.getIdVendedor()); i++) {
                try {
                    this.vendedor = vendedor;
                    vendedor = vendedores.get(i);
                } catch (Exception ex) {
                    new Funciones().SendMail("Ha ocurrido un error al seleccionar el vendedor en CargarDatosCombo PedidosActivity Tipo 'No vendedor', Excepcion controlada", ex.getStackTrace().toString() + " *** " + variables_publicas.info, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
                }
            }
            cboVendedor.setSelection(adapterVendedor.getPosition(vendedor));
        }
        cboVendedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                // On selecting a spinner item
                vendedor = (Vendedor) adapter.getItemAtPosition(position);
                if (!editar) {
                    pedido.setIdVendedor(vendedor.getCODIGO().toString());
                    GenerarCodigoPedido();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

/*        List<UnidadMedida> unidadMedidas = PreciosH.ObtenerListaUM(cliente.getTipo());
        ArrayAdapter<UnidadMedida> adapterUM = new ArrayAdapter<UnidadMedida>(this, android.R.layout.simple_spinner_item, unidadMedidas);
        adapterUM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboUM.setAdapter(adapterUM);

        String[] valores = {"Unidades"};
        cboUM.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valores));
        cboUM.setSelection(getIndex(cboUM, "Unidades"));*/

/*        final List<com.saf.sistemas.safasuncion.Entidades.TipoPrecio> TPrecio;
        TPrecio = TPreciosH.ObtenerTipoPrecio();
        ArrayAdapter<TipoPrecio> adapterTPrecio = new ArrayAdapter<TipoPrecio>(this, android.R.layout.simple_spinner_item, TPrecio);
        adapterTPrecio.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        cboTPrecio.setAdapter(adapterTPrecio);*/

        List<TipoPrecio> listTPrecio = TPreciosH.ObtenerTipoPrecio();
        ArrayAdapter<TipoPrecio> adapterTPrecio = new ArrayAdapter<TipoPrecio>(this, android.R.layout.simple_spinner_item, listTPrecio);
        adapterTPrecio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboTPrecio.setAdapter(adapterTPrecio);
        codTipoPrecio = listTPrecio.get(0);
        for (int i = 0; !(codTipoPrecio.getCod_Tipo_Precio().equals(cliente.getTipo())); i++)
            codTipoPrecio = listTPrecio.get(i);
        cboTPrecio.setSelection(adapterTPrecio.getPosition(codTipoPrecio));

        List<ClienteSucursal> sucursales = ClientesSucursalH.ObtenerClienteSucursales(pedido.getIdCliente());
        ArrayAdapter<ClienteSucursal> adapterSucursal = new ArrayAdapter<ClienteSucursal>(this, android.R.layout.simple_spinner_item, sucursales);
        adapterSucursal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboSucursal.setAdapter(adapterSucursal);
        cboSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                // On selecting a spinner item
                sucursal = (ClienteSucursal) adapter.getItemAtPosition(position);
                if (!sucursal.getCodSuc().equals("0")) {
                    cliente.setIdDepartamento(sucursal.getDeptoID());
                    IdDepartamento = Integer.parseInt(sucursal.getDeptoID());
                }
                RefrescarGrid();
                CalcularTotales();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        List<FormaPago> lstFormasPago = FormaPagoH.ObtenerListaFormaPago();
        ArrayAdapter<FormaPago> adapterFormaPago = new ArrayAdapter<FormaPago>(this, android.R.layout.simple_spinner_item, lstFormasPago);
        adapterFormaPago.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cboCondicion.setAdapter(adapterFormaPago);
        condicion = lstFormasPago.get(0);
        for (int i = 0; !(condicion.getCODIGO().equals(cliente.getIdFormaPago())); i++)
            condicion = lstFormasPago.get(i);

        cboCondicion.setSelection(adapterFormaPago.getPosition(condicion));
//        cboCondicion.setEnabled(false);

        cboCondicion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                // On selecting a spinner item
                condicion = (FormaPago) adapter.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void GenerarCodigoPedido() {
        pedido.setCodigoPedido("-" + GetFechaISO() + cliente.getIdCliente()  + pedido.getIdVendedor());
        lblNoPedido.setText("PEDIDO N°: " + pedido.getCodigoPedido());
    }

    private String GetFechaISO() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyMMddHHmms");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }

    private void LimipiarDatos(boolean MensajeCaja) {
        if (MensajeCaja) {
            txtPrecioArticulo.setText("0.00");
            articulo = null;
            txtCodigoArticulo.setText(null);
            txtCantidad.setError(null);
            txtCodigoArticulo.setText("");
            lblDescripcionArticulo.setText("");
            txtCantidad.setText("");
            lblFooter.setText("Total items:" + String.valueOf(listaArticulos.size()));
            txtCodigoArticulo.requestFocus();
            lblUM.setText("N/A");
            lblUMV.setText("N/A");
            lblExistentias.setText("N/A");
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    private boolean EsArticuloRepetido(String s) {

        for (HashMap<String, String> item : listaArticulos) {
            if (item.get("CodigoArticulo").equals(s) && item.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_TipoArt).equalsIgnoreCase("P")) {
                return true;
            }
        }
        return false;
    }

    private void setPrecio(HashMap<String, String> articulo, String pTipoPrecio, double precio, boolean settxtPrecioArticulo) {
        if (pTipoPrecio.equalsIgnoreCase("Especial")) {
            if (settxtPrecioArticulo) {
                txtPrecioArticulo.setText(String.valueOf(precio));
            }
            PrecioItem = precio;
        } else {
            if (settxtPrecioArticulo) {
                txtPrecioArticulo.setText(articulo.get("Precio" + pTipoPrecio));
            }
            PrecioItem = Double.parseDouble(articulo.get("Precio" + pTipoPrecio));
        }
        TipoPrecio = pTipoPrecio;

    }

    private boolean AgregarDetalle(HashMap<String, String> itemPedidos) {
        double Precio = Double.parseDouble(txtPrecioArticulo.getText().toString());
        String DescripcionArt = lblDescripcionArticulo.getText().toString();

        int MaxSKU=0;
        MaxSKU= Integer.parseInt(variables_publicas.usuario.getRutaForanea()== null ? "1" :variables_publicas.usuario.getRutaForanea());
        //Validamos que solamente se puedan ingresar 40 articulos
        if (listaArticulos.size() >= MaxSKU ) {
            MensajeAviso("No se puede agregar el producto seleccionado,ha alcanzado el limite de "+ String.valueOf(MaxSKU) +" productos por pedido.");
            return false;
        }

        itemPedidos.put("CodigoPedido", pedido.getCodigoPedido());
        itemPedidos.put("CodigoArticulo", articulo.getCodigo());
        //itemPedidos.put("Cod", articulo.getCodigo().split("-")[articulo.getCodigo().split("-").length - 1]);
        itemPedidos.put("Cod", articulo.getCodigo());
        itemPedidos.put("Cantidad", txtCantidad.getText().toString().trim());
        itemPedidos.put("Um", vUM.trim());
        itemPedidos.put("Precio", String.valueOf(Precio));
        itemPedidos.put("TipoPrecio", vTipoPrecio);
        itemPedidos.put("Descripcion", DescripcionArt);
        itemPedidos.put("CodUM", vCodUM);
        itemPedidos.put("Unidades",String.valueOf(Double.parseDouble(txtCantidad.getText().toString())));
        itemPedidos.put("Costo", String.valueOf(Double.parseDouble(articulo.getCosto())));
        itemPedidos.put("PorDescuento", String.valueOf(Double.parseDouble("0")));
        itemPedidos.put("PorDescuentoOriginal", String.valueOf(Double.parseDouble("0")));
        itemPedidos.put("TipoArt", "P");
        itemPedidos.put("BonificaA", "");
        //itemPedidos.put("PorIva", articulo.getPorIva());
        double subtotal, iva, total, descuento, porIva;
        subtotal = Double.parseDouble(itemPedidos.get("Precio")) * Double.parseDouble(itemPedidos.get("Cantidad"));
        descuento = subtotal * (Double.parseDouble(itemPedidos.get("PorDescuento")) / 100);
        subtotal = subtotal - descuento;

        if (variables_publicas.AplicaIVAGral.equalsIgnoreCase("1")){
            if (cliente.getExcento().equalsIgnoreCase("1")){
                porIva=0;
            }else{
                if (Double.parseDouble(articulo.getPorIva())==0){
                    porIva=0;
                }else{
                    porIva=Double.parseDouble(articulo.getPorIva());
                }
            }
        }else{
            porIva=0;
        }


        //porIva = Double.parseDouble(articulo.getPorIva());
        iva = subtotal * porIva;
        total = subtotal + iva;
        itemPedidos.put("Descuento", df.format(descuento));
        itemPedidos.put("PorcentajeIva",String.valueOf(porIva));
        itemPedidos.put("Iva", df.format(iva));
        itemPedidos.put("SubTotal", df.format(subtotal));
        itemPedidos.put("Total", df.format(total));
        itemPedidos.put("IdProveedor", articulo.getIdProveedor());
        itemPedidos.put("UnidadCajaVenta", articulo.getUnidadCajaVenta());
        itemPedidos.put("Bodega", "01");

        MaxSKU= Integer.parseInt(variables_publicas.usuario.getRutaForanea()== null ? "1" :variables_publicas.usuario.getRutaForanea());

        //Validamos que solamente se puedan ingresar 40 articulos
        if (listaArticulos.size() >= MaxSKU ) {
            MensajeAviso("No se puede agregar el producto seleccionado,ya que excede el limite de "+ String.valueOf(MaxSKU) +" productos para un pedido.");
            return false;
        }

        listaArticulos.add(itemPedidos);

        PrecioItem = 0;
        RefrescarGrid();
        CalcularTotales();
        return true;

    }

    private void RefrescarGrid() {
        adapter = new SimpleAdapter(
                getApplicationContext(), listaArticulos,
                R.layout.pedidos_list_item, new
                String[]{"Cod", "Cantidad","Um", "Precio", "TipoPrecio", "Descripcion", "PorDescuento","Unidades","CodUM", "Descuento", "SubTotal", "Iva", "Total"}, new
                int[]{R.id.lblDetalleCodProducto, R.id.lblDetalleCantidad, R.id.lblDetalleUM, R.id.lblDetallePrecio, R.id.lblDetalleTipoPrecio, R.id.lblDetalleDescripcion, R.id.lblDetallePorDescuento, R.id.lblDetalleUnidades, R.id.lblDetalleCodUM, R.id.lblDetalleDescuento, R.id.lblDetalleSubTotal, R.id.lblDetalleIva, R.id.lblDetalleTotal}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View currView = super.getView(position, convertView, parent);
                HashMap<String, String> currItem = (HashMap<String, String>) getItem(position);
                if (currItem.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_Descripcion).startsWith("**")) {
                    currView.setBackgroundColor(Color.RED);
                } else {
                    currView.setBackgroundColor(Color.WHITE);
                }
                return currView;
            }
        };

        lv.setAdapter(adapter);
    }

    private void CalcularTotales() {

        double iva = 0, descuento = 0;
        total = 0;
        subtotal = 0;
        for (int i = 0; i < listaArticulos.size(); i++) {
            HashMap<String, String> item = listaArticulos.get(i);

            try {
                subtotal += (df.parse(item.get("SubTotal"))).doubleValue();
                iva += (df.parse(item.get("Iva"))).doubleValue();
                total += (df.parse(item.get("Total"))).doubleValue();
            } catch (ParseException e) {
                MensajeAviso(e.getMessage());
            }
        }
        lblSubTotalCor.setText(df.format(subtotal));
        lblIvaCor.setText(df.format(iva));
        lblTotalCor.setText(df.format(total));

        if (tasaCambio > 0) {
            lblSubTotalDol.setText(String.valueOf(df.format(subtotal / tasaCambio)));
            lblIvaDol.setText(String.valueOf(df.format(iva / tasaCambio)));
            lblTotalDol.setText(String.valueOf(df.format(total / tasaCambio)));
        }
        lblFooter.setText("Total items:" + String.valueOf(listaArticulos.size()));

    }

    private void ValidarIVA() {

        double iva = 0, descuento = 0;
        total = 0;
        subtotal = 0;
        for (int i = 0; i < listaArticulos.size(); i++) {
            HashMap<String, String> item = listaArticulos.get(i);

            try {
                subtotal += (df.parse(item.get("SubTotal"))).doubleValue();
                iva += (df.parse(item.get("Iva"))).doubleValue();
                total += (df.parse(item.get("Total"))).doubleValue();
            } catch (ParseException e) {
                MensajeAviso(e.getMessage());
            }
        }
        lblSubTotalCor.setText(df.format(subtotal));
        lblIvaCor.setText(df.format(iva));
        lblTotalCor.setText(df.format(total));

        if (tasaCambio > 0) {
            lblSubTotalDol.setText(String.valueOf(df.format(subtotal / tasaCambio)));
            lblIvaDol.setText(String.valueOf(df.format(iva / tasaCambio)));
            lblTotalDol.setText(String.valueOf(df.format(total / tasaCambio)));
        }
        lblFooter.setText("Total items:" + String.valueOf(listaArticulos.size()));

    }

    public void MensajeAviso(String texto) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(texto);
        dlgAlert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (finalizar) {
                    finish();
                }
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public void MostrarMensajeGuardar() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = null;
        dialogBuilder.setCancelable(false);
        if (guardadoOK) {
            dialogView = inflater.inflate(R.layout.dialog_ok_layout, null);

            Button btnOK = (Button) dialogView.findViewById(R.id.btnOkDialogo);
            btnOK.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {

            dialogView = inflater.inflate(R.layout.offline_layout, null);
            dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        dialogBuilder.setView(dialogView);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

      @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    loadIMEI();
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        try {
            super.onCreateContextMenu(menu, v, menuInfo);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            HashMap<String, String> obj = (HashMap<String, String>) lv.getItemAtPosition(info.position);

            String HeaderMenu = obj.get("CodigoArticulo") + "\n" + obj.get("Descripcion");

            menu.setHeaderTitle(HeaderMenu);
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.eliminar_item_pedido, menu);
        } catch (Exception e) {
            MensajeAviso(e.getMessage());
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        try {
            final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            switch (item.getItemId()) {
                case R.id.Elimina_Item:
                    HashMap<String, String> itemArticulo = listaArticulos.get(info.position);
                    listaArticulos.remove(itemArticulo);
                    for (int i = 0; i < listaArticulos.size() - 1; i++) {
                        HashMap<String, String> a = listaArticulos.get(i);
                        if (a.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_BonificaA).equals(itemArticulo.get(variables_publicas.PEDIDOS_DETALLE_COLUMN_CodigoArticulo))) {
                            listaArticulos.remove(a);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    lv.setAdapter(adapter);
                    RefrescarGrid();
                    ValidarPreciosEscala();
                    AplicarBonificacionCombinada();
                    CalcularTotales();
                    RefrescarGrid();
                    LimipiarDatos(true);

                    return true;
                case R.id.Cantidad_Item:
                    final String[] result = {""};
                    final HashMap<String, String> itemArticulo2 = listaArticulos.get(info.position);
                    final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = null;
                    dialogBuilder.setCancelable(false);
                    dialogView = inflater.inflate(R.layout.cambiocantidad, null);
                    Button btnOK = (Button) dialogView.findViewById(R.id.btnGuardar);
                    Button btnNOK = (Button) dialogView.findViewById(R.id.btnCancelar);
                    final Spinner cbPrecio = (Spinner) dialogView.findViewById(R.id.cboPrecio);
                    final EditText txtCantidadNueva = (EditText) dialogView.findViewById(R.id.txtCantidad);
                    txtCantidadNueva.setText(itemArticulo2.get("Cantidad"));
                    txtCantidadNueva.setFocusable(true);
                    txtCantidadNueva.selectAll();

                    List<TipoPrecio> listTPrecio = TPreciosH.ObtenerTipoPrecio();
                    ArrayAdapter<TipoPrecio> adapterTPrecio = new ArrayAdapter<TipoPrecio>(this, android.R.layout.simple_spinner_item, listTPrecio);
                    adapterTPrecio.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cbPrecio.setAdapter(adapterTPrecio);
                    codTipoPrecio = listTPrecio.get(0);
                    for (int i = 0; !(codTipoPrecio.getCod_Tipo_Precio().equals(cliente.getTipo())); i++)
                        codTipoPrecio = listTPrecio.get(i);
                    cbPrecio.setSelection(adapterTPrecio.getPosition(codTipoPrecio));

                    cbPrecio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                            // On selecting a spinner item
                            codTipoPrecio = (TipoPrecio) adapter.getItemAtPosition(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                    dialogBuilder.setView(dialogView);

                    final  AlertDialog alertDialog = dialogBuilder.create();

                    btnOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (txtCantidadNueva.getText().toString().equals("") || txtCantidadNueva.getText().toString().isEmpty() || txtCantidadNueva.equals("0")) {
                                MensajeAviso("Debe ingresar un valor mayor a cero.");
                                return;
                            }
                            double subtotal, iva, total, descuento, porIva;
                            String vprecio="0";
                            itemArticulo2.put("Cantidad", txtCantidadNueva.getText().toString());

                            String tmpcodigoart =itemArticulo2.get("CodigoArticulo");
                            List<Articulo> precios = TPreciosH.ObtenerPrecioPorUM(tmpcodigoart);
                            if (precios.size()==0){
                                vTipoPrecio = cliente.getTipoPrecio();
                            }else {
                                for (int i = 0; i < precios.size(); i++) {
                                    if (codTipoPrecio.getCod_Tipo_Precio().toString().equalsIgnoreCase("1")){
                                        vprecio=precios.get(i).getPrecio();
                                        vTipoPrecio=ClientesH.ObtenerDescripcion(variables_publicas.TPRECIOS_COLUMN_TIPO_PRECIO,variables_publicas.TABLE_TPRECIOS,variables_publicas.TPRECIOS_COLUMN_COD_TIPO_PRECIO,"1");
                                    }else if (codTipoPrecio.getCod_Tipo_Precio().toString().equalsIgnoreCase("2")){
                                        vprecio=precios.get(i).getPrecio2();
                                        vTipoPrecio=ClientesH.ObtenerDescripcion(variables_publicas.TPRECIOS_COLUMN_TIPO_PRECIO,variables_publicas.TABLE_TPRECIOS,variables_publicas.TPRECIOS_COLUMN_COD_TIPO_PRECIO,"2");
                                    }else if (codTipoPrecio.getCod_Tipo_Precio().toString().equalsIgnoreCase("3")){
                                        vprecio=precios.get(i).getPrecio3();
                                        vTipoPrecio=ClientesH.ObtenerDescripcion(variables_publicas.TPRECIOS_COLUMN_TIPO_PRECIO,variables_publicas.TABLE_TPRECIOS,variables_publicas.TPRECIOS_COLUMN_COD_TIPO_PRECIO,"3");
                                    }else{
                                        vprecio=precios.get(i).getPrecio4();
                                        vTipoPrecio=ClientesH.ObtenerDescripcion(variables_publicas.TPRECIOS_COLUMN_TIPO_PRECIO,variables_publicas.TABLE_TPRECIOS,variables_publicas.TPRECIOS_COLUMN_COD_TIPO_PRECIO,"4");
                                    }
                                }
                            }

                            itemArticulo2.put("Precio",vprecio);
                            itemArticulo2.put("TipoPrecio",vTipoPrecio);
                            subtotal = Double.parseDouble(itemArticulo2.get("Precio")) * Double.parseDouble(itemArticulo2.get("Cantidad"));
                            descuento = subtotal * (Double.parseDouble(itemArticulo2.get("PorDescuento")) / 100);
                            subtotal = subtotal - descuento;

                            if (variables_publicas.AplicaIVAGral.equalsIgnoreCase("1")){
                                if (cliente.getExcento().equalsIgnoreCase("1")){
                                    porIva=0;
                                }else{
                                    if (Double.parseDouble(articulo.getPorIva())==0){
                                        porIva=0;
                                    }else{
                                        porIva=Double.parseDouble(articulo.getPorIva());
                                    }
                                }
                            }else{
                                porIva=0;
                            }
                            iva = subtotal * porIva;
                            total = subtotal + iva;
                            itemArticulo2.put("Descuento", df.format(descuento));
                            itemArticulo2.put("PorcentajeIva",String.valueOf(porIva));
                            itemArticulo2.put("Iva", df.format(iva));
                            itemArticulo2.put("SubTotal", df.format(subtotal));
                            itemArticulo2.put("Total", df.format(total));

                            listaArticulos.set(info.position,itemArticulo2);
                            adapter.notifyDataSetChanged();
                            lv.setAdapter(adapter);
                            RefrescarGrid();
                            ValidarPreciosEscala();
                            AplicarBonificacionCombinada();
                            CalcularTotales();
                            //RefrescarGrid();
                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            View focusedView = PedidosActivity.this.getCurrentFocus();
                            if (focusedView != null) {
                                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                            alertDialog.dismiss();
                        }
                    });

                    btnNOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });


                    alertDialog.show();
                    return  true;
                default:
                    return super.onContextItemSelected(item);
            }
        } catch (Exception e) {
            MensajeAviso(e.getMessage());
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmación Requerida")
                .setMessage("¿Está seguro que desea cancelar el pedido actual?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PedidosActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    //endregion


    private class SincronizardorPedidos extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PedidosActivity.this);
            pDialog.setMessage("Guardando pedido, por favor espere...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            CheckConnectivity();
            if (isOnline) {
                if (Boolean.parseBoolean(SincronizarDatos.SincronizarPedido(PedidoH, PedidoDetalleH, vendedor, cliente, pedido.getCodigoPedido(), jsonPedido, (editar == true && pedidoLocal == false)).split(",")[0])) {
                    guardadoOK = true;
                }
            } else {
                guardadoOK = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (PedidosActivity.this.isFinishing() == false) {
                MostrarMensajeGuardar();
            }

        }
    }

    private class ConsultarExistencias extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                CheckConnectivity();
                if (isOnline) {
                    existencia = SincronizarDatos.ConsultarExistencias(PedidosActivity.this, PedidoH, ArticulosH, articulo.getCodigo());
                }
            } catch (Exception ex) {
                Log.e("error", ex.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (existencia != "N/A") {
                lblExistentias.setText(String.valueOf((int) (Double.parseDouble(existencia))));
            } else {
                lblExistentias.setText(articulo.getExistencia());
            }
        }
    }

    private class GetLatestVersion extends AsyncTask<Void, Void, Void> {
        String latestVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                CheckConnectivity();
                if (isOnline) {
                    Document doc2 = Jsoup
                            .connect(
                                    "https://play.google.com/store/apps/details?id=com.saf.sistemas.safasuncion")
                            .get()
                            ;

                    Elements Version = doc2.select(".htlgb ");

                    for (int i = 0; i < 7 ; i++) {
                        latestVersion = Version.get(i).text();
                        if (Pattern.matches("^[0-9]{1}.[0-9]{1}.[0-9]{1}$", latestVersion)) {
                            break;
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
          /*  if (pDialog.isShowing())
                pDialog.dismiss();
*/
            String currentVersion = getCurrentVersion();
            variables_publicas.VersionSistema = currentVersion;
            if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(PedidosActivity.this);
                builder.setTitle("Nueva versión disponible");
                builder.setMessage("Es necesario actualizar la aplicación para poder continuar.");
                builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click button action
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.saf.sistemas.safdiscomert&hl=es")));
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                if (isFinishing()) {
                    return;
                }
                builder.show();
            }
        }


    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            String nn=spinner.getItemAtPosition(i).toString();

            if (nn.equals(myString)){
                index = i;
                break;
            }
        }
        return index;
    }
    private class GetValorConfig extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String urlString = urlGetConfiguraciones;

            String jsonStr = sh.makeServiceCall(urlString);

            Log.e(TAG, "Response from url: " + jsonStr);

            /**********************************USUARIOS**************************************/
            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray Usuarios = jsonObj.getJSONArray("GetConfiguracionesResult");

                    for (int i = 0; i < Usuarios.length(); i++) {
                        JSONObject c = Usuarios.getJSONObject(i);
                        String Valor = c.getString("Valor");
                        String Configuracion = c.getString("Configuracion");
                        String ConfigVDatos = "VersionDatos";
                        if (Configuracion.equals(ConfigVDatos)) {
                            variables_publicas.ValorConfigServ = Valor;

                            int ValorConfigLocal = Integer.parseInt(variables_publicas.Configuracion.getValor());
                            int ValorConfigServidor = Integer.parseInt(variables_publicas.ValorConfigServ);

                            if (ValorConfigLocal < ValorConfigServidor) {
                                sd.SincronizarTablas();
                            }

                        }
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "No se ha podido establecer contacto con el servidor");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "No se ha podido establecer contacto con el servidor",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {

                Log.e(TAG, "No se ha podido establecer contacto con el servidor");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "No se ha podido establecer contacto con el servidor",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }
    }
    //endregion


    @Override
    protected void onResume() {
        super.onResume();
    }
}


