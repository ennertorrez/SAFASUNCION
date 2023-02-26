package com.saf.sistemas.safasuncion.Principal;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.saf.sistemas.safasuncion.AccesoDatos.TPreciosHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.UsuariosHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.VendedoresHelper;
import com.saf.sistemas.safasuncion.AccesoDatos.RutasHelper;
import com.saf.sistemas.safasuncion.Auxiliar.Funciones;
import com.saf.sistemas.safasuncion.Auxiliar.SincronizarDatos;
import com.saf.sistemas.safasuncion.Auxiliar.variables_publicas;
import com.saf.sistemas.safasuncion.Entidades.Ruta;
import com.saf.sistemas.safasuncion.Entidades.Usuario;
import com.saf.sistemas.safasuncion.HttpHandler;
import com.saf.sistemas.safasuncion.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
public class Login extends Activity {
    private static final int REQUEST_READ_PHONE_STATE = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    private String TAG = Login.class.getSimpleName();
    private Button btnIngresar;
    private EditText txtUsuario;
    private EditText txtPassword;
    private Spinner cboRutas;
    private String Usuario = "";
    private String Contrasenia = "";
    private ProgressDialog pDialog;
    private String tipoBusqueda = "3";
    private boolean isOnline=false;
    private Ruta vRuta;
    private int iCurrentSelection=0;
    // URL to get contacts JSON

    final String url = variables_publicas.direccionIp + "/ServicioLogin.svc/BuscarUsuario/";
    final String urlGetConfiguraciones = variables_publicas.direccionIp + "/ServicioPedidos.svc/GetConfiguraciones";
    final String urlRutasUsr = variables_publicas.direccionIp + "/ServicioClientes.svc/GetRutasUsr/";

    private DataBaseOpenHelper DbOpenHelper;

