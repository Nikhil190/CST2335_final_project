package com.example.guiprogramming.foreign_currency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guiprogramming.R;
import com.example.guiprogramming.news_api.NewsMainActivity;
import com.example.guiprogramming.recipe_search_engine.RecipeMainActivity;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

//This activity converts base currency value to foreign currency value
public class ForeignConversionActivity extends AppCompatActivity {

    //default foreign currency rate
    double foreignRate = 1;
    String foreignSymbol;
    String baseSymbol;
    int spinnerPosition;

    CoordinatorLayout coordinatorLayout;
    Button buttonConvert;
    ImageButton buttonSwapUnits;
    EditText editTextAmount;
    TextView textBaseUnit, textForeignUnit,
            textAnswerBaseUnit, textAnswerForeignUnit,
            textAnswerBaseAmount, textAnswerConvAmount;

    //for checking if the foreign and base symbols are swapped
    boolean isSwapped;

    SharedPreferences sharedPreferences;
    String lastSearchedAmount, spBaseSymbol, spForeignSymbol;
    boolean isFromLastSearched;

    FavouritesDB db;
    //for checking if the value already exists in database or not
    boolean isAddedInDatabase;

    Button buttonAddToFavourites, buttonRemoveFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foreign_conversion);
        sharedPreferences = getSharedPreferences("FOREIGN_CURR_PREF", MODE_PRIVATE);
        lastSearchedAmount = sharedPreferences.getString("LAST_CONV_AMOUNT", "0");
        spBaseSymbol = sharedPreferences.getString("BASE_SYM", "0");
        spForeignSymbol = sharedPreferences.getString("FOREIGN_SYM", "0");

        baseSymbol = getIntent().getStringExtra("BASE");
        foreignSymbol = getIntent().getStringExtra("FOREIGN_SYM");
        spinnerPosition = getIntent().getIntExtra("BASE_POSITION",1);

        if (spBaseSymbol.equals(baseSymbol)
                && spForeignSymbol.equals(foreignSymbol)) {
            isFromLastSearched = true;
            isSwapped = sharedPreferences.getBoolean("isSwapped", false);
        } else {
            isFromLastSearched = false;
        }

        new GetSymbolRates(baseSymbol, foreignSymbol).execute();

        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        editTextAmount = findViewById(R.id.editTextAmount);
        textForeignUnit = findViewById(R.id.textForeignUnit);
        textAnswerBaseUnit = findViewById(R.id.textAnswerBaseUnit);
        textAnswerForeignUnit = findViewById(R.id.textAnswerForeignUnit);
        textAnswerBaseAmount = findViewById(R.id.textAnswerBaseAmount);
        textAnswerConvAmount = findViewById(R.id.textAnswerConvAmount);
        textBaseUnit = findViewById(R.id.textBaseUnit);
        buttonSwapUnits = findViewById(R.id.buttonSwapUnits);
        buttonConvert = findViewById(R.id.buttonConvert);
        buttonAddToFavourites = findViewById(R.id.buttonAddToFavourites);
        buttonRemoveFavourite = findViewById(R.id.buttonRemoveFavourite);

        db = new FavouritesDB(this);
        db.openDB();
        if (db.isDuplicate(baseSymbol, foreignSymbol)) {
            isAddedInDatabase = true;
            buttonAddToFavourites.setVisibility(View.GONE);
        } else {
            isAddedInDatabase = false;
            buttonRemoveFavourite.setVisibility(View.GONE);
        }
        db.closeDB();

        buttonSwapUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwapped) {
                    textBaseUnit.setText(baseSymbol);
                    textForeignUnit.setText(foreignSymbol);
                } else {
                    textBaseUnit.setText(foreignSymbol);
                    textForeignUnit.setText(baseSymbol);
                }
                isSwapped = !isSwapped;
            }
        });

        buttonConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strAmount = editTextAmount.getText().toString();
                if (strAmount.isEmpty()) {
                    Toast.makeText(ForeignConversionActivity.this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                } else {
                    double amount = Double.parseDouble(strAmount);
                    double answer = 0.0;
                    if (isSwapped) {
                        answer = amount / foreignRate;
                    } else {
                        answer = amount * foreignRate;
                    }
                    textAnswerBaseAmount.setText(strAmount);
                    String strAnswer = String.valueOf(answer);
                    textAnswerConvAmount.setText(strAnswer);
                    sharedPreferences.edit().putInt("BASE_POSITION", spinnerPosition)
                            .putString("BASE_SYM", baseSymbol)
                            .putString("FOREIGN_SYM", foreignSymbol)
                            .putString("LAST_CONV_AMOUNT", strAmount)
                            .putBoolean("isSwapped", isSwapped)
                            .apply();
                }
            }
        });

        buttonAddToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.openDB();
                long inserted = db.addToFavorite(baseSymbol, foreignSymbol, spinnerPosition);
                db.closeDB();

                if (inserted != -1) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getResources().getString(R.string.foreign_str_snack_added), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    buttonRemoveFavourite.setVisibility(View.VISIBLE);
                    buttonAddToFavourites.setVisibility(View.GONE);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getResources().getString(R.string.foreign_str_snack_add_fail), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

        buttonRemoveFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.openDB();
                int favID = db.getFavourite(baseSymbol, foreignSymbol).getId();
                boolean removed = db.removeFavourite(String.valueOf(favID));
                db.closeDB();

                if (removed) {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getResources().getString(R.string.foreign_str_removed_snack), Snackbar.LENGTH_LONG);
                    snackbar.show();
                    buttonRemoveFavourite.setVisibility(View.GONE);
                    buttonAddToFavourites.setVisibility(View.VISIBLE);
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, getResources().getString(R.string.foreign_str_fail_remove_snack), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    /**
     * Background AsyncTask - makes an HTTP call, for getting latest conversion
     * rate of particular currencies
     * */
    class GetSymbolRates extends AsyncTask<Void, Void, String> {

        String baseSymbol, foreignSymbol;

        ProgressDialog progressDialog;

        GetSymbolRates(String baseSymbol, String foreignSymbol) {
            this.baseSymbol = baseSymbol;
            this.foreignSymbol = foreignSymbol;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showing progress
            progressDialog = new ProgressDialog(ForeignConversionActivity.this);
            String waitString = getResources().getString(R.string.wait);
            progressDialog.setMessage(waitString);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //making http call
            String json = "";
            String serverURL = "https://api.exchangeratesapi.io/latest?base="
                    + baseSymbol + "&symbols=" + foreignSymbol;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new
                        InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                json = stringBuilder.toString();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(progressDialog != null){
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }
            //parsing json and setting up UI
            if (s != "") {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject rates = jsonObject.getJSONObject("rates");

                    Iterator<String> rateKeys = rates.keys();
                    while (rateKeys.hasNext()) {
                        String strSymbol = rateKeys.next();
                        foreignRate = rates.getDouble(strSymbol);
                    }

                    if (!isFromLastSearched) {
                        textBaseUnit.setText(baseSymbol);
                        textForeignUnit.setText(foreignSymbol);
                        textAnswerBaseUnit.setText(baseSymbol);
                        textAnswerForeignUnit.setText(foreignSymbol);
                        String strRate = String.valueOf(foreignRate);
                        textAnswerConvAmount.setText(strRate);
                    } else {
                        editTextAmount.setText(lastSearchedAmount);
                        if (!isSwapped) {
                            textBaseUnit.setText(baseSymbol);
                            textForeignUnit.setText(foreignSymbol);
                            textAnswerBaseUnit.setText(baseSymbol);
                            textAnswerForeignUnit.setText(foreignSymbol);
                        } else {
                            textBaseUnit.setText(foreignSymbol);
                            textForeignUnit.setText(baseSymbol);
                            textAnswerBaseUnit.setText(foreignSymbol);
                            textAnswerForeignUnit.setText(baseSymbol);
                        }
                        double amount = Double.parseDouble(lastSearchedAmount);
                        double answer;
                        if (isSwapped) {
                            answer = amount / foreignRate;
                        } else {
                            answer = amount * foreignRate;
                        }
                        textAnswerBaseAmount.setText(lastSearchedAmount);
                        String strAnswer = String.valueOf(answer);
                        textAnswerConvAmount.setText(strAnswer);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(ForeignConversionActivity.this, "Error while getting rates", Toast.LENGTH_SHORT).show();
            }
        }
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
                startActivity(new Intent(ForeignConversionActivity.this, RecipeMainActivity.class));
                break;
            case R.id.menu_news:
                startActivity(new Intent(ForeignConversionActivity.this, NewsMainActivity.class));
                break;
            case R.id.menu_help:
                //show custom dialog
                final Dialog dialog = new Dialog(ForeignConversionActivity.this);
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
