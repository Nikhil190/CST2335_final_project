package com.example.guiprogramming.news_api;

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
import com.example.guiprogramming.foreign_currency.ForeignMainActivity;
import com.example.guiprogramming.recipe_search_engine.RecipeMainActivity;

//News API first activity
public class NewsMainActivity extends AppCompatActivity {

    Button button, button2;

    Fragment fragment;
    NewsMainFragment mainFragment;
    FragmentTransaction fragmentTransaction;
    FragmentManager fragmentManager;

    String fragmentName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_main);

        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        mainFragment = new NewsMainFragment();
        fragmentManager = getSupportFragmentManager();

        //showing search fragment
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = mainFragment;
        fragmentName = mainFragment.getClass().getName();
        if (fragment.isAdded()) {
            fragmentTransaction.show(fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment, fragmentName);
        }
        fragmentTransaction.commit();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change fragment to search fragment
                fragmentTransaction = fragmentManager.beginTransaction();
                fragment = mainFragment;
                fragmentName = mainFragment.getClass().getName();
                if (fragment.isAdded()) {
                    fragmentTransaction.show(fragment);
                } else {
                    fragmentTransaction.replace(R.id.frameLayout, fragment, fragmentName);
                }
                fragmentTransaction.commit();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //change fragment to Saved fragment
                fragmentTransaction = fragmentManager.beginTransaction();
                fragment = new NewsSavedFragment();
                fragmentName = fragment.getClass().getName();
                if (fragment.isAdded()) {
                    fragmentTransaction.show(fragment);
                } else {
                    fragmentTransaction.replace(R.id.frameLayout, fragment, fragmentName);
                }
                fragmentTransaction.commit();
            }
        });
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
                startActivity(new Intent(NewsMainActivity.this, RecipeMainActivity.class));
                break;
            case R.id.menu_foreign:
                startActivity(new Intent(NewsMainActivity.this, ForeignMainActivity.class));
                break;
            case R.id.menu_help:
                //Showing custom dialog
                final Dialog dialog = new Dialog(NewsMainActivity.this);
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
