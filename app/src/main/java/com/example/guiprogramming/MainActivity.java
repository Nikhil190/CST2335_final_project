package com.example.guiprogramming;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.guiprogramming.foreign_currency.ForeignMainActivity;
import com.example.guiprogramming.news_api.NewsMainActivity;
import com.example.guiprogramming.recipe_search_engine.RecipeMainActivity;

public class MainActivity extends AppCompatActivity {

    Button btnRecipe, btnForeignCurrency, btnNewsAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRecipe = findViewById(R.id.btnRecipe);
        btnRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecipeMainActivity.class);
                startActivity(intent);
            }
        });
        btnForeignCurrency = findViewById(R.id.btnForeignCurrency);
        btnForeignCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForeignMainActivity.class);
                startActivity(intent);
            }
        });
        btnNewsAPI = findViewById(R.id.btnNewsAPI);
        btnNewsAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewsMainActivity.class);
                startActivity(intent);
            }
        });
    }
}
