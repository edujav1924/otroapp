package com.example.edu.delivery;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.circulardialog.CDialog;
import com.example.circulardialog.extras.CDConstants;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import greco.lorenzo.com.lgsnackbar.core.LGSnackbar;
import studio.carbonylgroup.textfieldboxes.SimpleTextChangedWatcher;
import studio.carbonylgroup.textfieldboxes.TextFieldBoxes;

public class send extends AppCompatActivity {
    private boolean aux_nombre=true;
    private boolean aux_apellido=true;
    private boolean aux_telefono=true;
    private JSONObject datos;
    basedatos mDbHelper;
    CheckBox remember,recubi;
    int flag=-1;
    String nombre_text,apellido_text,telefono_text;
    private  TextFieldBoxes nombre,apellido,telefono;
    EditText nombre_box,apellido_box,celular_box;
    private String ubicacion_text;
    private boolean aux_ubicacion,resp_ubi;
    private boolean aux_fecha=false,aux_hora=false;
    private String lugar,id,fecha_sel="",hora_sel="";
    int myear,mmonth,mday,day_select,month_select,year_select,hour_select,minute_select;
    int mhour,mminute;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        remember = findViewById(R.id.recordar);
        nombre_box= findViewById(R.id.box_edit_nombre);
        apellido_box = findViewById(R.id.box_edit_apellido);
        celular_box = findViewById(R.id.box_edit_telefono);
        recubi = findViewById(R.id.recubi);
        mDbHelper= new basedatos(send.this);

