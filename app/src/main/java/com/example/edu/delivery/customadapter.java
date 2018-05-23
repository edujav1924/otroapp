package com.example.edu.delivery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

class customadapterspinner extends BaseAdapter {
    Context context;
    private String[] countryNames;
    private String[] subs;
    LayoutInflater inflter;

    public customadapterspinner(Context applicationContext, ArrayList<String> countryNames, ArrayList<String> subs) {
        this.context = applicationContext;
        this.countryNames = countryNames.toArray(new String[0]);
        this.subs = subs.toArray(new String[0]);
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return countryNames.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_items, null);
        TextView names = view.findViewById(R.id.title);
        TextView subtitlulo = view.findViewById(R.id.sub);
        names.setText(countryNames[i]);
        subtitlulo.setText(subs[i]+" Gs.");
        return view;
    }
}

class customadapterlist extends BaseAdapter {
    Context context;
    private String[] pedido;
    private Integer[] precio;
    private Integer [] cantidad;
    LayoutInflater inflter;

    public customadapterlist(Context applicationContext, ArrayList<String> pedido, ArrayList<Integer> precio, ArrayList<Integer> cantidad) {
        this.context = applicationContext;
        this.pedido = pedido.toArray(new String[0]);
        this.precio = precio.toArray(new Integer[0]);
        this.cantidad = cantidad.toArray(new Integer[0]);
        inflter = (LayoutInflater.from(applicationContext));
    }



    @Override
    public int getCount() {
        return pedido.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_list_items, null);
        TextView names = view.findViewById(R.id.pedido_item_list);
        TextView subtitlulo = view.findViewById(R.id.precio_item_list);
        TextView cantidad_text = view.findViewById(R.id.cantidad_);
        names.setText(pedido[i]);
        subtitlulo.setText(String.valueOf(precio[i])+" Gs.");
        cantidad_text.setText(String.valueOf(cantidad[i]));
        return view;
    }
}

class customadapterubicacion extends BaseAdapter {
    Context context;
    private String[] lugar;
    LayoutInflater inflter;

    public customadapterubicacion(Context applicationContext, ArrayList<String> lugar) {
        this.context = applicationContext;
        this.lugar = lugar.toArray(new String[0]);
        inflter = (LayoutInflater.from(applicationContext));
    }
    @Override
    public int getCount() {
        return lugar.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.custom_spinner_ubicacion, null);
        TextView ubicationview = view.findViewById(R.id.text_ubi);
        ubicationview.setText(lugar[i]);
        return view;
    }
}
