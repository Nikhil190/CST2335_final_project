package com.example.guiprogramming.foreign_currency;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Used for handling database operations
 * */
public class FavouritesDB {

    //context of an activity or fragment
    Context context;

    SQLiteDatabase db;

    String databaseName = "foreignCurrencyDB";
    String tableName = "conversionFav";
    String id = "fav_id";
    String baseSymbol = "baseSymbol";
    String foreignSymbol = "foreignSymbol";
    String selectedPosition = "selectedPosition";

    DBOpenHelper openHelper;

    /**
     * @param context context of a Activity or fragment
     * */
    public FavouritesDB(Context context) {
        this.context = context;
    }

    /**
     * This method has to be called before
     * performing any operation into the database
     * */
    public void openDB() {
        openHelper = new DBOpenHelper(context);
        db = openHelper.getWritableDatabase();
    }

    /**
     *
     * Retrieves all the columns for favourites
     * @return ArrayList of type CurrencyMap -
     *          favourites stored in database
     * */
    public ArrayList<CurrencyMap> getAllFavourites() {
        ArrayList<CurrencyMap> favourites = new ArrayList<>();
        Cursor cursor = db.query(tableName, new String[]{id, baseSymbol, foreignSymbol, selectedPosition},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String baseSymbol = cursor.getString(1);
                String foreignSymbol = cursor.getString(2);
                int selectedPosition = cursor.getInt(3);
                CurrencyMap currencyMap = new CurrencyMap(id, baseSymbol, foreignSymbol, selectedPosition);
                favourites.add(currencyMap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return favourites;
    }

    /**
     * Checks for duplication, if it's a unique record,
     * then insert, or reject otherwise
     * @return the row ID of the newly inserted row,
     * or -1 if an error occurred
     *
     * */
    public long addToFavorite(String baseSymbolValue, String foreignSymbolValue, int selectedPositionValue) {
        if (isDuplicate(baseSymbolValue, foreignSymbolValue)) {
            return -1;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(baseSymbol, baseSymbolValue);
            contentValues.put(foreignSymbol, foreignSymbolValue);
            contentValues.put(selectedPosition, selectedPositionValue);
            return db.insert(tableName, null, contentValues);
        }
    }

    /**
     * Removes the row provided where column has ID
     * @param id id of a record
     * @return true if one or more rows deleted, or false otherwise
     * */
    public boolean removeFavourite(String id) {
        return db.delete(tableName, this.id + "=?", new String[]{id}) > 0;
    }

    /**
     * Removes the row provided where column has ID
     * @param baseSymbolValue baseSymbol of a currency
     * @param foreignSymbolValue foreign symbol of a currency
     * @return true if one duplicate value found, or false otherwise
     * */
    public boolean isDuplicate(String baseSymbolValue,
                               String foreignSymbolValue) {
        Cursor cursor = db.query(tableName,
                new String[]{baseSymbol, foreignSymbol},
                "(" + baseSymbol + " = ? OR " + baseSymbol + " = ?)" +
                        " AND (" + foreignSymbol + " = ? OR " + foreignSymbol + " = ?)",
                new String[]{baseSymbolValue, foreignSymbolValue, baseSymbolValue, foreignSymbolValue},
                null, null, null);
        boolean isDuplicate = cursor.moveToFirst();
        cursor.close();
        return isDuplicate;
    }

    /**
     * Searches for record, which has base and foreign matching values
     * @param baseSymbolValue baseSymbol of a currency
     * @param foreignSymbolValue foreign symbol of a currency
     * @return CurrencyMap of a record
     * */
    public CurrencyMap getFavourite(String baseSymbolValue,
                               String foreignSymbolValue) {
        Cursor cursor = db.query(tableName,
                new String[]{id, baseSymbol, foreignSymbol, selectedPosition},
                "(" + baseSymbol + " = ? OR " + baseSymbol + " = ?)" +
                        " AND (" + foreignSymbol + " = ? OR " + foreignSymbol + " = ?)",
                new String[]{baseSymbolValue, foreignSymbolValue, baseSymbolValue, foreignSymbolValue},
                null, null, null);
        CurrencyMap favourite = new CurrencyMap();
        if(cursor.moveToFirst()){
            favourite.setId(cursor.getInt(0));
            favourite.setBaseSymbol(cursor.getString(1));
            favourite.setForeignSymbol(cursor.getString(2));
            favourite.setSelectedPosition(cursor.getInt(3));
        }
        cursor.close();
        return favourite;
    }

    /**
     * After performing each operation in the database,
     * database has to be closed.
     * */
    public void closeDB() {
        openHelper.close();
    }

    /**
     * Helps to open SQLiteDatabase
     * */
    class DBOpenHelper extends SQLiteOpenHelper {
        DBOpenHelper(Context context) {
            super(context, databaseName, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + tableName + " (" +
                    id + " integer primary key autoincrement," +
                    baseSymbol + " text," +
                    foreignSymbol + " text," +
                    selectedPosition + " int)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + tableName);
            onCreate(db);
        }
    }
}
