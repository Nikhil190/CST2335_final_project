package com.example.guiprogramming.news_api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * class for storing data into the SQLiteDatabase
 * */
public class NewsDB {

    Context context;
    SQLiteDatabase database;
    SQLiteDatabaseConnection connection;

    String database_name = "news_database";
    String table_name = "saved_news";
    String article_title = "title";
    String article_description = "description";
    String article_url = "url";
    String article_image_url = "image_url";

    /**
     * @param context context of an activity or fragment
     * */
    public NewsDB(Context context) {
        this.context = context;
    }

    /**
     * Inner class to help the NewsDB class to provide
     * custom methods
     * */
    class SQLiteDatabaseConnection extends SQLiteOpenHelper {

        public SQLiteDatabaseConnection(@Nullable Context context) {
            super(context, database_name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //creating SQLite table
            db.execSQL("create table " + table_name
                    + "( " + article_title + " text ,"
                    + article_description + " text ,"
                    + article_url + " text ,"
                    + article_image_url + " text"
                    + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //drop existing table on upgrade
            db.execSQL("drop table if exists " + table_name);
            onCreate(db);
        }
    }

    /**
     * call this method before performing any
     * operation to the database
     * */
    public void openConnection() {
        connection = new SQLiteDatabaseConnection(context);
        database = connection.getWritableDatabase();
    }

    /**
     * call this method after performing any
     * operation to the database
     * */
    public void closeConnection() {
        connection.close();
    }

    /**
     * @return a list of saved articles in the database
     * */
    public ArrayList<Article> getAllNews() {
        ArrayList<Article> articles = new ArrayList<>();
        Cursor cursor = database.query(table_name,
                new String[]{article_title,
                        article_description,
                        article_url, article_image_url},
                null, null, null,
                null, null);
        //checking if the cursor has the data
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(0);
                String desc = cursor.getString(1);
                String url = cursor.getString(2);
                String imageURL = cursor.getString(3);
                Article article = new Article();
                article.setTitle(title);
                article.setDescription(desc);
                article.setUrl(url);
                article.setUrlToImage(imageURL);
                articles.add(article);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return articles;
    }

    /**
     * @param title title of an article
     * @param desc description of an article
     * @param url URL of an article
     * @param image_url URL of an article image
     * @return true if article is already saved, false otherwise
     * */
    public boolean alreadySaved(String title, String desc, String url, String image_url) {
        Cursor cursor = database.query(table_name,
                new String[]{article_title, article_description, article_url, article_image_url}
                , "(" + article_title
                        + " = ? AND " +
                        article_description + " = ? AND " +
                        article_url + " = ? AND " +
                        article_image_url + " = ?"
                        + ")", new String[]{title, desc, url, image_url},
                null, null, null);
        boolean saved = cursor.moveToFirst();
        cursor.close();
        return saved;
    }

    /**
     * @param title title of an article
     * @param desc description of an article
     * @param url URL of an article
     * @param image_url URL of an article image
     * @return -1 if news article is already saved
     * */
    public long saveNews(String title, String desc, String url, String image_url) {
        if (alreadySaved(title, desc, url, image_url)) {
            return -1;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(article_title, title);
            contentValues.put(article_description, desc);
            contentValues.put(article_url, url);
            contentValues.put(article_image_url, image_url);
            return database.insert(table_name, null, contentValues);
        }
    }

    /**
     * @param title title of an article
     * @param desc description of an article
     * @param url URL of an article
     * @param image_url URL of an article image
     * @return true if news article deleted otherwise false
     * */
    public boolean deleteNews(String title, String desc, String url, String image_url) {
        return database.delete(table_name,
                "(" + article_title
                        + " = ? AND " +
                        article_description + " = ? AND " +
                        article_url + " = ? AND " +
                        article_image_url + " = ?"
                        + ")", new String[]{title, desc, url, image_url}) > 0;
    }
}
