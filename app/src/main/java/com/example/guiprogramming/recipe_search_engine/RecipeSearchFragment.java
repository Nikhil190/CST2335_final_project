package com.example.guiprogramming.recipe_search_engine;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.guiprogramming.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Search fragment, calls API with entered text and shows result in a listview
 * */
public class RecipeSearchFragment extends Fragment {

    Button buttonSearch;
    ListView listView3;
    EditText recipeQuery;
    View v;

    ArrayList<RecipeData> recipeDataList;
    RecipeListAadapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_recipe_search, null);
            buttonSearch = v.findViewById(R.id.buttonSearch);
            recipeQuery = v.findViewById(R.id.recipeQuery);
            listView3 = v.findViewById(R.id.listView3);

            recipeDataList = new ArrayList<>();
            adapter = new RecipeListAadapter(getActivity(), recipeDataList);
            listView3.setAdapter(adapter);

            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String recipe_title = recipeQuery.getText().toString();
                    if (recipe_title.trim().equals("")) {
                        String error = getActivity().getResources().getString(R.string.recipe_toast_error);
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    } else if (recipe_title.equalsIgnoreCase("Lasagna")) {
                        new GetRecipeList("https://torunski.ca/FinalProjectLasagna.json").execute();
                    } else if (recipe_title.equalsIgnoreCase("Chicken Breast")) {
                        new GetRecipeList("https://torunski.ca/FinalProjectChickenBreast.json").execute();
                    } else {
                        String error = "Recipe Not available!";
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }

                    listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(getActivity(), RecipeDetailActivity.class);
                            RecipeData recipeData = (RecipeData) parent.getAdapter().getItem(position);
                            intent.putExtra("publisher", recipeData.getPublisher());
                            intent.putExtra("f2f_url",recipeData.getF2f_url());
                            intent.putExtra("title",recipeData.getTitle());
                            intent.putExtra("source_url",recipeData.getSource_url());
                            intent.putExtra("recipe_id",recipeData.getRecipe_id());
                            intent.putExtra("social_rank",recipeData.getSocial_rank());
                            intent.putExtra("publisher_url",recipeData.getPublisher_url());
                            intent.putExtra("image_url",recipeData.getImage_url());
                            startActivity(intent);
                        }
                    });
                }
            });
        } else {
            ViewGroup vg = (ViewGroup) v.getParent();
            if (vg != null) {
                vg.removeView(v);
            }
        }
        return v;
    }

    /**
     * Calling API for getting recipe list according to entered text
     * */
    class GetRecipeList extends AsyncTask<Void, Void, String> {

        String url;
        ProgressDialog progressDialog;

        GetRecipeList(String url) {
            this.url = url;
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getResources().getString(R.string.recipe_please_wait));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            String recipes = "";
            try {
                //calling API
                URL mUrl = new URL(url);
                HttpURLConnection urlConnection = (HttpURLConnection) mUrl.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader streamReader = new
                        InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String inputLine;
                //reading input stream
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }
                reader.close();
                streamReader.close();
                //returning json response
                recipes = stringBuilder.toString();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return recipes;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
            if (!s.equals("")) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("recipes");
                    recipeDataList.clear();
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject recipe = data.getJSONObject(i);
                        String publisher = recipe.getString("publisher");
                        String f2f_url = recipe.getString("f2f_url");
                        String title = recipe.getString("title");
                        String source_url = recipe.getString("source_url");
                        String recipe_id = recipe.getString("recipe_id");
                        double social_rank = recipe.getDouble("social_rank");
                        String publisher_url = recipe.getString("publisher_url");
                        String image_url = recipe.getString("image_url");
                        image_url = image_url.replace("http","https");
                        RecipeData recipeData = new RecipeData(title, publisher, f2f_url, source_url, recipe_id,
                                image_url, publisher_url, social_rank);
                        recipeDataList.add(recipeData);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
