package com.example.example.instanceofworries.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.example.instanceofworries.Entity.Area;

/**
 * Created by THANATHOS on 08/03/2017.
 */

//[NÃO ESTOU UTILIZANDO NO MOMENTO]
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "worriesscanner.db";
    private static final String TABLE_NAME = "area";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IDADE = "idade";
    private static final String COLUMN_IDENTIFICACAO = "identificacao";
    private static final String COLUMN_RISCO = "risco";
    private static final String COLUMN_UUID = "UUID";
    private static final String COLUMN_MAJOR = "major";
    private static final String COLUMN_MINOR = "minor";
    private static final String COLUMN_TIPO = "tipo";
    private static final String COLUMN_RSSI = "rssi";
    private static final String COLUMN_TXPOWER = "txpower";
    SQLiteDatabase db;
    private static final String TABLE_CREATE = "create table area ( id integer primary key not null, " +
            "idade text, " +
            "identificacao text, " +
            "risco text, " +
            "UUID text, " +
            "major text, " +
            "minor text, " +
            "tipo text, " +
            "rssi text, " +
            "txpower text);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void insertArea(Area area) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from area";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_ID, count);
        values.put(COLUMN_IDADE, area.getFlag());
        values.put(COLUMN_IDENTIFICACAO, area.getIdentificacao());
        values.put(COLUMN_RISCO, area.getRisco());
        values.put(COLUMN_UUID, area.getUUID());
        values.put(COLUMN_MAJOR, area.getMajor());
        values.put(COLUMN_MINOR, area.getMinor());
        values.put(COLUMN_TIPO, area.getTipo());
        values.put(COLUMN_RSSI, area.getRssi());
        values.put(COLUMN_TXPOWER, area.getTxpower());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void insertArea(String uuid) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        String query = "select * from area";
        Cursor cursor = db.rawQuery(query, null);
        int count = cursor.getCount();

        values.put(COLUMN_ID, count);
        values.put(COLUMN_UUID, uuid);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void clearArea(String uuid) {
        db.execSQL("delete from usuario where uuid = "+uuid);
    }

    public String getUsuario() {
        db = this.getReadableDatabase();
        String query = "select usuario, senha from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        String usuario = cursor.getString(0)+"-separador-"+cursor.getString(1);
        return usuario;
    }

    public int countArea(String uuid){
        db = this.getReadableDatabase();
        String query = "select uuid from " + TABLE_NAME + "where uuid = "+uuid;
        Cursor cursor = db.rawQuery(query, null);
        return cursor.getCount();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String query = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(query);
        this.onCreate(db);
    }
}