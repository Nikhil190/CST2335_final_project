package com.example.guiprogramming.recipe_search_engine;

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
 * List adapter, used to show data from API, or local database to a listview
 * */
public class RecipeListAadapter extends BaseAdapter {

    ArrayList<RecipeData> recipeData;
    static LayoutInflater inflater;

    RecipeListAadapter(Activity activity, ArrayList<RecipeData> recipeData) {
        this.recipeData = recipeData;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return recipeData.size();
    }

    @Override
    public Object getItem(int position) {
        return recipeData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView v;
        if (convertView == null) {
            v = new ItemView();
            convertView = inflater.inflate(R.layout.list_recipe_search, null);
            v.textView5 = convertView.findViewById(R.id.textView5);
            v.textView7 = convertView.findViewById(R.id.textView7);
            convertView.setTag(v);
        } else {
            v = (ItemView) convertView.getTag();
        }
        RecipeData recipeData = (RecipeData) getItem(position);
        v.textView5.setText(recipeData.getTitle());
        v.textView7.setText(recipeData.getPublisher());
        return convertView;
    }

    class ItemView {
        TextView textView5, textView7;
    }
}
