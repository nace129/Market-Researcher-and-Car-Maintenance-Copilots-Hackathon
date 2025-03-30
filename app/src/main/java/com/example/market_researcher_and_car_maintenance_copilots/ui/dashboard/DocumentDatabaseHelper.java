package com.example.market_researcher_and_car_maintenance_copilots.ui.dashboard;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.database.Cursor;
import java.util.ArrayList;
import java.util.List;

public class DocumentDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "documents.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_DOCUMENTS = "documents";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEXT = "text";

    public DocumentDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_DOCUMENTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TEXT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if needed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);
        onCreate(db);
    }

    public void insertDocument(String text) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEXT, text);
        db.insert(TABLE_DOCUMENTS, null, values);
        db.close();
    }

    // Add a method to retrieve all document texts (or perform more advanced searches)
    // For a retrieval mechanism, you might add methods like searchDocuments(String query)
    public List<String> searchDocuments(String query) {
        List<String> results = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Use SQL LIKE for simple matching (this is basic – adjust as needed)
        Cursor cursor = db.query(TABLE_DOCUMENTS, new String[]{COLUMN_TEXT},
                COLUMN_TEXT + " LIKE ?", new String[]{"%" + query + "%"},
                null, null, null);
        if(cursor != null) {
            while(cursor.moveToNext()) {
                results.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEXT)));
            }
            cursor.close();
        }
        db.close();
        return results;
    }
}

