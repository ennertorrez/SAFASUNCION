package com.safi_d.sistemas.safiapp.Informes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.core.app.ActivityCompat;

import com.safi_d.sistemas.safiapp.AccesoDatos.ClientesHelper;
import com.safi_d.sistemas.safiapp.AccesoDatos.DataBaseOpenHelper;
import com.safi_d.sistemas.safiapp.AccesoDatos.FacturasPendientesHelper;
import com.safi_d.sistemas.safiapp.AccesoDatos.InformesDetalleHelper;
import com.safi_d.sistemas.safiapp.AccesoDatos.InformesHelper;
import com.safi_d.sistemas.safiapp.AccesoDatos.RutasHelper;
import com.safi_d.sistemas.safiapp.AccesoDatos.VendedoresHelper;
import com.safi_d.sistemas.safiapp.Auxiliar.Funciones;
import com.safi_d.sistemas.safiapp.Auxiliar.SincronizarDatos;
import com.safi_d.sistemas.safiapp.Auxiliar.variables_publicas;
import com.safi_d.sistemas.safiapp.Entidades.Informe;
import com.safi_d.sistemas.safiapp.Entidades.Vendedor;
import com.safi_d.sistemas.safiapp.HttpHandler;
import com.safi_d.sistemas.safiapp.R;
import android.os.Bundle;
import com.google.gson.Gson;
import android.widget.Toast;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Pattern;


/**
 * Created by Sistemas on 16/3/2018.
 */

