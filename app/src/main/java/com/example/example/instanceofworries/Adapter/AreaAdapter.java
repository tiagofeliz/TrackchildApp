package com.example.example.instanceofworries.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.example.instanceofworries.Entity.Area;
import com.example.example.instanceofworries.MonitoringActivity;
import com.example.example.instanceofworries.R;
import com.example.example.instanceofworries.RangingActivity;
import com.example.example.instanceofworries.Utils.DatabaseHelper;

import java.util.ArrayList;

/**
 * Created by THANATHOS on 08/03/2017.
 */

public class AreaAdapter extends ArrayAdapter<Area> {

    private Context context;
    private ArrayList<Area> areas;
    private String contexto;
    private ArrayList<Area> areasAux = new ArrayList<>();

    public ArrayList<Area> getAreasAux() {
        return areasAux;
    }

    public void setAreasAux(ArrayList<Area> areasAux) {
        this.areasAux = areasAux;
    }

    public AreaAdapter(Context context, ArrayList<Area> areas, String contexto) {
        super(context, 0, areas);
        this.context = context;
        this.areas = areas;
        this.contexto = contexto;
    }

    //[NÃO USO] não estou mais usando por abrir a intent direto na activity ranging
    public void openActivity(String uuidValue) {
        Intent intent = new Intent(context, MonitoringActivity.class);
        intent.putExtra("uuid", uuidValue);
        context.startActivity(intent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Area areaPosicao = this.areas.get(position);

        if (contexto.equals("scanner")) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_area, null);
            final TextView id = (TextView) convertView.findViewById(R.id.item_area_id);
            id.setText("" + areaPosicao.getStatus());
            TextView uuid = (TextView) convertView.findViewById(R.id.item_area_uuid);
            uuid.setText(areaPosicao.getUUID());
            final String uuidValue = uuid.getText().toString();
            TextView distancia = (TextView) convertView.findViewById(R.id.item_distancia);
            distancia.setText(areaPosicao.getDistancia());
            TextView rssi = (TextView) convertView.findViewById(R.id.item_rssi);
            rssi.setText("Rssi: " + areaPosicao.getRssi());
            TextView txpower = (TextView) convertView.findViewById(R.id.item_txpower);
            txpower.setText("txPower: " + areaPosicao.getTxpower());
            final CheckBox check = (CheckBox) convertView.findViewById(R.id.check_minha_area);
            check.setOnClickListener(new View.OnClickListener() {
                Area area;

                @Override
                public void onClick(View view) {

                    if (check.isChecked()) {
                        area = areaPosicao;
                        //valor default para o raio, para caso não o configurem no dispositivo beacon
                        area.setRaio("4.00");
                        areasAux.add(area);
                    } else {
                        areasAux.remove(areaPosicao);
                    }
                }
            });
        } else {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.item_monitor, null);
            TextView id = (TextView) convertView.findViewById(R.id.item_area_id);
            id.setText("" + areaPosicao.getStatus());
            TextView uuid = (TextView) convertView.findViewById(R.id.item_area_uuid);
            uuid.setText(areaPosicao.getUUID());
            TextView distancia = (TextView) convertView.findViewById(R.id.item_distancia);
            distancia.setText(areaPosicao.getDistancia());
        }


        return convertView;
    }
}
