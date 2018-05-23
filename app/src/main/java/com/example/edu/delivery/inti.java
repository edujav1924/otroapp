package com.example.edu.delivery;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class inti extends AppCompatActivity  {
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    TextView texto_init, locationnet;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;
    private LocationCallback mLocationCallback;
    LocationManager manager;
    private boolean band = false;
    Drawable drawable;
    Boolean flag = false;
    View spin;
    Button last_ubication;
    private String base_latitud,base_longitud;
    private boolean flag2=false;
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inti);
        spin = findViewById(R.id.spin_kit);
        spin.setVisibility(View.GONE);
        texto_init = findViewById(R.id.text_init);
        locationnet = findViewById(R.id.locationnet);
        last_ubication = findViewById(R.id.lastu);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            flag=true;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            Log.e("status", "0");
            if(respuesta_term()){
                basedatos mDbHelper = new basedatos(inti.this);
                obtener_ubicaciones(mDbHelper);
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            }else {
                alert_term();
            }

        }

    }

    private boolean respuesta_term() {
        boolean bandera = false;
        basedatos mDbHelper = new basedatos(inti.this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                basedatos.info._ID,
                basedatos.info.COLUMN_CHECK,
        };
        String selection = basedatos.info._ID +"=?";
        String[] selectionArgs = { "1" };

        Cursor c = db.query(basedatos.info.TABLE_NAME,projection,selection, selectionArgs,null,null,null);
        c.moveToFirst();
        Log.e("valor de C", String.valueOf(c.getCount()));
        if(c.getCount()>0){
            Log.e("bandera",c.getString(1));
            if(c.getString(1).equals("1")){

                bandera = true;
            }
            c.close();
        }
        db.close();
        return bandera;
    }

    private void errors(String a) {
        locationnet.setText(a);
        texto_init.setText("");
        spin.setVisibility(View.GONE);

    }

    public void nubication(View view) {

       flag2 = true;
        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !flag) {
            showAlert();
        }else {
            getlocation();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (flag2){
            if(!manager.isProviderEnabled( LocationManager.GPS_PROVIDER)){
                errors("Necesita activar su Gps");

            }else {
                getlocation();
            }
        }
        flag2 = false;
    }
    public void lubication(View view) {
        spin.setVisibility(View.VISIBLE);
        basedatos mDbHelper = new basedatos(inti.this);
        leerubicacion(mDbHelper);
        getdata(base_latitud,base_longitud);
    }
    private void texto_inicio(String texto){
        texto_init.setText(texto);
    }
    private void obtener_ubicaciones(basedatos mDbHelper){
        ArrayList<String> list_ubi = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        String sql ="SELECT "+basedatos.posicion.COLUMN_LUGAR+" FROM " +basedatos.posicion.TABLE_NAME;
        cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            do {
                list_ubi.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        Spinner ubicaciones = findViewById(R.id.spinner_ubi);
        if (!list_ubi.isEmpty()){
            ubicaciones.setVisibility(View.VISIBLE);
            last_ubication.setVisibility(View.VISIBLE);
            customadapterubicacion customubi = new customadapterubicacion(this,list_ubi);

            ubicaciones.setAdapter(customubi);
            ubicaciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    id = adapterView.getSelectedItemPosition();
                    Log.e("el id es", String.valueOf(id));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
    private void leerubicacion(basedatos mDbHelper) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                basedatos.posicion._ID,
                basedatos.posicion.COLUMN_LUGAR,
                basedatos.posicion.COLUMN_lUGAR_LONGITUD,
                basedatos.posicion.COLUMN_LUGAR_LATITUD,

        };
        String selection = basedatos.posicion._ID +"=?";
        String[] selectionArgs = { String.valueOf(id+1) };

        Cursor c = db.query(basedatos.posicion.TABLE_NAME,projection,selection, selectionArgs,null,null,null);
        c.moveToFirst();
        Log.e("valor de C", String.valueOf(c.getCount()));
        if(c.getCount()>0){
            if(!c.getString(1).equals("0")){
                base_latitud = c.getString(3);
                base_longitud = c.getString(2);
                Log.e("location",c.getString(0)+" "+c.getString(1)+" "+c.getString(2)+" "+c.getString(3));
                //getdata(c.getString(2),c.getString(1));

            }
            c.close();


        }else {
            Log.e("aca","aca");
        }

        db.close();
    }


    private void getlocation() {

        locationnet.setText("");
        texto_inicio("Obteniendo localizacion");
        spin.setVisibility(View.VISIBLE);

        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && !flag) {
            showAlert();
        }else {
            Log.e("status", "1");
            mLocationCallback = new LocationCallback() {

                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {

                    }
                    for (Location location : locationResult.getLocations()) {
                        String latitud = location.convert(location.getLatitude(), 0);
                        String longitud = location.convert(location.getLongitude(), 0);
                        if (latitud.indexOf(',')>=0){
                            latitud = latitud.replace(',', '.');
                            longitud = longitud.replace(',', '.');
                        }
                        if (!manager.isProviderEnabled( LocationManager.GPS_PROVIDER )) {
                            stopLocationUpdates();
                            locationnet.setText("active su gps kp");
                        }
                        locationnet.setText("aproximando a: " + String.valueOf(location.getAccuracy()) + " metros");
                        if (location.getAccuracy() <= 25.0) {
                            stopLocationUpdates();
                            getdata(latitud, longitud);

                        }
                    }
                }


            };
            createLocationRequest();
            startLocationUpdates();
        }

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        Log.e("pausa","pausa");


    }
    @Override
    public void onRestart() {
        super.onRestart();  // Always call the superclass method first
        Log.e("restart","restart");

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Log.e("onback","presed");
        if (!respuesta_term()){
            this.finish();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(band)
            stopLocationUpdates();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(inti.this,
                                100);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        band = false;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        band = true;
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    alert_term();
                    /*obtener_ubicaciones(mDbHelper);
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    flag=false;*/
                }
                    else {
                    finish();

                }
            }
        }
    }
    public void Disable_Certificate_Validation_Java_SSL_Connections() {

// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    private void getdata(final String latitudcel, final String longitudcel) {
        texto_inicio("obteniendo datos");
        Disable_Certificate_Validation_Java_SSL_Connections();
        RequestQueue queue2 = Volley.newRequestQueue(this);
        String url ="https://delivery.simplelectronica.com/empresa.json";
        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.DONUT)
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray jsono = new JSONArray(response);
                            List<String> latitud=new ArrayList<>();
                            List<String> longitud=new ArrayList<>();
                            for (int i=0;i<jsono.length();i++){
                                latitud.add(jsono.getJSONObject(i).getString("latitud"));
                                longitud.add(jsono.getJSONObject(i).getString("longitud"));
                            }
                            getdirections(jsono,latitud,longitud,latitudcel,longitudcel);
                            /*for (int i=0;i<jsono.length();i++){
                                Log.e("value", String.valueOf(jsono.getJSONObject(i).getJSONArray("productos")));
                                for (int j=0;j<jsono.length();j++){
                                    Log.e("value", String.valueOf(jsono.getJSONObject(i).getJSONArray("productos").getJSONObject(j).getString("producto")));
                                }
                            }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                           errors("Hubo un error al recibir datos del servidor");
                            Log.e("Error1","Error1");
                        }
                    }
                }, new Response.ErrorListener() {
            @RequiresApi(api = Build.VERSION_CODES.DONUT)
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                   errors("Error al conectar con el servidor");
                } else if (error instanceof AuthFailureError) {
                    errors("Error de autenticacion");

                } else if (error instanceof ServerError) {

                    errors("Error interna del servidor");

                } else if (error instanceof NetworkError) {

                    errors("Error de conexion");
                } else if (error instanceof ParseError) {

                    errors("Error de formato de datos");
                }
            }
        });
        queue2.add(stringRequest2);
    }

    private void getdirections(final JSONArray jsono, List<String> latitud, List<String> longitud, final String latitudcel, final String longitudcel) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://maps.googleapis.com/maps/api/distancematrix/json?units=meter&origins="+latitudcel+","+longitudcel+"&destinations=";
        for (int j=0;j<latitud.size();j++){
            url = url + longitud.get(j) +"%2C"+latitud.get(j)+"%7C";
        }
        Log.e("url",url);
        //String url ="http://maps.googleapis.com/maps/api/distancematrix/json?units=meter&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.DONUT)
                    @Override
                    public void onResponse(String response) {
                        List<String> direcciones=new ArrayList<>();
                        List<String> distancias=new ArrayList<>();
                        List<String> tiempo=new ArrayList<>();
                        try {
                            JSONObject other = new JSONObject(response);
                            JSONArray arrayLocales = other.getJSONArray("destination_addresses");
                            for (int i=0;i<arrayLocales.length();i++){
                                direcciones.add(arrayLocales.getString(i));
                            }
                            JSONArray milocation = other.getJSONArray("origin_addresses");
                            JSONArray elements = other.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
                            for (int i=0;i<elements.length();i++){
                                JSONObject aux3 =elements.getJSONObject(i).getJSONObject("distance");
                                distancias.add(aux3.getString("text"));
                                aux3 = elements.getJSONObject(i).getJSONObject("duration");
                                tiempo.add(aux3.getString("text"));
                                //Log.e("get", aux3.getString("text"));
                            }
                            desarrollo(direcciones,distancias,tiempo,jsono,longitudcel,latitudcel);
                        } catch (JSONException e) {
                            Log.e("error", String.valueOf(e));
                            e.printStackTrace();
                            errors("Hubo un error al recibir datos de localizacion");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                errors("no fue posible obtener datos de localizacion");
            }
        });
        queue.add(stringRequest);
    }

    private void desarrollo(List<String> direcciones, List<String> distancias, List<String> tiempo, JSONArray jsono, String longitudcel, String latitudcel) {
        Intent intent = new Intent(inti.this, BusquedaActivity.class);
        intent.putExtra("direcciones", (Serializable) direcciones);
        intent.putExtra("distancias", (Serializable) distancias);
        intent.putExtra("tiempo", (Serializable) tiempo);
        intent.putExtra("json", String.valueOf(jsono));
        intent.putExtra("latitud", latitudcel);
        intent.putExtra("longitud",longitudcel);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        ActivityCompat.finishAffinity(this);
        startActivity(intent);


    }
    private void showAlert() {
        new FancyAlertDialog.Builder(this)
                .setTitle("Localizacion")
                .setBackgroundColor(Color.parseColor("#009933"))  //Don't pass R.color.colorvalue
                .setMessage("Para poder brindarle la mejor atencion necesitamos que active su GPS")
                .setNegativeBtnText("Salir")
                .setPositiveBtnBackground(Color.parseColor("#009933"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("GPS")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.SLIDE)
                .isCancellable(true)
                .setIcon(R.drawable.ic_gps, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        finish();
                    }
                })
                .build();
    }
    private void alert_term(){
        boolean wrapInScrollView = true;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        final LayoutInflater inflater = this.getLayoutInflater();
        final View dialog_view= inflater.inflate(R.layout.forma_de_uso, null);
        builder.setView(dialog_view);
        builder.setPositiveButton("Acepto", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                basedatos bd = new basedatos(inti.this);
                SQLiteDatabase db = bd.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(basedatos.info.COLUMN_CHECK, String.valueOf(1));
                long newRowId = db.insert(basedatos.info.TABLE_NAME, null, values);
                db.close();
                basedatos mDbHelper = new basedatos(inti.this);
                obtener_ubicaciones(mDbHelper);
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(inti.this);
                flag=false;
                Log.e("terminos","acepto");

            }
        }).setNegativeButton("Rechazo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e("terminos","rechazo");
                finish();
            }
        }).setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialogInterface.cancel();
                    inti.this.finish();

                }
                return true;
            }
        }).create().show();

    }


}