    private UsuariosHelper UsuariosH;
    private ClientesHelper ClientesH;
    private VendedoresHelper VendedoresH;
    private RutasHelper RutasH;
    private TPreciosHelper TPreciosH;
    private CategoriasClienteHelper CategoriaH;
    private PromocionesHelper PromocionesH;
    private FormaPagoHelper FormaPagoH;
    private ConfiguracionSistemaHelper ConfigH;
    private ClientesSucursalHelper ClientesSucH;
    private ArticulosHelper ArticulosH;
    private PedidosDetalleHelper PedidoDetalleH;
    private PedidosHelper PedidoH;
    private EscalaPreciosHelper EscalaPreciosH;
    private SincronizarDatos sd;
    String MsjLoging = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.iniciosesion);

        //Esto sirve para permitir realizar conexion a internet en el Hilo principal

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        MsjLoging = variables_publicas.MensajeLogin;
        if (MsjLoging != "") {
            mensajeAviso(MsjLoging);
        }

        vRuta=new Ruta();
        DbOpenHelper = new DataBaseOpenHelper(Login.this);
        ClientesH = new ClientesHelper(DbOpenHelper.database);
        UsuariosH = new UsuariosHelper(DbOpenHelper.database);
        VendedoresH = new VendedoresHelper(DbOpenHelper.database);
        ConfigH = new ConfiguracionSistemaHelper(DbOpenHelper.database);
        ClientesSucH = new ClientesSucursalHelper(DbOpenHelper.database);
        PromocionesH = new PromocionesHelper(DbOpenHelper.database);
        FormaPagoH = new FormaPagoHelper(DbOpenHelper.database);
        ArticulosH = new ArticulosHelper(DbOpenHelper.database);
        UsuariosH = new UsuariosHelper(DbOpenHelper.database);
        PedidoH = new PedidosHelper(DbOpenHelper.database);
        RutasH = new RutasHelper(DbOpenHelper.database);
        TPreciosH = new TPreciosHelper(DbOpenHelper.database);
        CategoriaH = new CategoriasClienteHelper(DbOpenHelper.database);
        PedidoDetalleH = new PedidosDetalleHelper(DbOpenHelper.database);
        EscalaPreciosH = new EscalaPreciosHelper(DbOpenHelper.database);

        sd = new SincronizarDatos(DbOpenHelper, ClientesH, VendedoresH,PromocionesH,
                FormaPagoH,
                ConfigH, ClientesSucH, ArticulosH, UsuariosH,PedidoH,PedidoDetalleH,CategoriaH,TPreciosH,RutasH,EscalaPreciosH);

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        cboRutas = (Spinner) findViewById(R.id.cboRutas);


        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    CheckConnectivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        txtUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //System.out.println(s.toString() + " " + start + " " + count + " " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    if(isOnline){
                        if (Build.VERSION.SDK_INT >= 11) {
                            //--post GB use serial executor by default --
                            new GetRutasUsuario().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        } else {
                            //--GB uses ThreadPoolExecutor by default--
                            new GetRutasUsuario().execute();
                        }
                    }
                }else {
                    if(isOnline){
                        if (Build.VERSION.SDK_INT >= 11) {
                            //--post GB use serial executor by default --
                            new GetRutasTotal().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        } else {
                            //--GB uses ThreadPoolExecutor by default--
                            new GetRutasTotal().execute();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Usuario UltimoUsuario = UsuariosH.BuscarUltimoUsuario();
        if (UltimoUsuario != null) {

            txtUsuario.setText(UltimoUsuario.getUsuario());
            final List<Ruta> CRuta;
            CRuta = RutasH.ObtenerRutaVendedor(Integer.parseInt(UltimoUsuario.getCodigo()));

            ArrayAdapter<Ruta> adapterRuta = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CRuta);
            adapterRuta.setDropDownViewResource(android.R.layout.simple_list_item_checked);
            cboRutas.setAdapter(adapterRuta);
            if (!adapterRuta.isEmpty()) {
                cboRutas.setSelection(0);
            }
            txtPassword.requestFocus();
        }

        TextView lblVersion = (TextView) findViewById(R.id.login_version);
        lblVersion.setText("Versión " + getCurrentVersion());
        /*NO CAMBIAR ESTA DIRECCION: VERIFICA SI ES SERVIDOR DE PRUEBAS*/
//        if (variables_publicas.direccionIp != "http://190.212.127.107:8088") {
        if (variables_publicas.direccionIp != "http://190.212.238.22:8088") {
            lblVersion.setText("Versión " + getCurrentVersion() + " Desarrollo");
        }
        txtPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    btnIngresar.performClick();
                }
                return false;
            }
        });
        btnIngresar = (Button) findViewById(R.id.btnIngresar);

        iCurrentSelection = cboRutas.getSelectedItemPosition();
        cboRutas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                vRuta = (Ruta) adapter.getItemAtPosition(position);
                variables_publicas.rutacargada=vRuta.getIDRUTA();
                variables_publicas.rutacargadadescripcion=vRuta.getRUTA();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                inputMethodManager.hideSoftInputFromWindow(txtUsuario.getWindowToken(), 0);
                inputMethodManager.hideSoftInputFromWindow(txtPassword.getWindowToken(), 0);

               Funciones.GetLocalDateTime();

                Usuario = txtUsuario.getText().toString();
                Contrasenia = txtPassword.getText().toString();

                if (TextUtils.isEmpty(Usuario)) {
                    txtUsuario.setError("Ingrese el nombre de usuario");
                    return;
                }
                if (TextUtils.isEmpty(Contrasenia)) {
                    txtPassword.setError("Ingrese la contraseña");
                    return;
                }

                //Esto sirve para permitir realizar conexion a internet en el Hilo principal

                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                isOnline =Funciones.TestServerConectivity();

                variables_publicas.usuario = UsuariosH.BuscarUsuarios(Usuario, Contrasenia);
                String VersionDatos = "VersionDatos";
                variables_publicas.Configuracion = ConfigH.BuscarValorConfig(VersionDatos);

                if(isOnline){
                    if (Build.VERSION.SDK_INT >= 11) {
                        //--post GB use serial executor by default --
                        new GetValorConfig().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    } else {
                        //--GB uses ThreadPoolExecutor by default--
                        new GetValorConfig().execute();
                    }
                }
                if (!isOnline && variables_publicas.usuario != null) {
                    variables_publicas.MensajeLogin = "";
                    variables_publicas.LoginOk = true;
                    Intent intent = new Intent("android.intent.action.Barra_cargado");
                    startActivity(intent);
                    variables_publicas.rutacargada=vRuta.getIDRUTA();
                    finish();
                } else if (!isOnline && variables_publicas.usuario == null) {
                    mensajeAviso("Usuario o contraseña invalido\n O para conectar un nuevo usuario debe conectarse a internet");
                }
            }
        });
        variables_publicas.usuario = UltimoUsuario;
        ValidarUltimaVersion();
        loadIMEI();
        try {
            Configuration config = getResources().getConfiguration();
            restartInLocale(config.locale);
        } catch (Exception ex) {
            Funciones.MensajeAviso(getApplicationContext(), ex.getMessage());
        }

    }

    private void restartInLocale(Locale locale) {
        if (!locale.getDisplayName().equalsIgnoreCase("español (Estados Unidos)")) {
            locale = new Locale("es", "US");
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            Resources resources = getResources();
            resources.updateConfiguration(config, resources.getDisplayMetrics());
            recreate();
        }


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
            new AlertDialog.Builder(Login.this)
                    .setTitle("Permission Request")
                    .setMessage("Se necesita permiso para acceder al estado del telefono")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(Login.this,
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            // Received permission result for READ_PHONE_STATE permission.est.");
            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // READ_PHONE_STATE permission has been granted, proceed with displaying IMEI Number
                //alertAlert(getString(R.string.permision_available_read_phone_state));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // READ_PHONE_STATE permission has not been granted.
                } else {
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
            } else {
                alertAlert("Se necesita permiso para acceder al estado del telefono");
            }
        }
    }

    private void alertAlert(String msg) {
        new AlertDialog.Builder(Login.this)
                .setTitle("Permission Request")
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do somthing here
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void ValidarUltimaVersion() {
        String currentVersion = getCurrentVersion();
        variables_publicas.VersionSistema = currentVersion;

            String latestVersion = "";
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
    public class GetClientes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            //SINCRONIZAR DATOS
            try {
                sd.SincronizarTablaClientes();
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
//
                variables_publicas.LoginOk = false;
                variables_publicas.MensajeLogin = "Ha ocurrido un error al sincronizar los datos de clientes. Por favor intente nuevamente";
                UsuariosH.EliminaUsuarios();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
    //region ObtieneUsuario
    public class GetUser extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            Intent intent = new Intent("android.intent.action.Barra_cargado");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            //************USUARIOS
            HttpHandler sh = new HttpHandler();
            String urlString = url + Usuario + "/" + Funciones.Codificar(Contrasenia);
            String encodeUrl = "";
            try {
                URL Url = new URL(urlString);
                URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
                encodeUrl = uri.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }


            String jsonStr = sh.makeServiceCall(encodeUrl);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray Usuarios = jsonObj.getJSONArray("BuscarUsuarioResult");
                    if (Usuarios.length() == 0) {
                        variables_publicas.LoginOk = false;
                        variables_publicas.MensajeLogin = "Usuario o contraseña invalido";
                        return null;
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

                        variables_publicas.LoginOk = true;
                        variables_publicas.MensajeLogin = "";
                        variables_publicas.usuario = UsuariosH.BuscarUsuarios(Usuario, Contrasenia);

                        //SINCRONIZAR DATOS
                        try {
                            sd.SincronizarTodo();
                        } catch (final JSONException e) {
                            Log.e(TAG, "Json parsing error: " + e.getMessage());
//
                            variables_publicas.LoginOk = false;
                            variables_publicas.MensajeLogin = "Ha ocurrido un error al sincronizar los datos. Por favor intente nuevamente";
                            UsuariosH.EliminaUsuarios();
                        }
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "error: " + "No se ha podido establecer contacto con el servidor");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "error: " + e.getMessage(),
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
                                "error: " + "No se ha podido establecer contacto con el servidor",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog!=null && pDialog.isShowing())
                pDialog.dismiss();
        }
    }

    //endregion
