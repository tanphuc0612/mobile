package com.example.lightmap.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lightmap.model.Address;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "History_manager";
    private static final String TABLE_HISTORY_ADDRESS = "search_history_address";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LAT = "lat";
    private static final String COLUMN_LONG = "lng";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DETAIIL = "detail";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "MyDatabaseHelper.onCreate ... ");

        String s = "CREATE TABLE" +
                " " + TABLE_HISTORY_ADDRESS + " (" +
                COLUMN_ID + " TEXT PRIMARY KEY," +
                COLUMN_LONG + " REAL," +
                COLUMN_LAT + " REAL," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_DETAIIL + " TEXT);";
        db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "MyDatabaseHelper.onUpgrade ... ");
        // Hủy (drop) bảng cũ nếu nó đã tồn tại.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY_ADDRESS);
        // Và tạo lại.
        onCreate(db);
    }

    public void addAddress(Address address) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, address.getPlaceId());
        values.put(COLUMN_LONG, address.getLatLng().longitude);
        values.put(COLUMN_LAT, address.getLatLng().latitude);
        values.put(COLUMN_TITLE, address.getAddressTitle());
        values.put(COLUMN_DETAIIL, address.getAddressDetail());

        db.insert(TABLE_HISTORY_ADDRESS, null, values);
        db.close();
    }

//    public Address getAddress(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        Cursor cursor = db.query(TABLE_HISTORY_ADDRESS, new String[]{COLUMN_ID, COLUMN_LONG, COLUMN_LAT, COLUMN_TITLE, COLUMN_DETAIIL}, COLUMN_ID + "=?", new String[]{String.valueOf(id)},
//                null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//        Address address = new Address(cursor.getInt(0), cursor.getLong(1), cursor.getString(2));
//        return address;
//    }

    public List<Address> getAllHistories() {
        List<Address> addressList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String selection = "SELECT * FROM " + TABLE_HISTORY_ADDRESS;

        Cursor cursor = db.rawQuery(selection, null);

        if (cursor.moveToFirst()) {
            do {
                Address address = new Address();
                address.setPlaceId(cursor.getString(0));
                address.setLatLng(new LatLng(cursor.getDouble(2), cursor.getDouble(1)));
                address.setAddressTitle(cursor.getString(3));
                address.setAddressDetail(cursor.getString(4));
                addressList.add(address);
            } while (cursor.moveToNext());
        }
        return addressList;

    }


//    public int getNotesCount() {
//        Log.i(TAG, "MyDatabaseHelper.getNotesCount ... ");
//
//        String countQuery = "SELECT  * FROM " + TABLE_NOTE;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//
//        int count = cursor.getCount();
//
//        cursor.close();
//
//        // return count
//        return count;
//    }


//    public int updateNote(Note note) {
//        Log.i(TAG, "MyDatabaseHelper.updateNote ... " + note.getNoteTitle());
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_NOTE_TITLE, note.getNoteTitle());
//        values.put(COLUMN_NOTE_CONTENT, note.getNoteContent());
//
//        // updating row
//        return db.update(TABLE_NOTE, values, COLUMN_NOTE_ID + " = ?",
//                new String[]{String.valueOf(note.getNoteId())});
//    }

    public void deleteAddress(Address address) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_HISTORY_ADDRESS, COLUMN_ID + " = ?", new String[]{String.valueOf(address.getPlaceId())});
        db.close();
    }

}
