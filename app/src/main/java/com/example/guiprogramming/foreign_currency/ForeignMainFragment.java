package com.example.guiprogramming.foreign_currency;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.guiprogramming.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

//Main listing fragment
public class ForeignMainFragment extends Fragment {

    Spinner spinnerBaseCurrency;
    ListView listForeignSymbols;
    ForeignMainFragmentListAdapter listAdapter;
    ArrayList<ForeignSymbols> listData;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList<String> spinnerData;
    EditText editTextFilter;
    Button buttonFilter, buttonClearFilter;
    View layout;

    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //checking if fragment instance already exists or not
        if (layout == null) {
            sharedPreferences = getActivity().getSharedPreferences("FOREIGN_CURR_PREF", Context.MODE_PRIVATE);

            layout = inflater.inflate(R.layout.fragment_foreign_main, null);
            spinnerBaseCurrency = layout.findViewById(R.id.spinnerBaseCurrency);
            listForeignSymbols = layout.findViewById(R.id.listForeignSymbols);
            editTextFilter = layout.findViewById(R.id.editTextFilter);
            buttonFilter = layout.findViewById(R.id.buttonFilter);
            buttonClearFilter = layout.findViewById(R.id.buttonClearFilter);

            listData = new ArrayList<>();
            listAdapter = new ForeignMainFragmentListAdapter(getActivity(), listData);
            spinnerData = new ArrayList<>();
            spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, spinnerData);
            spinnerBaseCurrency.setAdapter(spinnerAdapter);
            listForeignSymbols.setAdapter(listAdapter);
            new GetAllCurrencySymbols().execute();

            spinnerBaseCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        //get items for list
                        editTextFilter.setText("");
                        listForeignSymbols.setAdapter(listAdapter);
                        new GetSymbolRates(spinnerData.get(position)).execute();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            buttonFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filterText = editTextFilter.getText().toString().trim().toUpperCase();
                    ArrayList<ForeignSymbols> filterData = new ArrayList<>();
                    for (int i = 0; i < listData.size(); i++) {
                        if (listData.get(i).getSymbol().contains(filterText)) {
                            filterData.add(listData.get(i));
                        }
                    }
                    listForeignSymbols.setAdapter(new ForeignMainFragmentListAdapter(getActivity(), filterData));
                }
            });

            buttonClearFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextFilter.setText("");
                    listForeignSymbols.setAdapter(listAdapter);
                }
            });

            listForeignSymbols.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int spinnerPosition = spinnerBaseCurrency.getSelectedItemPosition();
                    Intent intent = new Intent(getActivity(), ForeignConversionActivity.class);
                    intent.putExtra("BASE", spinnerData.get(spinnerPosition));
                    ForeignSymbols sym = (ForeignSymbols) parent.getAdapter().getItem(position);
                    intent.putExtra("FOREIGN_SYM", sym.getSymbol());
                    intent.putExtra("BASE_POSITION",spinnerPosition);
                    getActivity().startActivity(intent);
                }
            });

        } else {
            ViewGroup layout_container = (ViewGroup) layout.getParent();
            if (layout_container != null) {
                layout_container.removeView(layout);
            }
        }
        return layout;
    }

    /**
     * Background AsyncTask - makes an HTTP call, for getting all currency symbols
     * rate of particular currencies
     * */
    class GetAllCurrencySymbols extends AsyncTask<Void, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showing progress
            progressDialog = new ProgressDialog(getActivity());
            String waitString = getActivity().getResources().getString(R.string.wait);
            progressDialog.setMessage(waitString);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String json = "";
            String serverURL = "https://api.exchangeratesapi.io/latest";
            //making http call
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
            if (s != "") {
                //parsing json and setting up data for spinner
                spinnerData.clear();
                String firstItem = getResources().getString(R.string.foreign_str_spinner_item);
                spinnerData.add(firstItem);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject rates = jsonObject.getJSONObject("rates");

                    String base = jsonObject.getString("base");
                    spinnerData.add(base);

                    Iterator<String> rateKeys = rates.keys();
                    while (rateKeys.hasNext()) {
                        String rate = rateKeys.next();
                        spinnerData.add(rate);
                    }
                    spinnerAdapter.notifyDataSetChanged();
                    int selection = sharedPreferences.getInt("BASE_POSITION", 1);
                    spinnerBaseCurrency.setSelection(selection);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String errorMessage = getResources().getString(R.string.foreign_str_toast_error_while);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Background AsyncTask - makes an HTTP call, for getting all other currency symbol
     * list with rate
     * */
    class GetSymbolRates extends AsyncTask<Void, Void, String> {

        String baseCurrency;
        ProgressDialog progressDialog;

        GetSymbolRates(String baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showing progress
            progressDialog = new ProgressDialog(getActivity());
            String waitString = getActivity().getResources().getString(R.string.wait);
            progressDialog.setMessage(waitString);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String json = "";
            String serverURL = "https://api.exchangeratesapi.io/latest?base=" + baseCurrency;
            //making http call
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
            if (s != "") {
                //parsing json and setting up UI for listView
                listData.clear();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject rates = jsonObject.getJSONObject("rates");

                    Iterator<String> rateKeys = rates.keys();
                    while (rateKeys.hasNext()) {
                        String strSymbol = rateKeys.next();
                        double rate = rates.getDouble(strSymbol);
                        ForeignSymbols symbol = new ForeignSymbols(strSymbol, rate);
                        listData.add(symbol);
                    }
                    listAdapter.notifyDataSetChanged();
                    listForeignSymbols.setAdapter(listAdapter);
                    String searchText = sharedPreferences.getString("FOREIGN_SYM", "");
                    int spinnerPos = sharedPreferences.getInt("BASE_POSITION",0);
                    if (!searchText.equals("")
                            && spinnerPos == spinnerBaseCurrency.getSelectedItemPosition()) {
                        editTextFilter.setText(searchText);
                        String filterText = editTextFilter.getText().toString().trim().toUpperCase();
                        ArrayList<ForeignSymbols> filterData = new ArrayList<>();
                        for (int i = 0; i < listData.size(); i++) {
                            if(listData.get(i).getSymbol().contains(filterText)){
                                filterData.add(listData.get(i));
                            }
                        }
                        listForeignSymbols.setAdapter(new ForeignMainFragmentListAdapter(getActivity(), filterData));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.foreign_str_rates_error), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
