package com.example.guiprogramming.foreign_currency;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.guiprogramming.R;

import java.util.ArrayList;

//Favourites fragment
public class ForeignFavFragment extends Fragment {

    Button buttonForFragFilter, buttonForFragClear;
    EditText editTextForFavFilter;
    ListView listViewFavouriteList;

    View layout;

    ArrayList<CurrencyMap> favouritesData;
    FavouritesDB db;
    ForeignFavFragmentListAdapter favListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //checking if fragment instance already exists or not
        if (layout == null) {
            db = new FavouritesDB(getActivity());
            layout = inflater.inflate(R.layout.fragment_foreign_fav, null);
            buttonForFragFilter = layout.findViewById(R.id.buttonForFragFilter);
            buttonForFragClear = layout.findViewById(R.id.buttonForFragClear);
            editTextForFavFilter = layout.findViewById(R.id.editTextForFavFilter);
            listViewFavouriteList = layout.findViewById(R.id.listViewFavouriteList);
            buttonForFragFilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String filterText = editTextForFavFilter.getText().toString().trim().toUpperCase();
                    ArrayList<CurrencyMap> filterData = new ArrayList<>();
                    for (int i = 0; i < favouritesData.size(); i++) {
                        if (favouritesData.get(i).getBaseSymbol().contains(filterText) || favouritesData.get(i).getForeignSymbol().contains(filterText)) {
                            filterData.add(favouritesData.get(i));
                        }
                    }
                    listViewFavouriteList.setAdapter(new ForeignFavFragmentListAdapter(getActivity(), filterData));
                }
            });

            buttonForFragClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextForFavFilter.setText("");
                    listViewFavouriteList.setAdapter(favListAdapter);
                }
            });

            //getting favourites value from database
            db.openDB();
            favouritesData = db.getAllFavourites();
            favListAdapter = new ForeignFavFragmentListAdapter(getActivity(), favouritesData);
            listViewFavouriteList.setAdapter(favListAdapter);
            db.closeDB();

            listViewFavouriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CurrencyMap data = (CurrencyMap) parent.getAdapter().getItem(position);
                    Intent intent = new Intent(getActivity(), ForeignConversionActivity.class);
                    intent.putExtra("BASE", data.getBaseSymbol());
                    CurrencyMap sym = (CurrencyMap) parent.getAdapter().getItem(position);
                    intent.putExtra("FOREIGN_SYM", sym.getForeignSymbol());
                    intent.putExtra("BASE_POSITION",data.getSelectedPosition());
                    getActivity().startActivity(intent);
                }
            });
        }else{
            ViewGroup layout_container = (ViewGroup) layout.getParent();
            if (layout_container != null) {
                layout_container.removeView(layout);
            }
        }
        return layout;
    }
}