//region ObtienerRutas
    public class GetRutasTotal extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String urlString = variables_publicas.direccionIp + "/ServicioClientes.svc/GetRutasTotal/";
            String encodeUrl = "";
            try {
                URL Url = new URL(urlString);
                URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
                encodeUrl = uri.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }


            String jsonStr = sh.makeServiceCall(encodeUrl);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray Rutas = jsonObj.getJSONArray("GetRutasTotalResult");
                    if (Rutas.length() == 0) {
                        return null;
                    }
                    RutasH.EliminaRutas();
                    // looping through All Contacts


                    for (int i = 0; i < Rutas.length(); i++) {
                        JSONObject c = Rutas.getJSONObject(i);

                        String idruta = c.getString("IDRUTA");
                        String ruta = c.getString("RUTA");
                        String vendedor = c.getString("VENDEDOR");

                        RutasH.GuardarRutas(idruta, ruta, vendedor);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "error: " + "No se ha podido establecer contacto con el servidor");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "error: " + e.getMessage(),
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
                                "error: " + "No se ha podido establecer contacto con el servidor",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            CargarComboRuta();
            /*if (pDialog!=null && pDialog.isShowing())
                pDialog.dismiss();*/
        }
    }
    public class GetRutasUsuario extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            String urlString = urlRutasUsr  + txtUsuario.getText();
            String encodeUrl = "";
            try {
                URL Url = new URL(urlString);
                URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
                encodeUrl = uri.toURL().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }


            String jsonStr = sh.makeServiceCall(encodeUrl);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray Rutas = jsonObj.getJSONArray("GetRutasUsrResult");
                    if (Rutas.length() == 0) {
                        return null;
                    }
                    RutasH.EliminaRutas();
                    // looping through All Contacts


                    for (int i = 0; i < Rutas.length(); i++) {
                        JSONObject c = Rutas.getJSONObject(i);

                        String idruta = c.getString("IDRUTA");
                        String ruta = c.getString("RUTA");
                        String vendedor = c.getString("VENDEDOR");

                        RutasH.GuardarRutas(idruta, ruta, vendedor);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "error: " + "No se ha podido establecer contacto con el servidor");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "error: " + e.getMessage(),
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
                                "error: " + "No se ha podido establecer contacto con el servidor",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            CargarComboRuta();
            /*if (pDialog!=null && pDialog.isShowing())
                pDialog.dismiss();*/
        }
    }
    //region ObtieneValorConfiguracion
    private class GetValorConfig extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            HttpHandler sh = new HttpHandler();
            //String urlString = urlGetConfiguraciones;

            String jsonStr = sh.makeServiceCall(urlGetConfiguraciones) ;

            Log.e(TAG, "Response from url: " + jsonStr);


            if (jsonStr != null) {

                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray conf = jsonObj.getJSONArray("GetConfiguracionesResult");

                    for (int i = 0; i < conf.length(); i++) {
                        JSONObject c = conf.getJSONObject(i);
                        String Valor = c.getString("Valor");
                        String Configuracion = c.getString("Configuracion");
                        String ConfigVDatos = "VersionDatos";
                        if (Configuracion.equals(ConfigVDatos)) {
                            variables_publicas.ValorConfigServ = Valor;
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isOnline && variables_publicas.usuario != null && variables_publicas.Configuracion != null) {
                try {

                    String FechaLocal = variables_publicas.usuario.getFechaActualiza();
                    String FechaActual = Funciones.getDatePhone();
                    int ValorConfigLocal = Integer.parseInt(variables_publicas.Configuracion.getValor());
                    int ValorConfigServidor = Integer.parseInt(variables_publicas.ValorConfigServ);
                    if (!FechaLocal.equals(FechaActual) || ValorConfigLocal < ValorConfigServidor) {

                        if (Build.VERSION.SDK_INT >= 11) {
                            //--post GB use serial executor by default --
                            new GetUser().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        } else {
                            //--GB uses ThreadPoolExecutor by default--
                            new GetUser().execute();
                           }


                    } else {

                        if (Build.VERSION.SDK_INT >= 11) {
                            //--post GB use serial executor by default --
                            new GetUser().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        } else {
                            //--GB uses ThreadPoolExecutor by default--
                            new GetUser().execute();
                        }

                        if (Build.VERSION.SDK_INT >= 11) {
                            //--post GB use serial executor by default --
                            new GetClientes().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                        } else {
                            //--GB uses ThreadPoolExecutor by default--
                            new GetClientes().execute();
                        }
                        variables_publicas.MensajeLogin = "";
                        variables_publicas.LoginOk = true;
                        variables_publicas.rutacargada= vRuta.getIDRUTA();
                        Intent intent = new Intent("android.intent.action.Barra_cargado");
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    mensajeAviso(e.getMessage());
                }

            } else if (isOnline && (variables_publicas.usuario == null || variables_publicas.Configuracion == null)) {
                if (Build.VERSION.SDK_INT >= 11) {
                    //--post GB use serial executor by default --
                    new GetUser().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                } else {
                    //--GB uses ThreadPoolExecutor by default--
                    new GetUser().execute();
                }
            }



        }
    }
    //endregion




    public void mensajeAviso(String texto) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(texto);
        dlgAlert.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }





    private class GetLatestVersion extends AsyncTask<Void, Void, Void> {
        String latestVersion;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog

            /*
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("consultando version del sistema, por favor espere...");
            pDialog.setCancelable(false);
            pDialog.show();*/
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                CheckConnectivity();
                if(isOnline){
                    //It retrieves the latest version by scraping the content of current version from play store at runtime

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


            String currentVersion = getCurrentVersion();
            variables_publicas.VersionSistema = currentVersion;
            if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                builder.setTitle("Nueva version disponible");
                builder.setMessage("Es necesario actualizar la aplicacion para poder continuar.");
                builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click button action
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.saf.sistemas.safasuncion&hl=es")));
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                if(isFinishing()){return;}
                builder.show();
            }
        }


    }

    private void CheckConnectivity() {
        isOnline = Funciones.TestServerConectivity();
    }

    private void CargarComboRuta(){
        final List<Ruta> CRutas;
        CRutas= RutasH.ObtenerListaRutas();

        ArrayAdapter<Ruta> adapterRutas = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CRutas);
        adapterRutas.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        cboRutas.setAdapter(adapterRutas);
        if (!adapterRutas.isEmpty()) {
            cboRutas.setSelection(0);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if (pDialog!=null)
            pDialog.dismiss();
    }
}
