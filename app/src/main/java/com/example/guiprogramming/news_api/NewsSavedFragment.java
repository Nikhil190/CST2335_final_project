package com.example.guiprogramming.news_api;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.guiprogramming.R;

import java.util.ArrayList;

/**
 * Shows the list of articles saved in the database
 * and on list item click, it will show further details
 * */
public class NewsSavedFragment extends Fragment {

    View layout;
    ListView listView2;
    ArrayList<Article> articleArrayList;

    NewsDB database;
    NewsArticlesAdapter newsArticlesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //checking if fragment instance already exists or not
        if (layout != null) {
            ViewGroup layout_container = (ViewGroup) layout.getParent();
            if (layout_container != null) {
                layout_container.removeView(layout);
            }
        }else{
            layout = inflater.inflate(R.layout.fragment_news_saved, null);
            listView2 = layout.findViewById(R.id.listView2);
            database = new NewsDB(getActivity());
            //reading news articles from the database
            database.openConnection();
            articleArrayList = database.getAllNews();
            newsArticlesAdapter = new NewsArticlesAdapter(getActivity(), articleArrayList);
            database.closeConnection();

            listView2.setAdapter(newsArticlesAdapter);

            listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //sending data intent
                    Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                    Article article = (Article) parent.getAdapter().getItem(position);
                    intent.putExtra("title",article.getTitle());
                    intent.putExtra("description",article.getDescription());
                    intent.putExtra("url",article.getUrl());
                    intent.putExtra("imageURL",article.getUrlToImage());
                    startActivity(intent);
                }
            });
        }
        return layout;
    }
}
