package com.example.edu.delivery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import greco.lorenzo.com.lgsnackbar.core.LGSnackbar;


public class ScrollingActivity extends AppCompatActivity {
    ImageView logo;
    ScrollableNumberPicker numberPicker;
    ListView pedidos_seleccionados;
    TextView total_text;
    Spinner categoria;
    ArrayList<String> pedido_list = new ArrayList<>();
    ArrayList<Integer> precio_list = new ArrayList<>();
    ArrayList<Integer> cant_list = new ArrayList<>();
    ArrayList<String> pedido_list_spinner = new ArrayList<>();
    ArrayList<String> precio_list_spinner = new ArrayList<>();
    private int id_seleccion;
    customadapterlist madapter;
    JSONArray jsondata;
    String position;
    int total=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        categoria = findViewById(R.id.categoria);
        pedidos_seleccionados = findViewById(R.id.lista);
        total_text = findViewById(R.id.total);
        logo = findViewById(R.id.logo);
        try {
            jsondata = new JSONArray(getIntent().getExtras().getString("objeto"));
            position = getIntent().getExtras().getString("posicion");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        toolbars();
        spinners();
        numberpickers();

    }
    public void total(){
        total=0;
        for (int i=0;i<pedido_list.size();i++){
            total = total + precio_list.get(i);
        }
        total_text.setText((String.valueOf(total)+" Gs."));

    }
    public void btn_agregar(View view){
        int canti = numberPicker.getValue();
        if (id_seleccion!=0 && canti!=0) {

            Integer precio = Integer.parseInt(precio_list_spinner.get(id_seleccion));
            String producto = pedido_list_spinner.get(id_seleccion);
            if (!pedido_list.contains(producto)) {
                precio_list.add(precio*canti);
                pedido_list.add(producto);
                cant_list.add(canti);

            }
            else {
                int id = pedido_list.indexOf(producto);
                precio_list.set(id,precio*canti);
                cant_list.set(id,canti);

            }
            numberPicker.setValue(numberPicker.getMinValue());
            listviews();
            Utility.setListViewHeightBasedOnChildren(pedidos_seleccionados);
            total();


        }

    }
    private void spinners() {

        int position = Integer.parseInt(getIntent().getExtras().getString("posicion"));
        try {
            pedido_list_spinner.add("--Seleccione--");
            precio_list_spinner.add("----");
            JSONArray objeto = new JSONArray(getIntent().getExtras().getString("objeto"));
            for (int i =0;i<objeto.getJSONObject(position).getJSONArray("productos").length();i++){
                pedido_list_spinner.add(objeto.getJSONObject(position).getJSONArray("productos").getJSONObject(i).getString("producto"));
                precio_list_spinner.add(objeto.getJSONObject(position).getJSONArray("productos").getJSONObject(i).getString("precio"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        customadapterspinner adaptador = new customadapterspinner(this, pedido_list_spinner, precio_list_spinner);
        categoria.setAdapter(adaptador);
        categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_seleccion = adapterView.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });
    }
    private void toolbars(){
        final CollapsingToolbarLayout collapsingToolbarLayout =  findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout =  findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset <= 127) {
                    collapsingToolbarLayout.setTitle("Delivery ON");

                    logo.animate().alpha(0.0f).setDuration(500);


                    isShow = true;
                } else if(isShow) {
                    logo.animate().alpha(1.0f).setDuration(500);
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

    }
    private void listviews(){
        madapter = new customadapterlist(this,pedido_list,precio_list,cant_list);
        pedidos_seleccionados.setAdapter(madapter);
        pedidos_seleccionados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showAlert(i);
            }
        });
    }
    private void numberpickers(){
        numberPicker = findViewById(R.id.number);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.custommenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_terminos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialog_view= inflater.inflate(R.layout.forma_de_uso, null);
            builder.setView(dialog_view);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).create().show();
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_quejas) {
            /*final EditText nombre_ = findViewById(R.id.nombre_sug);
            final EditText apellido = findViewById(R.id.apellido_sug);
            final EditText mensaje = findViewById(R.id.mensaje_sug);*/
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Get the layout inflater
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialog_view= inflater.inflate(R.layout.content_reclamos, null);
            builder.setView(dialog_view);


            builder.setPositiveButton("enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText nombre = dialog_view.findViewById(R.id.nombre_sug);
                        EditText apellido = dialog_view.findViewById(R.id.apellido_sug);
                        EditText mensaje = dialog_view.findViewById(R.id.mensaje_sug);
                        if (!nombre.getText().toString().equals("") &&
                                !apellido.getText().toString().equals("") &&
                                !mensaje.getText().toString().equals("")){
                            enviar_comentario(nombre.getText().toString(),apellido.getText().toString(),mensaje.getText().toString());
                        }
                        else {
                            alert("Existen campos vacios",0);
                        }

                    }
                }).setTitle("Formulario").setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();


            return true;
        }
        if(id == R.id.action_send){
            if(!pedido_list.isEmpty()){
                JSONObject customobj = new JSONObject();
                try {
                    customobj.put("precio_total",total);
                    customobj.put("latitud", getIntent().getExtras().getString("latitud"));
                    customobj.put("longitud", getIntent().getExtras().getString("longitud"));
                    customobj.put("empresa",jsondata.getJSONObject(Integer.parseInt(position)).getString("empresa"));
                    customobj.put("empresa_id",Integer.parseInt(position)+1);
                    customobj.put("ubicacion",
                            "https://www.google.com/maps?q=" +
                                    getIntent().getExtras().getString("latitud")+","+getIntent().getExtras().getString("longitud"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArr = new JSONArray();
                for(int i = 0;i<pedido_list.size();i++){
                    JSONObject pedidos = new JSONObject();
                    try {
                        pedidos.put("producto",pedido_list.get(i));
                        pedidos.put("cantidad",cant_list.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jsonArr.put(pedidos);
                }
                try {
                    ArrayList<String> distancia = (ArrayList) getIntent().getExtras().getSerializable("distancias");
                    customobj.put("distancia",distancia.get(Integer.parseInt(position)));
                    customobj.put("pedidos",jsonArr);
                    Intent intent = new Intent(this,send.class);
                    intent.putExtra("datos", String.valueOf(customobj));
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                new LGSnackbar.LGSnackbarBuilder(getApplicationContext(), "Lista Vacia")
                        .duration(2000)
                        .actionTextColor(Color.RED)
                        .backgroundColor(Color.RED)
                        .minHeightDp(20)
                        .textColor(Color.WHITE)
                        .iconID(R.drawable.ic_custom_error)
                        .callback(null)
                        .action(null)
                        .show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void enviar_comentario(String nombre, String apellido, String mensaje) {
        JSONObject dato = new JSONObject();
        try {
            dato.put("nombre",nombre);
            dato.put("apellido",apellido);
            dato.put("mensaje",mensaje);
            volley_post(dato);
        } catch (JSONException e) {
            e.printStackTrace();
            alert("error al compilar datos",0);
        }

    }
    private void volley_post(JSONObject dato){
        RequestQueue request = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "https://delivery.simplelectronica.com/comentarios",dato, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String valor = jsonObject.getString("status");
                    Log.e("respnse",valor);
                    if (valor.equals("exitoso")){
                        new CDialog(ScrollingActivity.this).createAlert(valor,
                                CDConstants.SUCCESS,   // Type of dialog
                                CDConstants.LARGE)    //  size of dialog
                                .setAnimation(CDConstants.SCALE_FROM_RIGHT_TO_LEFT)     //  Animation for enter/exit
                                .setDuration(3000)   // in milliseconds
                                .setTextSize(CDConstants.NORMAL_TEXT_SIZE)  // CDConstants.LARGE_TEXT_SIZE, CDConstants.NORMAL_TEXT_SIZE
                                .show();

                    }else {
                        new CDialog(ScrollingActivity.this).createAlert(valor,
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

                if (error instanceof TimeoutError) {
                    alert("Error al conectar con el servidor",0);
                }else if (error instanceof NoConnectionError){
                    alert("falla de internet",0);
                }
                else if (error instanceof AuthFailureError) {
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
    public static class Utility {
        static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }
    private void showAlert(final int id) {

        new FancyAlertDialog.Builder(this)
                .setTitle("Pedido")
                .setBackgroundColor(Color.parseColor("#42a1f4"))  //Don't pass R.color.colorvalue
                .setMessage("Desea quitar '"+cant_list.get(id)+" "+pedido_list.get(id)+"s' de la lista de pedidos?")
                .setNegativeBtnText("Cancelar")
                .setPositiveBtnBackground(Color.parseColor("#42a1f4"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Eliminar")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.ic_info_outline_black_24dp, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        pedido_list.remove(id);
                        precio_list.remove(id);
                        cant_list.remove(id);
                        total();
                        listviews();
                        Utility.setListViewHeightBasedOnChildren(pedidos_seleccionados);
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();

                    }
                })
                .build();


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

}
