package com.example.guiprogramming.foreign_currency;

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

import com.example.guiprogramming.R;
import com.example.guiprogramming.news_api.NewsMainActivity;
import com.example.guiprogramming.recipe_search_engine.RecipeMainActivity;

public class ForeignMainActivity extends AppCompatActivity {

    Fragment fragment;
    ForeignMainFragment mainFragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    String fragmentName = "";

    Button buttonForeignHome, buttonForeignFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_main);

        buttonForeignHome = findViewById(R.id.buttonForeignHome);
        buttonForeignFavorites = findViewById(R.id.buttonForeignFavorites);

        mainFragment = new ForeignMainFragment();
        fragmentManager = getSupportFragmentManager();

        initializeViews();

        buttonForeignFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new ForeignFavFragment();
                fragmentName = mainFragment.getClass().getName();
                if (fragment.isAdded()) {
                    fragmentTransaction.show(fragment);
                } else {
                    fragmentTransaction.replace(R.id.container, fragment, fragmentName);
                }
                fragmentTransaction.commit();
            }
        });

        buttonForeignHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeViews();
            }
        });
    }

    //initialize first fragment
    void initializeViews() {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = mainFragment;
        fragmentName = mainFragment.getClass().getName();
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.replace(R.id.container, fragment, fragmentName);
        }
        fragmentTransaction.commit();
    }

    //inflating menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.foreign_menu, menu);
        return true;
    }

    //handling menu click events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_recipe:
                startActivity(new Intent(ForeignMainActivity.this, RecipeMainActivity.class));
                break;
            case R.id.menu_news:
                startActivity(new Intent(ForeignMainActivity.this, NewsMainActivity.class));
                break;
            case R.id.menu_help:
                final Dialog dialog = new Dialog(ForeignMainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_about_foreign);
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
