package com.example.guiprogramming.news_api;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.guiprogramming.R;

import java.util.ArrayList;

/**
 * Custom list view adapter
 * */
public class NewsArticlesAdapter extends BaseAdapter {

    static LayoutInflater inflater;
    ArrayList<Article> articleArrayList;
    NewsArticlesAdapter(Activity activity, ArrayList<Article> articleArrayList){
        this.articleArrayList = articleArrayList;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @return number of elements stored in arraylist
     * */
    @Override
    public int getCount() {
        return articleArrayList.size();
    }

    /**
     * @param position position of an item from a list
     * @return item stored at particular position of an arraylist
     * */
    @Override
    public Object getItem(int position) {
        return articleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_news_article, null);
            holder.textView2 = convertView.findViewById(R.id.textView2);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        Article article = articleArrayList.get(position);
        holder.textView2.setText(article.getTitle());
        return convertView;
    }

    class Holder{
        TextView textView2;
    }
}
