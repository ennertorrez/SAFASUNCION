package com.suplidora.sistemas.sisago.Principal;


import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import android.os.Handler;

import com.suplidora.sistemas.sisago.AccesoDatos.ArticulosHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.CartillasBcDetalleHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.CartillasBcHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.ClientesHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.ClientesSucursalHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.ConfiguracionSistemaHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.DataBaseOpenHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.DescuentoEspecialHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.FacturasPendientesHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.FormaPagoHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.InformesDetalleHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.InformesHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.PedidosDetalleHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.PedidosHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.PrecioEspecialCanalHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.PrecioEspecialHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.UsuariosHelper;
import com.suplidora.sistemas.sisago.AccesoDatos.VendedoresHelper;
import com.suplidora.sistemas.sisago.Auxiliar.Funciones;
import com.suplidora.sistemas.sisago.Auxiliar.SincronizarDatos;
import com.suplidora.sistemas.sisago.Auxiliar.variables_publicas;
import com.suplidora.sistemas.sisago.Informes.InformesActivity;
import com.suplidora.sistemas.sisago.Menu.ClientesFragment;
import com.suplidora.sistemas.sisago.Clientes.ClientesNew;
import com.suplidora.sistemas.sisago.Menu.ClientesInactivosFragment;
import com.suplidora.sistemas.sisago.Menu.FacturasMoraClienteFragment;
import com.suplidora.sistemas.sisago.Menu.HistoricoventasClienteFragment;
import com.suplidora.sistemas.sisago.Menu.ListaDevolucionesFragment;
import com.suplidora.sistemas.sisago.Menu.ListaInformesFragment;
import com.suplidora.sistemas.sisago.Menu.ListaPedidosFragment;
import com.suplidora.sistemas.sisago.Menu.ListaPedidosSupFragment;
import com.suplidora.sistemas.sisago.Menu.ListaPedidovsFacturado;
import com.suplidora.sistemas.sisago.Menu.ListaRecibosPendFragment;
import com.suplidora.sistemas.sisago.Menu.MaestroProductoFragment;
import com.suplidora.sistemas.sisago.Menu.MapViewFragment;
import com.suplidora.sistemas.sisago.Menu.PedidosFragment;
import com.suplidora.sistemas.sisago.Menu.PromocionesFragment;
import com.suplidora.sistemas.sisago.Menu.PromocionesSisaFragment;
import com.suplidora.sistemas.sisago.Menu.reporteComisionesFragment;
import com.suplidora.sistemas.sisago.R;

import org.json.JSONException;

public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Handler handler = new Handler();
    private String TAG = ClientesFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    TextView lblUsuarioHeader;
    TextView lblUsuarioHeaderCodigo;
    TextView lblVersion;
    TextView lblServidor;
    private DataBaseOpenHelper DbOpenHelper;

    private SincronizarDatos sd;
    private UsuariosHelper UsuariosH;
    private ClientesHelper ClientesH;
    private VendedoresHelper VendedoresH;
    private CartillasBcHelper CartillasBcH;
    private CartillasBcDetalleHelper CartillasBcDetalleH;
    private FormaPagoHelper FormaPagoH;
    private PrecioEspecialHelper PrecioEspecialH;
    private PrecioEspecialCanalHelper PrecioEspecialCanalH;
    private DescuentoEspecialHelper DescuentoEspeciallH;
    private ConfiguracionSistemaHelper ConfigH;
    private ClientesSucursalHelper ClientesSucH;
    private ArticulosHelper ArticulosH;
    private PedidosDetalleHelper PedidoDetalleH;
    private InformesHelper InformesH;
    private InformesDetalleHelper InformesDetalleH;
    private FacturasPendientesHelper FacturasPendientesH;
    private PedidosHelper PedidoH;

    String IMEI = "";
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = true;
    protected LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //SE ACTIVA EL GPS DEL CELULAR
       /* Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(myIntent);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            new AlertDialog.Builder(this)
                    .setTitle("Confirmación Requerida")
                    .setMessage("GPS no stá habilitado. Favor activar la localización.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .show();
        }*/
        //SE LANZA EL SERVICIO DE LOCALIZACION
