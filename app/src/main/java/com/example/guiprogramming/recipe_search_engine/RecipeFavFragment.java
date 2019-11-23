package com.example.guiprogramming.recipe_search_engine;

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
 * Shows the list of saved recipes in the SQLitedatabase
 * */
public class RecipeFavFragment extends Fragment {

    View v;
    ListView favouritesList;
    ArrayList<RecipeData> data;
    RecipeListAadapter adapter;
    RecipeDB db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(v == null){
            v = inflater.inflate(R.layout.fragment_recipe_favourites, null);
            favouritesList = v.findViewById(R.id.favouritesList);
            db = new RecipeDB(getActivity());
            db.open();
            data = db.getAllRecipe();
            db.close();
            adapter = new RecipeListAadapter(getActivity(), data);
            favouritesList.setAdapter(adapter);
            favouritesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        }else{
            ViewGroup vg = (ViewGroup) v.getParent();
            if (vg != null) {
                vg.removeView(v);
            }
        }
        return v;
    }
}
