package com.example.guiprogramming.recipe_search_engine;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.guiprogramming.R;
import com.example.guiprogramming.foreign_currency.ForeignMainActivity;
import com.example.guiprogramming.news_api.NewsMainActivity;

/**
 * Contains two fragments
 * */
public class RecipeMainActivity extends AppCompatActivity {
    
    Button buttonSearch, buttonFavourites;
    FrameLayout recipe_container;

    Fragment f;
    RecipeSearchFragment f1;
    FragmentManager fm;
    FragmentTransaction ft;
    String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_main);

        buttonSearch = findViewById(R.id.buttonSearch);
        buttonFavourites = findViewById(R.id.buttonFavourites);
        recipe_container = findViewById(R.id.recipe_container);
        f1 = new RecipeSearchFragment();
        fm = getSupportFragmentManager();
        replacesearchfragment();
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replacesearchfragment();
            }
        });
        buttonFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replacefavouritesfragment();
            }
        });
    }

    /**
     * Showing search fragment
     * */
    void replacesearchfragment(){
        ft = fm.beginTransaction();
        f = f1;
        tag = f1.getClass().getName();
        if (f.isAdded()) {
            ft.show(f);
        } else {
            ft.replace(R.id.recipe_container, f, tag);
        }
        ft.commit();
    }

    /**
     * showing favourites fragment
     * */
    void replacefavouritesfragment(){
        ft = fm.beginTransaction();
        f = new RecipeFavFragment();
        tag = f1.getClass().getName();
        if (f.isAdded()) {
            ft.show(f);
        } else {
            ft.replace(R.id.recipe_container, f, tag);
        }
        ft.commit();
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