/*        Intent intent = new Intent(this,GPSTracker.class);
        startService(intent);*/
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnabled) {
            showSettingsAlert();
        }

        Intent intent = new Intent(this,GPSTracker.class);
        AutoArranque ar = new AutoArranque();
        ar.onReceive(this,intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        navigationView.addHeaderView(header);
        lblUsuarioHeader = (TextView) header.findViewById(R.id.UsuarioHeader);
        lblUsuarioHeaderCodigo = (TextView) header.findViewById(R.id.UsuarioHeaderCodigo);
        lblVersion = (TextView) header.findViewById(R.id.lblVersionSistema);
        lblServidor = (TextView) header.findViewById(R.id.lblServidor);
        String userHeader = "";
        String userHeaderCodigo = "";
        String VersionSistema = "";
        String Servidor = "";

        try {
            userHeader = variables_publicas.usuario.getNombre();
            userHeaderCodigo = variables_publicas.usuario.getCodigo();
            VersionSistema = "Version: " + variables_publicas.VersionSistema;
            Servidor = variables_publicas.direccionIp.equals("http://186.1.18.75:8080") ? "SERVIDOR: PRODUCCION" : "SERVIDOR: DESARROLLO";
        } catch (Exception ex) {
            Log.e("Error:", ex.getMessage());
        }
        lblUsuarioHeader.setText(userHeader);
        lblUsuarioHeaderCodigo.setText("Codigo: " + userHeaderCodigo);
        lblVersion.setText(VersionSistema);
        lblServidor.setText(Servidor);

        DbOpenHelper = new DataBaseOpenHelper(MenuActivity.this);
        ClientesH = new ClientesHelper(DbOpenHelper.database);
        UsuariosH = new UsuariosHelper(DbOpenHelper.database);
        VendedoresH = new VendedoresHelper(DbOpenHelper.database);
        ConfigH = new ConfiguracionSistemaHelper(DbOpenHelper.database);
        ClientesSucH = new ClientesSucursalHelper(DbOpenHelper.database);
        CartillasBcH = new CartillasBcHelper(DbOpenHelper.database);
        CartillasBcDetalleH = new CartillasBcDetalleHelper(DbOpenHelper.database);
        FormaPagoH = new FormaPagoHelper(DbOpenHelper.database);
        PrecioEspecialH = new PrecioEspecialHelper(DbOpenHelper.database);
        PrecioEspecialCanalH = new PrecioEspecialCanalHelper(DbOpenHelper.database);
        DescuentoEspeciallH = new DescuentoEspecialHelper(DbOpenHelper.database);
        ArticulosH = new ArticulosHelper(DbOpenHelper.database);
        UsuariosH = new UsuariosHelper(DbOpenHelper.database);
        PedidoH = new PedidosHelper(DbOpenHelper.database);
        PedidoDetalleH = new PedidosDetalleHelper(DbOpenHelper.database);
        InformesH = new InformesHelper(DbOpenHelper.database);
        InformesDetalleH = new InformesDetalleHelper(DbOpenHelper.database);
        FacturasPendientesH = new FacturasPendientesHelper(DbOpenHelper.database);

        sd = new SincronizarDatos(DbOpenHelper, ClientesH, VendedoresH, CartillasBcH,
                CartillasBcDetalleH, FormaPagoH,PrecioEspecialH, PrecioEspecialCanalH, ConfigH, ClientesSucH,
                ArticulosH, UsuariosH, PedidoH, PedidoDetalleH,InformesH,InformesDetalleH,FacturasPendientesH,DescuentoEspeciallH);

        try {
            variables_publicas.info = "***** Usuario: " + variables_publicas.usuario.getNombre() + " / IMEI: " + (variables_publicas.IMEI == null ? "null" : variables_publicas.IMEI) + " / VersionSistema: " + variables_publicas.VersionSistema + " ******** ";
        } catch (Exception ex) {
            Log.e("error", ex.getMessage());
            ex.printStackTrace();
        }

        navigationView.getMenu().getItem(2).getSubMenu().getItem(1).setVisible(false); //Clientes nuevos
        navigationView.getMenu().getItem(2).getSubMenu().getItem(2).setVisible(false); //Activar Clientes
        navigationView.getMenu().getItem(4).setVisible(false); //Recibos

        if ((!variables_publicas.usuario.getCanal().equalsIgnoreCase("Detalle")&& variables_publicas.usuario.getTipo().equalsIgnoreCase("Vendedor")) || variables_publicas.usuario.getTipo().equalsIgnoreCase("Supervisor") || variables_publicas.usuario.getTipo().equalsIgnoreCase("User") ) {
            navigationView.getMenu().getItem(4).setVisible(true); //Recibos
            if (variables_publicas.usuario.getTipo().equalsIgnoreCase("Supervisor") || variables_publicas.usuario.getTipo().equalsIgnoreCase("User")){
                navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setVisible(false); //Agregar Recibos
                navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(true); //Listado de Recibos
                navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setVisible(true); //Estado de cuenta
                navigationView.getMenu().getItem(4).getSubMenu().getItem(3).setVisible(false); //Estado de cuenta
            }else {
                navigationView.getMenu().getItem(4).getSubMenu().getItem(0).setVisible(true); //Agregar Recibos
                navigationView.getMenu().getItem(4).getSubMenu().getItem(1).setVisible(true); //Listado de Recibos
                navigationView.getMenu().getItem(4).getSubMenu().getItem(2).setVisible(true); //Estado de cuenta
                navigationView.getMenu().getItem(4).getSubMenu().getItem(3).setVisible(true); //Estado de cuenta
            }
        }

        if ((variables_publicas.usuario.getCanal().equalsIgnoreCase("Detalle")&& variables_publicas.usuario.getTipo().equalsIgnoreCase("Vendedor")) || variables_publicas.usuario.getTipo().equalsIgnoreCase("Supervisor") || variables_publicas.usuario.getTipo().equalsIgnoreCase("User") ) {
            navigationView.getMenu().getItem(2).getSubMenu().getItem(1).setVisible(true); //Clientes nuevos
        }

        if (variables_publicas.usuario.getTipo().equalsIgnoreCase("Supervisor") || variables_publicas.usuario.getTipo().equalsIgnoreCase("User") ) {
            navigationView.getMenu().getItem(2).getSubMenu().getItem(2).setVisible(true); //Activar Clientes
        }

        if (variables_publicas.usuario.getTipo().equalsIgnoreCase("User")  ) {
            navigationView.getMenu().getItem(5).getSubMenu().getItem(3).setVisible(false); //Reporte de Comisiones
        }

        IMEI = variables_publicas.IMEI;
        if (IMEI == null) {

            new AlertDialog.Builder(this)
                    .setTitle("Confirmación Requerida")
                    .setMessage("Es necesario configurar el permiso \"Administrar llamadas telefonicas\" para porder guardar un Cliente, Desea continuar ? ")
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                variables_publicas.IMEI = tm.getImei();
            } else {
                variables_publicas.IMEI = tm.getDeviceId();
            }
        }
    }

    private void requestReadPhoneStatePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example if the user has previously denied the permission.
            new AlertDialog.Builder(MenuActivity.this)
                    .setTitle("Permission Request")
                    .setMessage("Se necesita permiso para acceder al estado del telefono")
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //re-request
                            ActivityCompat.requestPermissions(MenuActivity.this,
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            removeAllFragments(getFragmentManager());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class SincronizaDatos extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MenuActivity.this);
            pDialog.setMessage("Cargando datos, por favor espere......");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            //SINCRONIZAR DATOS
            try {

                boolean isOnline = Funciones.TestServerConectivity();
                if (isOnline) {
                    sd.SincronizarTodo();
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
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
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            mensajeAviso("Datos actualizados correctamente");
        }
    }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        try {
            int id = item.getItemId();
            if (id == R.id.SincronizarDatos) {

                if (Build.VERSION.SDK_INT >= 11) {
                    //--post GB use serial executor by default --
                    new SincronizaDatos().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                } else {
                    //--GB uses ThreadPoolExecutor by default--
                    new SincronizaDatos().execute();
                }
            }
            //noinspection SimplifiableIfStatement
            if (id == R.id.Salir) {
                finish();//return true;
            }
            if (id == R.id.CerrarSesion) {
                Intent newAct = new Intent(getApplicationContext(), Login.class);
                newAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newAct);//return true;
                finish();
            }
            return super.onOptionsItemSelected(item);
        } catch (Exception e) {
            Funciones.MensajeAviso(getApplicationContext(), e.getMessage());
        } finally {
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        android.app.FragmentTransaction tran;
        FragmentManager fragmentManager = getFragmentManager();

        switch (id) {

            case R.id.btnMaestroProductos:
                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new MaestroProductoFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;
            case R.id.btnMapa:
                fragmentManager.executePendingTransactions();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.content_frame, new MapViewFragment());
                transaction.addToBackStack(null);
                transaction.commit();

                break;
            case R.id.btnMaestroClientes:
                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ClientesFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;
            case R.id.btnNuevoCliente:
                Intent newCli = new Intent(getApplicationContext(), ClientesNew.class);
                startActivity(newCli);
                break;

            case R.id.btnActivarCliente:
                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ClientesInactivosFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnNuevoInforme:
                Intent newRecibo = new Intent(getApplicationContext(), InformesActivity.class);
                startActivity(newRecibo);
                break;

            case R.id.btnListadoPedidos:
                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ListaPedidosFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnPedidoFacturado:
                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ListaPedidovsFacturado());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnPromociones:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new PromocionesFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnOtrasPromociones:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new PromocionesSisaFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnListaInforme:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ListaInformesFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnReciboPend:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ListaRecibosPendFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnEstadoCta:
                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new FacturasMoraClienteFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnNuevoPedido:
                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new PedidosFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnReporteVentasAlDia:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ListaPedidosSupFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnReporteHistVentas:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new HistoricoventasClienteFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnReporteDevoluciones:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new ListaDevolucionesFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;

            case R.id.btnReporteComisiones:

                fragmentManager.executePendingTransactions();
                tran = getFragmentManager().beginTransaction();
                tran.add(R.id.content_frame, new reporteComisionesFragment());
                tran.addToBackStack(null);
                tran.commit();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void removeAllFragments(FragmentManager fragmentManager) {

        if (fragmentManager.getBackStackEntryCount() > 0 || getSupportFragmentManager().getBackStackEntryCount() > 0) {

            while (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStackImmediate();
            }

            while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStackImmediate();
            }

        } else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Confirmación requerida")
                    .setMessage("Está seguro que desea salir de la aplicación?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(MenuActivity.this);
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle("Configuración GPS");
        // Setting Dialog Message
        alertDialog.setMessage("GPS no está habilitado. Favor activarlo");
        // On pressing Settings button
        alertDialog.setPositiveButton("Activar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Necesita permiso de Localización")
                        .setMessage("Esta aplicación necesita permiso de Localizacion.Presione Aceptar para poder usarla.")
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MenuActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );

                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
/*        Intent intent = new Intent(this,GPSTracker.class);
        startService(intent);*/
        Intent intent = new Intent(this,GPSTracker.class);
        AutoArranque ar = new AutoArranque();
        ar.onReceive(this,intent);
    }
  }
