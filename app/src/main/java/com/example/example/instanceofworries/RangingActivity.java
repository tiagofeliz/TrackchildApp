package com.example.example.instanceofworries;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.example.instanceofworries.Adapter.AreaAdapter;
import com.example.example.instanceofworries.Entity.Area;
import com.example.example.instanceofworries.Utils.DatabaseHelper;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import static org.altbeacon.beacon.BeaconParser.ALTBEACON_LAYOUT;
import static org.altbeacon.beacon.BeaconParser.EDDYSTONE_TLM_LAYOUT;
import static org.altbeacon.beacon.BeaconParser.EDDYSTONE_UID_LAYOUT;
import static org.altbeacon.beacon.BeaconParser.EDDYSTONE_URL_LAYOUT;
import static org.altbeacon.beacon.BeaconParser.URI_BEACON_LAYOUT;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    Area area = new Area();
    ArrayList<Area> listArea = new ArrayList<Area>();
    AreaAdapter areaAdapter;
    DatabaseHelper db = new DatabaseHelper(this);

    String number, distance;

    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        areaAdapter = new AreaAdapter(this, listArea, "scanner");

        lv = (ListView) findViewById(R.id.listAreasBeacons);

        lv.setAdapter(areaAdapter);

        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));

        number = getIntent().getStringExtra("number");
        distance = getIntent().getStringExtra("identificacao");

        beaconManager.bind(this);
    }

    public void refreshList(ArrayList<Area> listArea) {
        this.listArea = listArea;
        areaAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) beaconManager.setBackgroundMode(false);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {//EditText editText = (EditText)RangingActivity.this.findViewById(R.id.rangingText);

                    for (Beacon beacon : beacons) {
                        logToDisplay(beacon.toString() + "--" + beacon.getDistance(), beacon.getTxPower(), beacon.getRssi(), beacon.getId2().toString());
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("quarto", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void logToDisplay(final String line, final int txpowerBeacon, final int rssiBeacon, final String major) {
        runOnUiThread(new Runnable() {
            public void run() {
                boolean find = false;
                for (Area area : listArea) {
                    if (area.getUUID().equals(line.split("id1: ")[1].split(" id2: ")[0])) {
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    Area novaArea = new Area();
                    novaArea.setUUID(line.split("id1: ")[1].split(" id2: ")[0]);
                    if ((major.equals("1"))){
                        novaArea.setDistancia("Escorregador");
                    }else{
                        novaArea.setDistancia("Balanço");
                    }
                    novaArea.setRssi("" + rssiBeacon);
                    novaArea.setTxpower("" + txpowerBeacon);
                    listArea.add(novaArea);
                    refreshList(listArea);
                }
            }
        });
    }

    public void iniciarMonitoramento(View view) {

        String uuids = "";
        String raio = "";

        for (Area area : areaAdapter.getAreasAux()) {
            if (uuids.equals("")) {
                uuids = uuids + area.getUUID();
                raio = raio + area.getRaio();
            } else {
                uuids = uuids + "--" + area.getUUID();
                raio = raio + "--" + area.getRaio();
            }
        }


        if (areaAdapter.getAreasAux().size() > 0) {
            Intent intent = new Intent(this, MonitoringActivity.class);
            intent.putExtra("uuids", uuids);
            intent.putExtra("number", number);
            intent.putExtra("raio", raio);
            intent.putExtra("identificacao", distance);
            startActivity(intent);
        } else {
            AlertDialog alerta;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Atenção");
            builder.setMessage("Selecione as áreas para monitoramento");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            alerta = builder.create();
            alerta.show();
        }
    }
}