public class InformesActivity extends Activity implements ActivityCompat.OnRequestPermissionsResultCallback {
    private TextView txtCodigoInforme;
    private TextView lblTc;
    private Spinner cboVendedor;
    private Button btnAgregarRecibo;
    private Button btnGuardar;
    private Button btnCancelar;
    private ListView lv;
    private TextView lblTotalDol;
    private TextView lblTotalCor;
    private TextView lblFooter;
    private double tasaCambio = 0;
    private String TAG = InformesActivity.class.getSimpleName();
    private VendedoresHelper VendedoresH;
    private DataBaseOpenHelper DbOpenHelper;
    private RutasHelper RutasH;
    private SincronizarDatos sd;
    private DecimalFormat df;
    private String vIdVendedor;
    private String vnombreVendedor;
    private Vendedor vendedor;
    private boolean finalizar = false;
    public static ArrayList<HashMap<String, String>> listaInformes;
    private SimpleAdapter adapter;
    private Informe informe;
    String IMEI = "";
    private InformesHelper InformesH;
    private InformesDetalleHelper InformesDetalleH;
    private FacturasPendientesHelper FacturasPendientesH;
    private ClientesHelper ClientesH;
    private boolean isOnline = false;
    public static ArrayList<HashMap<String, String>> lista;
    private double total;
    private String vIdSerie;
    private String vUltNumero;
    static final String VALINFORME = "CodInforme";
    public static boolean editar;
    public static boolean veditar;
    private final int PETICION_ACTIVITY_SEGUNDA = 1;
    private boolean guardadoOK;
    private String jsonInforme = "";
    private ProgressDialog pDialog;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informepago);

        informe = new Informe();

        DbOpenHelper = new DataBaseOpenHelper(InformesActivity.this);
        VendedoresH = new VendedoresHelper(DbOpenHelper.database);
        InformesH = new InformesHelper(DbOpenHelper.database);
        InformesDetalleH = new InformesDetalleHelper(DbOpenHelper.database);
        FacturasPendientesH =  new FacturasPendientesHelper(DbOpenHelper.database);
        RutasH =  new RutasHelper(DbOpenHelper.database);

        df = new DecimalFormat("#0.00");
        DecimalFormatSymbols fmts = new DecimalFormatSymbols();
        fmts.setGroupingSeparator(',');
        df.setGroupingSize(3);
        df.setGroupingUsed(true);
        df.setDecimalFormatSymbols(fmts);

        ValidarUltimaVersion();

        cboVendedor = (Spinner) findViewById(R.id.cboVendedor);
        lblFooter = (TextView) findViewById(R.id.lblFooter);
        lblTc = (TextView) findViewById(R.id.lblTC);
        tasaCambio = Double.parseDouble(variables_publicas.usuario.getTasaCambio());
        txtCodigoInforme = (TextView) findViewById(R.id.lblNoInforme);
        lblTotalCor = (TextView) findViewById(R.id.lblTotalCor);
        lblTotalDol = (TextView) findViewById(R.id.lblTotalDol);
        btnAgregarRecibo = (Button) findViewById(R.id.btnAgregarRecibo);
        btnGuardar = (Button) findViewById(R.id.btnGuardar);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        lv = (ListView) findViewById(R.id.listrecibos);

        //Obteniendo el valkre de la Tasa de cambio.
        lblTc.setText(df.format(Double.parseDouble(variables_publicas.usuario.getTasaCambio())));

        sd = new SincronizarDatos(DbOpenHelper,InformesH,InformesDetalleH,ClientesH,FacturasPendientesH,RutasH);

        listaInformes = new ArrayList<HashMap<String, String>>();
        listaInformes.clear();

        cargarCboVendedor();
        if (editar==false){
            GenerarCodigoInforme();
        }

        listaInformes=InformesH.ObtenerInformeDet(txtCodigoInforme.getText().toString().replace("No. Informe: ",""));
        //Definición de la Lista de Recibos
        registerForContextMenu(lv);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView1);
        //Eventos para a lista de Recibos
        lv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

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

        RefrescarGrid();
        CalcularTotales();

        if (variables_publicas.usuario.getTipo().equalsIgnoreCase("Vendedor")) {
            cboVendedor.setEnabled(false);
        } else {
            cboVendedor.setEnabled(true);

        }

        cboVendedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long id) {
                vendedor = (Vendedor) adapter.getItemAtPosition(position);
                vIdVendedor = vendedor.getCODIGO().toString();
                vnombreVendedor = vendedor.getNOMBRE().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        if (variables_publicas.usuario.getTipo().equals("User") || variables_publicas.usuario.getTipo().equals("Supervisor")) {
            vIdVendedor = vendedor.getCODIGO().toString();
            vnombreVendedor = vendedor.getNOMBRE().toString();
        }else {
            vIdVendedor= variables_publicas.usuario.getCodigo();
            vnombreVendedor = variables_publicas.usuario.getNombre();
        }

        btnAgregarRecibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vIdVendedor.equals("0") || vIdVendedor==null){
                    MensajeAviso("Debe Seleccionar un vendedor.");
                }else {
                    lista = new ArrayList<HashMap<String, String>>();
                    lista = InformesDetalleH.ObtenerUltimoCodigoRecibo(vIdVendedor);

                    for (int i = 0; i < lista.size(); i++) {
                        vIdSerie = lista.get(i).get("IdSerie");
                        vUltNumero = lista.get(i).get("Numero");
                    }
                    if (lista.size() > 0) {
                        DbOpenHelper.database.beginTransaction();
                        if (GuardarInforme()) {
                            DbOpenHelper.database.setTransactionSuccessful();
                            DbOpenHelper.database.endTransaction();
                            Intent agrRecibo = new Intent(v.getContext(), AgregarRecibo.class);
                            agrRecibo.putExtra(variables_publicas.INFORMES_COLUMN_CodInforme, txtCodigoInforme.getText().toString().replace("No. Informe: ", ""));
                            agrRecibo.putExtra(variables_publicas.KEY_IdVendedor, vIdVendedor);
                            agrRecibo.putExtra(variables_publicas.KEY_NombreVendedor, vnombreVendedor);
                            agrRecibo.putExtra(variables_publicas.KEY_idSerie, vIdSerie);
                            agrRecibo.putExtra(variables_publicas.KEY_ultRecibo, vUltNumero);
                            startActivityForResult(agrRecibo, PETICION_ACTIVITY_SEGUNDA);
                            editar = true;
                        } else {
                            DbOpenHelper.database.endTransaction();
                            editar = false;
                        }
                    } else {
                        MensajeAviso("El Vendedor Seleccionado no tiene talonario de Recibos asignado.");
                    }
                }
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InformesActivity.this.onBackPressed();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Guardar();
                } catch (Exception e) {
                    DbOpenHelper.database.endTransaction();
                    MensajeAviso(e.getMessage());
                }
            }
        });
    }

    private boolean Guardar() {
        if (lv.getCount() <= 0) {
            MensajeAviso("No se puede guardar el Informe, Debe ingresar al menos 1 recibo");
            return false;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmación Requerida")
                .setMessage("Esta seguro que desea guardar el informe?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SincronizarInforme(InformesH.ObtenerInforme(informe.getCodigoInforme()));
                        editar=false;
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
    private boolean SincronizarInforme(HashMap<String, String> informe) {
        Gson gson = new Gson();

        jsonInforme = gson.toJson(informe);

        try {
            if (Build.VERSION.SDK_INT >= 11) {
                //--post GB use serial executor by default --
                new SincronizardorInformes().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            } else {
                //--GB uses ThreadPoolExecutor by default--
                new SincronizardorInformes().execute();
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
        }

        return false;
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
    private class SincronizardorInformes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(InformesActivity.this);
            pDialog.setMessage("Guardando datos, por favor espere...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            CheckConnectivity();
            if (isOnline) {
                if (Boolean.parseBoolean(SincronizarDatos.SincronizarInforme(InformesH, InformesDetalleH, vIdVendedor,  informe.getCodigoInforme(), jsonInforme, false).split(",")[0])) {
                    FacturasPendientesH.SincronizarFacturasSaldos(vIdVendedor,"0");
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

            if (InformesActivity.this.isFinishing() == false) {
                MostrarMensajeGuardar();
            }

        }
    }

    public void MostrarMensajeGuardar() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = null;
        dialogBuilder.setCancelable(false);
        if (guardadoOK) {
            dialogView = inflater.inflate(R.layout.dialog_ok_inf_layout, null);

            String auxValorNuevo=variables_publicas.noInforme;

            Button btnOK = (Button) dialogView.findViewById(R.id.btnOkDialogo);
            TextView nuevoValor = (TextView) dialogView.findViewById(R.id.nuevoIdInforme);
            nuevoValor.setText(auxValorNuevo);
            nuevoValor.setTextColor(Color.parseColor("#FFBF5300"));

            btnOK.setOnClickListener(new View.OnClickListener() {
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==PETICION_ACTIVITY_SEGUNDA) {
            if(resultCode==Activity.RESULT_OK) {
                String textoCapturado = data.getStringExtra(AgregarRecibo.TEXTO_CAPTURADO);
                listaInformes=InformesH.ObtenerInformeDet(textoCapturado);
                RefrescarGrid();
                CalcularTotales();
            }
        }
    }
    private void RefrescarGrid() {
        adapter = new SimpleAdapter(
                getApplicationContext(), listaInformes,
                R.layout.informes_list_item, new
                String[]{"Recibo", "Id", "Cliente", "Monto", "Facturas"}, new
                int[]{R.id.lblReciboNo, R.id.lblClienteId, R.id.lblNombreClienteList, R.id.lblMonto, R.id.lblFacturasList}) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View currView = super.getView(position, convertView, parent);
                HashMap<String, String> currItem = (HashMap<String, String>) getItem(position);
                return currView;
            }
        };

        lv.setAdapter(adapter);
    }

    private boolean GuardarInforme() {

        IMEI = variables_publicas.IMEI;
        informe.setCodigoInforme(txtCodigoInforme.getText().toString().replace("No. Informe: ",""));
        informe.setImei(IMEI);
        informe.setIdVendedor(vIdVendedor);
        informe.setAprobada("false");
        informe.setAnulada("false");
        informe.setFecha(getDatePhone());
        informe.setUsuario("SAFIAPP");

        //Esto lo ponemos para cuando es editar
        InformesH.EliminaInforme(informe.getCodigoInforme());
        Funciones.GetLocalDateTime();

        boolean saved = InformesH.GuardarInforme(informe.getCodigoInforme(),informe.getFecha(),informe.getIdVendedor(),informe.getAprobada(),informe.getAnulada(),informe.getImei(),informe.getUsuario());

        if (!saved) {
            MensajeAviso("Ha Ocurrido un error al guardar los datos");
            return false;
        }
        return true;
    }

    private void obtenerIdInforme() {

        String encodeUrl = "";
        HttpHandler sh = new HttpHandler();

        String urlString = variables_publicas.direccionIp + "/ServicioRecibos.svc/ObtenerConsecutivoInforme";;
        try {
            URL Url = new URL(urlString);
            URI uri = new URI(Url.getProtocol(), Url.getUserInfo(), Url.getHost(), Url.getPort(), Url.getPath(), Url.getQuery(), Url.getRef());
            encodeUrl = uri.toURL().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            String jsonStr = sh.makeServiceCall(encodeUrl);
            if (jsonStr == null) {
                new Funciones().SendMail("Ha ocurrido un error al obtener el Nuevo Id del Informe, Respuesta nula GET", variables_publicas.info + urlString, "dlunasistemas@gmail.com", variables_publicas.correosErrores);
            } else {
                Log.e(TAG, "Response from url: " + jsonStr);

                JSONObject jsonObj = new JSONObject(jsonStr);
                String resultState = (String) ((String) jsonObj.get("ObtenerConsecutivoInformeResult")).split(",")[0];
                String NoInforme = (String) ((String) jsonObj.get("ObtenerConsecutivoInformeResult")).split(",")[1];
                if (resultState.equals("true")) {
                    txtCodigoInforme.setText("No. Informe: " + NoInforme);
                }
            }
        } catch (Exception ex) {
            new Funciones().SendMail("Ha ocurrido un error al obtener el Nuevo Id del Informe, Excepcion controlada", variables_publicas.info + ex.getMessage(), "dlunasistemas@gmail.com", variables_publicas.correosErrores);

        }
    }

    private void cargarCboVendedor(){
        //Combo Vendedores
        List<Vendedor> vendedores = VendedoresH.ObtenerListaVendedores();
        ArrayAdapter<Vendedor> adapterVendedor = new ArrayAdapter<Vendedor>(this, android.R.layout.simple_spinner_item, vendedores);
        adapterVendedor.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        cboVendedor.setAdapter(adapterVendedor);

        if (variables_publicas.usuario.getTipo().equals("Vendedor")){
            vIdVendedor = variables_publicas.usuario.getCodigo();
            vnombreVendedor = variables_publicas.usuario.getNombre();
            cboVendedor.setSelection(getIndex(cboVendedor,variables_publicas.usuario.getNombre()));
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
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmación Requerida")
                .setMessage("Esta seguro que desea cancelar el Informe actual?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int valrecibo = InformesH.BuscarMinimoRecibo(txtCodigoInforme.getText().toString().replace("No. Informe: ", ""));
                        if (valrecibo>0){
                            vIdSerie=InformesH.BuscarSerieMinimoRecibo(String.valueOf(valrecibo),vIdVendedor);
                            InformesDetalleH.ActualizarCodigoRecibo(vIdSerie,String.valueOf(valrecibo-1),vIdVendedor);
                        }
                        InformesH.EliminaInforme(txtCodigoInforme.getText().toString().replace("No. Informe: ", ""));
                        FacturasPendientesH.ActualizarTodasFacturasPendientes(txtCodigoInforme.getText().toString().replace("No. Informe: ", ""));
                        InformesDetalleH.EliminarDetalleInforme(txtCodigoInforme.getText().toString().replace("No. Informe: ", ""));
                        editar=false;

                        InformesActivity.this.finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
    private String getDatePhone() {
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formatteDate = df.format(date);
        return formatteDate;
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
                    //It retrieves the latest version by scraping the content of current version from play store at runtime
                    Document doc2 = Jsoup
                            .connect(
                                    "https://play.google.com/store/apps/details?id=com.safi_d.sistemas.safiapp")
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
                final AlertDialog.Builder builder = new AlertDialog.Builder(InformesActivity.this);
                builder.setTitle("Nueva version disponible");
                builder.setMessage("Es necesario actualizar la aplicacion para poder continuar.");
                builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Click button action
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.safi_D.sistemas.safiapp&hl=es")));
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
    private void CheckConnectivity() {
        isOnline = Funciones.TestServerConectivity();
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        try {
            super.onCreateContextMenu(menu, v, menuInfo);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            HashMap<String, String> obj = (HashMap<String, String>) lv.getItemAtPosition(info.position);

            String HeaderMenu = obj.get("Recibo");
            //String HeaderMenu = obj.get("Factura") + "\n" + obj.get("Descripcion");
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
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            switch (item.getItemId()) {
                case R.id.Elimina_Item:
                    int valorUltLista= listaInformes.size()==0 ? 0 :listaInformes.size()-1;
                    if (valorUltLista==info.position){
                        HashMap<String, String> itemRecibo = listaInformes.get(info.position);
                        listaInformes.remove(itemRecibo);
                        FacturasPendientesH.ActualizarTodasFacturasPendientesRecibo(itemRecibo.get(variables_publicas.DETALLEINFORMES_COLUMN_Recibo));
                        InformesDetalleH.ActualizarCodigoRecibo(vIdSerie,String.valueOf(Integer.parseInt(itemRecibo.get(variables_publicas.DETALLEINFORMES_COLUMN_Recibo))-1),vIdVendedor);
                        InformesDetalleH.EliminarDetalleInforme2(txtCodigoInforme.getText().toString().replace("No. Informe: ", ""),itemRecibo.get(variables_publicas.DETALLEINFORMES_COLUMN_Recibo));
                        for (int i = 0; i < listaInformes.size() - 1; i++) {
                            HashMap<String, String> a = listaInformes.get(i);
                            if (a.get(variables_publicas.DETALLEINFORMES_COLUMN_Recibo).equals(itemRecibo.get(variables_publicas.DETALLEINFORMES_COLUMN_Recibo))) {
                                listaInformes.remove(a);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        lv.setAdapter(adapter);

                    }else{
                        MensajeAviso("Recibo no se puede Eliminar. Debe Eliminar el último.");
                    }

                    RefrescarGrid();
                    CalcularTotales();

                    return true;

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
    private void CalcularTotales() {

        total = 0;
        for (int i = 0; i < listaInformes.size(); i++) {
            HashMap<String, String> item = listaInformes.get(i);

            try {
                total += (df.parse(item.get("Monto"))).doubleValue();
            } catch (ParseException e) {
                MensajeAviso(e.getMessage());
            }
        }
        lblTotalCor.setText(df.format(total));

        if (tasaCambio > 0) {
            lblTotalDol.setText(String.valueOf(df.format(total / tasaCambio)));
        }
        lblFooter.setText("Total items:" + String.valueOf(listaInformes.size()));

    }
    private void GenerarCodigoInforme() {
        if (vIdVendedor==null){
            vIdVendedor="0";
        }
        informe.setCodigoInforme(GetFechaISO() + vIdVendedor);
        txtCodigoInforme.setText("No. Informe: " + informe.getCodigoInforme());
    }

    private String GetFechaISO() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyMMddHHmms");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
        return nowAsISO;
    }
}
