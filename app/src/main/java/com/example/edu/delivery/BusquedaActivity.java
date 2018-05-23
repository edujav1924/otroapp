package com.example.edu.delivery;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class BusquedaActivity extends AppCompatActivity{
    JSONArray jsono;
    RecyclerView recyclerView;
    MaterialSearchView searchView;
    TextView result_text;
    RecyclerAdapter adapter;
    ArrayList<String> direcciones=new ArrayList<>();
    ArrayList<String> tiempo=new ArrayList<>();
    ArrayList<String> distancias=new ArrayList<>();
    String lat,lon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda);
        distancias= getIntent().getExtras().getStringArrayList("distancias");
        tiempo = getIntent().getExtras().getStringArrayList("tiempo");
        direcciones = getIntent().getExtras().getStringArrayList("direcciones");
        lat = getIntent().getExtras().getString("latitud");
        lon = getIntent().getExtras().getString("longitud");
        try {
            jsono = new JSONArray(getIntent().getExtras().getString("json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Toolbar toolbar2 = findViewById(R.id.toolbar2);
        toolbar2.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar2);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        result_text = findViewById(R.id.result_text);
        result_text.setVisibility(View.GONE);
        recyclerView = findViewById(R.id.customrecicler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.setItems(jsono,distancias,lat,lon);
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (hasSubstring(jsono,newText)){
                    result_text.setText(("No se encontraron resultados"));
                    result_text.setVisibility(View.VISIBLE);
                }
                else {
                    result_text.setVisibility(View.GONE);
                }
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    public boolean hasSubstring(JSONArray c, String substring) {
        JSONArray customjson = new JSONArray();
        for (int i=0;i<c.length();i++){
            try {
                if( c.getJSONObject(i).getString("empresa").toLowerCase().contains(substring.toLowerCase())) {
                    customjson.put(c.getJSONObject(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.setItems(customjson, distancias, lat, lon);
        adapter.notifyDataSetChanged();
        if(customjson.length()>0){
            return false;
        }else {
            return true;
        }
    }
    @Override
    public void onBackPressed() {
    if (searchView.isSearchOpen()) {
        searchView.closeSearch();
    } else {
        super.onBackPressed();
    }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.mymenu, menu);
    MenuItem item = menu.findItem(R.id.action_search);
    searchView.setMenuItem(item);
    return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
        return true;
    }
    return super.onOptionsItemSelected(item);
    }

}
