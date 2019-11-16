package com.example.guiprogramming.news_api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * This fragment works as a search fragment,
 * retrieves all the articles related to entered topic.
 */
public class NewsMainFragment extends Fragment {

    View layout;
    EditText editText;
    Button button3;
    ListView listView;

    ArrayList<Article> articleArrayList;
    NewsArticlesAdapter newsArticlesAdapter;

    SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //checking if fragment instance already exists or not
        if (layout != null) {
            ViewGroup layout_container = (ViewGroup) layout.getParent();
            if (layout_container != null) {
                layout_container.removeView(layout);
            }
        } else {
            sharedPreferences = getActivity().getSharedPreferences("news_shared_prefs", Context.MODE_PRIVATE);
            layout = inflater.inflate(R.layout.fragment_news_main, null);
            editText = layout.findViewById(R.id.editText);
            button3 = layout.findViewById(R.id.button3);
            listView = layout.findViewById(R.id.listView);

            articleArrayList = new ArrayList<>();
            newsArticlesAdapter = new NewsArticlesAdapter(getActivity(), articleArrayList);
            listView.setAdapter(newsArticlesAdapter);

            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String query = editText.getText().toString().trim();
                    if (query.length() != 0) {
                        //calling api through async task
                        new GetNews(query).execute();
                    }else{
                        //showing toast
                        String errorMessage = getActivity().getResources().getString(R.string.news_error_toast);
                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //opening detailed activity
                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                    Article article = (Article) parent.getAdapter().getItem(position);
                    intent.putExtra("title", article.getTitle());
                    intent.putExtra("description", article.getDescription());
                    intent.putExtra("url", article.getUrl());
                    intent.putExtra("imageURL", article.getUrlToImage());
                    startActivity(intent);
                }
            });

            String lastSearched = sharedPreferences.getString("last_searched","");
            if(!lastSearched.isEmpty()){
                editText.setText(lastSearched);
                button3.performClick();
            }
        }
        return layout;
    }

    /***
     * Async task for fetching news articles from API realted to topic
     * */
    class GetNews extends AsyncTask<Void, Void, String> {

        String query;
        ProgressDialog progressDialog;

        /***
         * @param query topic be searched
         * */
        GetNews(String query) {
            this.query = query;
            progressDialog = new ProgressDialog(getActivity());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            String wait = getActivity().getResources().getString(R.string.news_wait);
            progressDialog.setMessage(wait);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (s.equals("")) {
            } else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray articles = jsonObject.getJSONArray("articles");
                    for (int i = 0; i < articles.length(); i++) {
                        JSONObject jo = articles.getJSONObject(i);
                        String title = jo.getString("title");
                        String description = jo.getString("description");
                        String urlToImage = jo.getString("urlToImage");
                        String url = jo.getString("url");

                        Article article = new Article();
                        article.setTitle(title);
                        article.setDescription(description);
                        article.setUrl(url);
                        article.setUrlToImage(urlToImage);
                        articleArrayList.add(article);
                    }
                    newsArticlesAdapter.notifyDataSetChanged();

                    //storing data to sharedpreferences for later viewing
                    sharedPreferences.edit()
                            .putString("last_searched", query)
                            .apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            String response = "";
            try {
                //calling API
                URL url = new URL("https://newsapi.org/v2/everything?apiKey=48bc93ef342f405b94068e036bed71a0&q=" + URLEncoder.encode(query, "UTF-8"));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
                response = stringBuilder.toString();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
            return response;
        }
    }
}
