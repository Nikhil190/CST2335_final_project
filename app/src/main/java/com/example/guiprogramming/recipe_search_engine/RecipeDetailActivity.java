package com.example.guiprogramming.recipe_search_engine;

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
import com.example.guiprogramming.news_api.NewsMainActivity;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Shows detail of a recipe passed through INTENT,
 * either from API or local SQLite database
 * */
public class RecipeDetailActivity extends AppCompatActivity {

    ImageView imageView2;
    TextView textView8, textView9, textView10;
    CoordinatorLayout coordinatorLayout;

    Button button7, button8, button9, button10;

    String title, publisher, f2f_url,
            source_url, recipe_id,
            image_url, publisher_url;
    double social_rank;

    //SQLitedatabase interaction object
    RecipeDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        imageView2 = findViewById(R.id.imageView2);
        textView8 = findViewById(R.id.textView8);
        textView9 = findViewById(R.id.textView9);
        textView10 = findViewById(R.id.textView10);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        button10 = findViewById(R.id.button10);
        title = getIntent().getStringExtra("title");
        publisher = getIntent().getStringExtra("publisher");
        f2f_url = getIntent().getStringExtra("f2f_url");
        source_url = getIntent().getStringExtra("source_url");
        recipe_id = getIntent().getStringExtra("recipe_id");
        image_url = getIntent().getStringExtra("image_url");
        publisher_url = getIntent().getStringExtra("publisher_url");
        social_rank = getIntent().getDoubleExtra("social_rank", 0);
        textView8.setText(title);
        textView9.setText(publisher);
        String textSocialRank = String.valueOf(social_rank);
        textView10.setText(textSocialRank);

        new GetImage(image_url).execute();

        db = new RecipeDB(this);
        db.open();
        if (db.exists(recipe_id)) {
            button9.setVisibility(View.GONE);
            button10.setVisibility(View.VISIBLE);
        } else {
            button9.setVisibility(View.VISIBLE);
            button10.setVisibility(View.GONE);
        }

        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(source_url));
                startActivity(i);
            }
        });

        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(publisher_url));
                startActivity(i);
            }
        });

        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                if (db.addToDB(title, publisher,
                        f2f_url, source_url, recipe_id,
                        image_url, publisher_url,
                        social_rank) != -1){
                    button9.setVisibility(View.GONE);
                    button10.setVisibility(View.VISIBLE);
                    String success_msg = getResources().getString(R.string.recipe_snack_add_success);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, success_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    String success_msg = getResources().getString(R.string.recipe_snack_add_fail);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, success_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                db.close();
            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                if (db.removeFromDB(recipe_id)){
                    button9.setVisibility(View.VISIBLE);
                    button10.setVisibility(View.GONE);
                    String success_msg = getResources().getString(R.string.recipe_snack_rem_success);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, success_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else{
                    String success_msg = getResources().getString(R.string.recipe_snack_rem_fail);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, success_msg, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                db.close();
            }
        });
    }

    /**
     * Loading image in a background thread
     * */
    class GetImage extends AsyncTask<Void, Void, Bitmap> {

        String imageURL;

        GetImage(String imageURL) {
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
            if (bitmap != null) {
                imageView2.setImageBitmap(bitmap);
            }
        }
    }

    //inflating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    //handling menu click events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_foreign:
                startActivity(new Intent(this, ForeignMainActivity.class));
                break;
            case R.id.menu_news:
                startActivity(new Intent(this, NewsMainActivity.class));
                break;
            case R.id.menu_help:
                //Showing custom dialog
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_help_recipe);
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
