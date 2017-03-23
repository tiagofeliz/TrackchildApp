package com.example.example.instanceofworries;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.example.instanceofworries.Adapter.AreaAdapter;
import com.example.example.instanceofworries.Entity.Area;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class MonitoringActivity extends AppCompatActivity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;

    //entidade global área
    Area area = new Area();
    //arraylist global de áreas
    ArrayList<Area> listArea = new ArrayList<Area>();
    //adapter para as listas do aplicativo
    AreaAdapter areaAdapter;
    //componente listView
    ListView lv;
    //valores de uuid's e seus respectivos raios padrão
    String uuidValues;
    String raioValues;
    //número de telefone informado pelo usuário e variável para as mensagens de texto
    String phoneNo;
    String message;//[NÃO USO]
    //identificação da pulseira
    String identificacao;
    //variável para armazenamento do uuid ativo no momento
    String uuidAtivo = "";
    //recuperando o contexto da aplicação
    Context context = this;
    //[ANDROID: NOTIFICAÇÃO] ID para gerenciamento das notificações pull
    private static final int NOTIFICATION_ID = 123;
    //objeto gerenciador de notificações
    NotificationManager notificationManager;
    //variável para identificar o tipo da atualização lançada
    public int flagRegion = 0;
    //[ANDROID: MENSAGENS] estado atual das permissões de sms para o aplicativo
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        //instancia o gerenciador de notificações
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //instancia o gerenciador de beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //[INFORMAÇÃO SOBRE A BIBLIOTECA UTILIZADA NO APLICATIVO]
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //resgatando valores passados por EXTRA
        uuidValues = getIntent().getStringExtra("uuids");
        raioValues = getIntent().getStringExtra("raio");
        phoneNo = getIntent().getStringExtra("number");
        identificacao = getIntent().getStringExtra("identificacao");
        //instancia o adapter para as listas apresentadas
        areaAdapter = new AreaAdapter(this, listArea, "monitor");
        //referenciando componente list view
        lv = (ListView) findViewById(R.id.listAreasBeacons);
        //inserindo adapter configurado
        lv.setAdapter(areaAdapter);
        //configurando recursos de beacons
        beaconManager.bind(this);
    }

    //atualizar a lista a cada atualização lançada
    public void refreshList(ArrayList<Area> listArea) {
        this.listArea = listArea;
        areaAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(NOTIFICATION_ID);
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

    //[BEACON] conexão e serviços relacionados aos beacons
    @Override
    public void onBeaconServiceConnect() {

        //escaneando
        beaconManager.setRangeNotifier(new RangeNotifier() {

            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                Beacon firstBeacon = null;
                if (beacons.size() > 0) {
                    boolean beaconValidate = false;
                    //analisar beacons encontrados
                    for (Beacon beacon : beacons) {
                        //caso o usuário já esteja em alguma área, não é necessário verificar com todos os beacons
                        if (flagRegion == 1) {
                            if (beacon.getId1().toString().equals(uuidAtivo)) {
                                beaconValidate = true;
                                firstBeacon = beacon;
                                break;
                            }
                        } else {
                            for (int i = 0; i < uuidValues.split("--").length; i++) {
                                if (uuidValues.split("--")[i].equals(beacon.getId1().toString())) {
                                    beaconValidate = true;
                                    firstBeacon = beacon;
                                    break;
                                }
                            }
                        }
                        if (beaconValidate) {
                            break;
                        }
                    }
                    if (beaconValidate) {
                        if (uuidAtivo.equals("") || firstBeacon.getId1().toString().equals(uuidAtivo)) {
                            char[] vetDistancia = String.valueOf(firstBeacon.getDistance()).toCharArray();
                            String distancia = "";
                            for (int i = 0; i < 4; i++) {
                                distancia = distancia + vetDistancia[i];
                            }
                            if (Double.parseDouble(distancia) < Double.parseDouble(firstBeacon.getId3().toString() + ".00") && flagRegion == 0) {
                                if (firstBeacon.getId2().toString().equals("1")) {
                                    logToDisplay(identificacao + " entrou numa região: " + distancia, "Escorregador");
                                    postNotification("Mudança de posição (Escorregador): " + distancia);
                                    sendSMSMessage(identificacao + " entrou no Escorregador: " + distancia);
                                    newSmsSend(identificacao + " entrou no Escorregador: " + distancia);
                                } else {
                                    logToDisplay(identificacao + " entrou numa região: " + distancia, "Balanço");
                                    postNotification("Mudança de posição (Balanço): " + distancia);
                                    sendSMSMessage(identificacao + " entrou no Balanço: " + distancia);
                                    newSmsSend(identificacao + " entrou no Balanço: " + distancia);
                                }
                                flagRegion = 1;
                                uuidAtivo = firstBeacon.getId1().toString();
                            }
                            if (Double.parseDouble(distancia) > Double.parseDouble(firstBeacon.getId3().toString() + ".00") && flagRegion == 1) {
                                if (firstBeacon.getId1().toString().equals(uuidAtivo)) {
                                    if (firstBeacon.getId2().toString().equals("1")) {
                                        logToDisplay(identificacao + " saiu de uma região: " + distancia, "Escorregador");
                                        postNotification("Mudança de posição (Escorregador): " + distancia);
                                        sendSMSMessage(identificacao + " saiu do Escorregador: " + distancia);
                                        newSmsSend(identificacao + " saiu do Escorregador: " + distancia);
                                    } else {
                                        logToDisplay(identificacao + " saiu de uma região: " + distancia, "Balanço");
                                        postNotification("Mudança de posição (Balanço): " + distancia);
                                        sendSMSMessage(identificacao + " saiu do Balanço: " + distancia);
                                        newSmsSend(identificacao + " saiu do Balanço: " + distancia);
                                    }
                                    flagRegion = 0;
                                    uuidAtivo = "";
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    //[ANDROID: NOTIFICAÇÃO] apresentação de notificação pull para o usuário da pulseira
    private void postNotification(String msg) {
        Intent notifyIntent = new Intent(MonitoringActivity.this, MainActivity.class);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                MonitoringActivity.this, 0, new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(MonitoringActivity.this)
                .setSmallIcon(R.drawable.alert256b)
                .setContentTitle("Monitoramento atual").setContentText(msg)
                .setAutoCancel(true).setContentIntent(pendingIntent).build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    //apresentar na tela de monitoramento a atualização lançada quanto a transição do usuário pelas áreas demarcadas
    private void logToDisplay(final String line, final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Area novaArea = new Area();
                novaArea.setUUID(line);
                novaArea.setDistancia(msg);
                listArea.add(novaArea);
                refreshList(listArea);
            }
        });
    }

    //[NÃO USO] este método é chamado uma única vez, apenas quando a permissão para as mensagens é requerida
    protected void sendSMSMessage(String msg) {
        message = msg;

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    //[ANDROID: PERMISSÃO] requerindo permisssão para utilização dos recursos de sms
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    //método para envio de mensagens de texto utilizando o gerenciador de sms padrão do dispositivo
    public void newSmsSend(String msg) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }

    //finalizar a monitoração (finaliza a activity)
    public void stopMonitoring(View view) {
        finish();
    }

    //[TESTE] teste de envio de mensagens de texto
    public void teste(View view) {
        sendSMSMessage("Uma mensagem foi enviada");
    }
}

