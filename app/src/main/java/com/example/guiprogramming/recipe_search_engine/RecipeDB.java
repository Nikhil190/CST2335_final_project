package com.example.guiprogramming.recipe_search_engine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Used this class to connect with SQLite database
 * */
public class RecipeDB {

    SQLiteDatabase db;
    ConnectHelper conn;
    Context context;
    final int version = 1;
    final String db_name = "recipe_database";
    String table_name = "recipe_table";
    String title = "title";
    String publisher = "publisher";
    String f2f_url = "f2f_url";
    String source_url = "source_url";
    String recipe_id = "recipe_id";
    String image_url = "image_url";
    String publisher_url = "publisher_url";
    String social_rank = "social_rank";

    /**
     * @param context context of an activity
     * */
    RecipeDB(Context context) {
        this.context = context;
    }

    /**
     * To open connection with the SQLite database
     * */
    public void open() {
        conn = new ConnectHelper(context);
        db = conn.getWritableDatabase();
    }

    /**
     * To close opened connection for SQLite database
     * */
    public void close() {
        conn.close();
    }

    /***
     * To create and manage
     * table details
     * */
    class ConnectHelper extends SQLiteOpenHelper {

        ConnectHelper(Context context) {
            super(context, db_name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //crates table first time
            String query = "create table " + table_name + " (" +
                    title + " text, " +
                    publisher + " text, " +
                    f2f_url + " text, " +
                    source_url + " text, " +
                    recipe_id + " text, " +
                    image_url + " text, " +
                    publisher_url + " text, " +
                    social_rank + " real " +
                    ")";
            db.execSQL(query);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + table_name);
            onCreate(db);
        }
    }

    /**
     * Retrieves all the data stored in recipe table
     * @return ArrayList<RecipeData> of recipe stored in SQLite database
     * */
    public ArrayList<RecipeData> getAllRecipe() {
        ArrayList<RecipeData> recipeData = new ArrayList<>();
        Cursor cursor = db.query(table_name, new String[]{
                        title, publisher, f2f_url, source_url,
                        recipe_id, image_url, publisher_url,
                        social_rank
                },
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String publisher = cursor.getString(1);
                String f2f_url = cursor.getString(2);
                String source_url = cursor.getString(3);
                String recipe_id = cursor.getString(4);
                String image_url = cursor.getString(5);
                String publisher_url = cursor.getString(6);
                double social_rank = cursor.getDouble(7);
                RecipeData data = new RecipeData(title, publisher,
                        f2f_url, source_url, recipe_id,
                        image_url, publisher_url, social_rank);
                recipeData.add(data);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recipeData;
    }

    /**
     * @param recipe_id unique id of a recipe
     * @return true if the recipe with recipe_id exists in table, false otherwise
     * */
    public boolean exists(String recipe_id) {
        Cursor cursor = db.query(table_name, new String[]{title},
                this.recipe_id + " = ?", new String[]{recipe_id},
                null, null, null);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = true;
        }
        cursor.close();
        return exists;
    }

    /**
     * INSERT operation, a new row will be added,
     *      if the record does not exists already.
     *
     * @param title title of recipe
     * @param publisher name of publisher
     * @param f2f_url relevant API web page
     * @param source_url Source URL
     * @param recipe_id unique id of a recipe
     * @param image_url image URL of recipe
     * @param publisher_url publisher URL
     * @param social_rank social rank
     *
     * @return -1 if record already exists, or number of inserted rows otherwise
     * */
    public long addToDB(String title, String publisher,
                        String f2f_url, String source_url, String recipe_id,
                        String image_url, String publisher_url,
                        double social_rank) {
        if (exists(recipe_id)) {
            return -1;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(this.title, title);
            contentValues.put(this.publisher, publisher);
            contentValues.put(this.f2f_url, f2f_url);
            contentValues.put(this.source_url, source_url);
            contentValues.put(this.recipe_id, recipe_id);
            contentValues.put(this.image_url, image_url);
            contentValues.put(this.publisher_url, publisher_url);
            contentValues.put(this.social_rank, social_rank);
            return db.insert(table_name, null, contentValues);
        }
    }

    /**
     * DELETE operation
     * @param recipe_id unique id of a recipe
     * @return true if row deleted, false otherwise
     * */
    public boolean removeFromDB(String recipe_id) {
        return db.delete(table_name, this.recipe_id + "=?", new String[]{recipe_id}) > 0;
    }

}