        try {
            flag = leerinfo();
            verify();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),"erroralleerinfo",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        try {
            datos = new JSONObject(getIntent().getExtras().getString("datos"));
            resp_ubi = leerubi();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        nombre = findViewById(R.id.text_field_nombre);
        nombre.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                nombre_text = theNewText;
              if (isError){
                  nombre.setError("3 caractéres mínimo",false);
                  aux_nombre = true;
              }
              else{
                  aux_nombre = false;
              }
              verify();
            }
        });
        apellido = findViewById(R.id.text_field_apellido);
        apellido.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                apellido_text = theNewText;
                if (isError){
                    apellido.setError("3 caractéres mínimo",false);
                    aux_apellido = true;
                }
                else{
                    aux_apellido = false;
                }
                verify();
            }
        });
        telefono = findViewById(R.id.text_telefono);
        telefono.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                telefono_text = theNewText;
                if (isError){
                    telefono.setError("3 caractéres mínimo",false);
                    aux_telefono = true;
                }
                else {
                    aux_telefono = false;
                }
                verify();
            }
        });
        final TextFieldBoxes ubicacion = findViewById(R.id.text_ubicacion);
        ubicacion.setSimpleTextChangeWatcher(new SimpleTextChangedWatcher() {
            @Override
            public void onTextChanged(String theNewText, boolean isError) {
                ubicacion_text = theNewText;
                if (isError){
                    ubicacion.setError("4 caracteres minimo",false);
                    aux_ubicacion = true;
                }
                else {
                    aux_ubicacion = false;
                }
                verify();
            }
        });
    }


    public void verify(){
        if(!aux_apellido && !aux_nombre && !aux_telefono && !aux_ubicacion){
            findViewById(R.id.button_enviar).setEnabled(true);
        }
        else {
            findViewById(R.id.button_enviar).setEnabled(false);

        }
    }
    public void enviar(View v){
        try {
            String token = FirebaseInstanceId.getInstance().getToken();
            datos.put("token",token);
            datos.put("nombre",nombre_box.getText().toString());
            datos.put("apellido",apellido_box.getText().toString());
            datos.put("celular",celular_box.getText().toString());
            RadioButton a = findViewById(R.id.delivery);
            if (a.isChecked()){
                datos.put("tipo","delivery");
                datos.put("hora_sel","");
                datos.put("fecha_sel","");
                enviar();
            }
            else {
                if (!hora_sel.equals("") && !fecha_sel.equals("")){
                    datos.put("tipo","pasa_a_buscar");

                    task tarea = new task();
                    tarea.execute(mday,mmonth,myear,day_select,month_select,year_select,hour_select,minute_select,mhour,mminute);
                }
                else {
                    alert("falta hora y/o fecha en 'pasar a buscar' ",0);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
            alert("Error de envio",0);
        }

    }
    private void enviar(){
        Log.e("dato", String.valueOf(datos));
        RequestQueue request = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://delivery.simplelectronica.com/cliente/",datos, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String valor = jsonObject.getString("status");
                    Log.e("respnse",valor);
                    if (valor.equals("exitoso")){
                        new CDialog(send.this).createAlert(valor,
                                CDConstants.SUCCESS,   // Type of dialog
                                CDConstants.LARGE)    //  size of dialog
                                .setAnimation(CDConstants.SCALE_FROM_RIGHT_TO_LEFT)     //  Animation for enter/exit
                                .setDuration(3000)   // in milliseconds
                                .setTextSize(CDConstants.NORMAL_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                                .show();
                        Thread thread = new Thread(){
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                                    send.this.finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        if(remember.isChecked() || recubi.isChecked()){
                            Thread hilo = new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        if (remember.isChecked() && flag==0){
                                            guardarinfo(nombre_text,apellido_text,telefono_text);
                                        }else if(remember.isChecked() && flag>0){
                                            actualizar();
                                        }
                                        if(recubi.isChecked()){
                                            addubi();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                            };
                            hilo.start();

                        }
                    }else {
                        new CDialog(send.this).createAlert(valor,
                                CDConstants.WARNING,   // Type of dialog
                                CDConstants.LARGE)    //  size of dialog
                                .setAnimation(CDConstants.SCALE_FROM_RIGHT_TO_LEFT)     //  Animation for enter/exit
                                .setDuration(3000)   // in milliseconds
                                .setTextSize(CDConstants.NORMAL_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    alert("error al enviar pedido",0);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    alert("Error al conectar con el servidor",0);
                } else if (error instanceof AuthFailureError) {
                    alert("Error de autenticacion",0);

                } else if (error instanceof ServerError) {

                    alert("Error interna del servidor",0);

                } else if (error instanceof NetworkError) {

                    alert("Error de conexion",0);
                } else if (error instanceof ParseError) {

                    alert("Error de formato de datos",0);
                }
            }
        });

        request.add(jsonObjectRequest);

    }
    private void addubi() {
        Log.e("addubi","entre");
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        try {

            EditText a = findViewById(R.id.box_edit_ubicacion);
            values.put(basedatos.posicion.COLUMN_LUGAR, a.getText().toString());
            values.put(basedatos.posicion.COLUMN_lUGAR_LONGITUD, datos.getString("longitud"));
            values.put(basedatos.posicion.COLUMN_LUGAR_LATITUD, datos.getString("latitud"));
            Log.i("resubi", String.valueOf(resp_ubi));
            if (resp_ubi){
                Log.e("lugar",lugar);
                if (!lugar.equals(a.getText().toString())){
                    String selection = basedatos.FeedEntry._ID +"=?";
                    String[] selectionArgs = { id };
                    int count = db.update(
                            basedatos.posicion.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs);
                    db.close();
                }


            }else {
                Log.e("nuevo nombre",a.getText().toString());
                long newRowId = db.insert(basedatos.posicion.TABLE_NAME, null, values);
                db.close();
            }

        }
         catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean leerubi() throws JSONException {
        boolean flag = false;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor,cursor_2 = null;
        String sql ="SELECT "+basedatos.posicion._ID+","+basedatos.posicion.COLUMN_LUGAR_LATITUD+" FROM "
                +basedatos.posicion.TABLE_NAME+" WHERE "+basedatos.posicion.COLUMN_LUGAR_LATITUD+
                "="+datos.getString("latitud");
        cursor= db.rawQuery(sql,null);
        Log.e("c","Cursor Count : " + cursor.getCount());
        sql ="SELECT "+basedatos.posicion._ID+","+basedatos.posicion.COLUMN_lUGAR_LONGITUD+","
                +basedatos.posicion.COLUMN_LUGAR+" FROM "
                +basedatos.posicion.TABLE_NAME+" WHERE "+basedatos.posicion.COLUMN_lUGAR_LONGITUD+
                "="+datos.getString("longitud");
        cursor_2= db.rawQuery(sql,null);
        Log.e("c","Cursor_2 Count : " + cursor_2.getCount());
        if(cursor.getCount()>0 && cursor_2.getCount()>0){
            cursor.moveToFirst();
            cursor_2.moveToFirst();
            Log.e("ides",cursor.getString(0)+" "+cursor_2.getString(0));
            if (cursor.getString(0).equals(cursor_2.getString(0)) && !cursor.getString(0).equals("0")){
                EditText a = findViewById(R.id.box_edit_ubicacion);
                a.setText(cursor_2.getString(2));
                lugar = cursor_2.getString(2);
                id = cursor_2.getString(0);
                cursor.close();
                flag  = true;
            }
        }else{
            Log.e("no encontrado"," no encontrado");
            cursor.close();
        }
        Log.e("flag es", String.valueOf(flag));
        return flag;
    }

    private void actualizar(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(basedatos.FeedEntry.COLUMN_NOMBRE, nombre_box.getText().toString());
        values.put(basedatos.FeedEntry.COLUMN_APELLIDO, apellido_box.getText().toString());
        values.put(basedatos.FeedEntry.COLUMN_CELULAR ,celular_box.getText().toString());
        String selection = basedatos.FeedEntry._ID +"=?";
        String[] selectionArgs = { "1" };
        int count = db.update(
                basedatos.FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
        db.close();
    }

    private int leerinfo() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                basedatos.FeedEntry._ID,
                basedatos.FeedEntry.COLUMN_NOMBRE,
                basedatos.FeedEntry.COLUMN_APELLIDO,
                basedatos.FeedEntry.COLUMN_CELULAR,
        };
        String selection = basedatos.FeedEntry._ID +"=?";
        String[] selectionArgs = { "1" };
        //Cursor c = db.rawQuery(" SELECT * FROM entry Where 'basedatos.FeedEntry._ID=0' ", null);
        Cursor c = db.query(basedatos.FeedEntry.TABLE_NAME,projection,selection, selectionArgs,null,null,null);
        c.moveToFirst();
        if(c.getCount()>0){
            Log.e("id", String.valueOf(c.getCount()));
            Log.e("id",c.getString(1));
            Log.e("id1",c.getString(2));
            Log.e("id2",c.getString(3));
            nombre_box.setText(c.getString(1));
            apellido_box.setText(c.getString(2));
            celular_box.setText(c.getString(3));
            aux_telefono = false;
            aux_nombre = false;
            aux_apellido =false;
        }
        else{
            Log.e("status", "vacio");
        }
        db.close();
        return c.getCount();
    }
    private void guardarinfo(String nombre, String apellido, String celular) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(basedatos.FeedEntry.COLUMN_NOMBRE, nombre);
        values.put(basedatos.FeedEntry.COLUMN_APELLIDO, apellido);
        values.put(basedatos.FeedEntry.COLUMN_CELULAR, celular);


// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(basedatos.FeedEntry.TABLE_NAME, null, values);
        db.close();

    }
    public void alert(String text, int type){
        switch (type){
            case 0:
                new LGSnackbar.LGSnackbarBuilder(getApplicationContext(), text)
                        .duration(3000)
                        .actionTextColor(Color.RED)
                        .backgroundColor(Color.RED)
                        .minHeightDp(50)
                        .textColor(Color.WHITE)
                        .iconID(R.drawable.ic_custom_error)
                        .callback(null)
                        .action(null)
                        .show();
                break;
            case 1:
                new LGSnackbar.LGSnackbarBuilder(getApplicationContext(), text)
                        .duration(3000)
                        .actionTextColor(Color.GREEN)
                        .backgroundColor(Color.GREEN)
                        .minHeightDp(50)
                        .textColor(Color.WHITE)
                        .iconID(R.drawable.ic_check_black_24dp)
                        .callback(null)
                        .action(null)
                        .show();
                break;
        }

    }
    public void onRadioButtonClicked(View v){
        boolean checked = ((RadioButton) v).isChecked();

        // Check which radio button was clicked
        switch(v.getId()) {
            case R.id.delivery:
                if (checked) {
                    findViewById(R.id.hora_layout).setVisibility(View.GONE);
                    findViewById(R.id.fecha_layout).setVisibility(View.GONE);
                    break;
                }
            case R.id.pasar_a_buscar:
                if (checked) {

                    findViewById(R.id.hora_layout).setVisibility(View.VISIBLE);
                    findViewById(R.id.fecha_layout).setVisibility(View.VISIBLE);
                    break;
                }
        }
    }
    @SuppressLint("ValidFragment")
    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            mhour = c.get(Calendar.HOUR_OF_DAY);
            mminute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, mhour, mminute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        @SuppressLint("StaticFieldLeak")
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hora_prog  = String.valueOf(hourOfDay);
            String min_prog  = String.valueOf(minute);
            hour_select = hourOfDay;
            minute_select = minute;
            if(hourOfDay<10){
                hora_prog = "0"+hora_prog;
            }
            if(minute<10){
                min_prog = "0"+min_prog;
            }
            hora_sel = hora_prog+":"+min_prog;
            TextView h = findViewById(R.id.hora);
            h.setText(hora_prog+":"+min_prog+" hs.");



        }

    }

    private class task extends AsyncTask<Integer, String, String> {
        ProgressDialog progress = new ProgressDialog(send.this);
        protected String doInBackground(Integer... integers) {

            int myear = integers[2];
            int mmonth = integers[1];
            int mday = integers[0];
            int year = integers[5];
            int month = integers[4];
            int day = integers[3];
            int hour = integers[6];
            int min = integers[7];
            int mhour = integers[8];
            int mmin = integers[9];
            final SntpClient client = new SntpClient();
            this.publishProgress();
            if (client.requestTime("py.pool.ntp.org", 10000)) {
                long now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
                Calendar time = Calendar.getInstance();
                time.setTimeInMillis(now);
                myear = time.get(Calendar.YEAR);
                mmonth = time.get(Calendar.MONTH);
                mday = time.get(Calendar.DAY_OF_MONTH);
                mhour = time.get(Calendar.HOUR_OF_DAY);
                mmin = time.get(Calendar.MINUTE);
                Log.e("consultaNTP","successful");
            }
            else{
                Log.e("consultaNTP","ERROR: Request unsuccessful");
            }

            if (year<myear){
                aux_fecha = false;
                return ("seleccione un nuevo año, 'pasar a buscar' ");
            }
            else if (month<mmonth && year==myear) {
                aux_fecha = false;
                return ("mes ya pasado seleccionado, 'pasar a buscar' ");

            }
            else if (day<mday && month==mmonth && year==myear) {
                aux_fecha = false;
                return ("dia ya pasado seleccionado, 'pasar a buscar' ");
            }
            else if (day==mday && month==mmonth && year==myear){
                if (mhour<hour){
                    aux_hora = true;
                }
                else if (mhour==hour){
                    if (min>mmin){
                        aux_hora = true;
                    }
                    else {
                        aux_hora = false;
                        return ("seleccione una nuevo minuto, 'pasar a buscar' ");
                    }
                }
                else {
                    aux_hora = false;
                    return ("seleccione una nueva hora, 'pasar a buscar' ");
                }
            }
            else {
                try {
                    datos.put("hora_sel",hora_sel);
                    datos.put("fecha_sel",fecha_sel);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        protected void onProgressUpdate(String... value) {
            super.onProgressUpdate(value);

            progress.setTitle("Conectando");
            progress.setMessage("Espere un momento por favor...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();



        }

        protected void onPostExecute(String result) {
            if (result!=null){
                alert(result,0);
            }
            else {
                enviar();
            }
            progress.dismiss();
        }


    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @SuppressLint("StaticFieldLeak")
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            myear = c.get(Calendar.YEAR);
            mmonth= c.get(Calendar.MONTH);
            mday= c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, myear, mmonth, mday);
        }

        @SuppressLint("StaticFieldLeak")
        public void onDateSet(DatePicker view, int year, int month, int day) {
            year_select = year;
            month_select = month;
            day_select = day;
            fecha_sel = day+"-"+month+"-"+year;
            TextView fecha_show = findViewById(R.id.fecha);
            fecha_show.setText(day+"-"+"-"+month+" "+year);
        }
    }
    public void time(View v){

        Log.e("click","click");
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


}
