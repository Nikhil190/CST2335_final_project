package com.example.guiprogramming.news_api;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guiprogramming.R;
import com.example.guiprogramming.foreign_currency.ForeignMainActivity;
import com.example.guiprogramming.recipe_search_engine.RecipeMainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This activity shows detailed info
 * */
public class NewsDetailActivity extends AppCompatActivity implements View.OnClickListener {

    CoordinatorLayout coordinatorLayout;
    TextView textView3, textView4;
    ImageView imageView;
    Button button4, button5, button6;

    String title, description, url, imageURL;
    boolean isSaved;

    NewsDB database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        //receiving data from passed intent
        title = getIntent().getStringExtra("title");
        description = getIntent().getStringExtra("description");
        url = getIntent().getStringExtra("url");
        imageURL = getIntent().getStringExtra("imageURL");
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        imageView = findViewById(R.id.imageView);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        textView3.setText(title);
        textView4.setText(description);

        database = new NewsDB(NewsDetailActivity.this);
        database.openConnection();
        isSaved = database.alreadySaved(title, description, url, imageURL);
        database.closeConnection();

        // check if the data is already stored or not
        if(isSaved){
            button5.setVisibility(View.GONE);
        }else{
            button6.setVisibility(View.GONE);
        }

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening URL in web browser
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //insert new news article
                //showing appropriate snackbar to user
                database.openConnection();
                long insertion = database.saveNews(title, description, url, imageURL);
                if(insertion == -1){
                    //failed
                    String fail_msg = getResources().getString(R.string.news_failed_save);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, fail_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    //success
                    String success_msg = getResources().getString(R.string.news_success_save);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, success_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    button6.setVisibility(View.VISIBLE);
                    button5.setVisibility(View.GONE);
                }
                database.closeConnection();
            }
        });

        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete saved news article
                //showing appropriate snackbar to user
                database.openConnection();
                boolean deletion = database.deleteNews(title, description, url, imageURL);
                if(deletion){
                    //success
                    String success_msg = getResources().getString(R.string.news_success_delete);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, success_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    button5.setVisibility(View.VISIBLE);
                    button6.setVisibility(View.GONE);
                }else{
                    //failed
                    String fail_msg = getResources().getString(R.string.news_failed_delete);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, fail_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                database.closeConnection();
            }
        });

        new GetImage(imageURL).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button4:
                //Read article in browser
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.button5:
                //save article to local database
                isSaved = true;
                button6.setVisibility(View.VISIBLE);
                button5.setVisibility(View.GONE);
                break;
            case R.id.button6:
                //delete article from local database
                isSaved = false;
                button6.setVisibility(View.GONE);
                button5.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Loading image from URL
     * */
    class GetImage extends AsyncTask<Void, Void, Bitmap> {

        String imageURL;
        GetImage(String imageURL){
            this.imageURL = imageURL;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            Bitmap bitmap = null;
            URL url = null;
            try {
                url = new URL(imageURL);
                InputStream inputStream = url.openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //set bitmap to image view
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    //inflating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_menu, menu);
        return true;
    }

    //handling menu click events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_recipe:
                startActivity(new Intent(NewsDetailActivity.this, RecipeMainActivity.class));
                break;
            case R.id.menu_foreign:
                startActivity(new Intent(NewsDetailActivity.this, ForeignMainActivity.class));
                break;
            case R.id.menu_help:
                final Dialog dialog = new Dialog(NewsDetailActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_help_news);
                Button buttonOk = dialog.findViewById(R.id.buttonOk);
                buttonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);

                dialog.setCancelable(true);
                dialog.show();
                break;

        }
        return (super.onOptionsItemSelected(item));
    }
}
