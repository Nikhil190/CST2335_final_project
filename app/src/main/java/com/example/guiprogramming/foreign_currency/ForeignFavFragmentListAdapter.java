package com.example.guiprogramming.foreign_currency;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.guiprogramming.R;

import java.util.ArrayList;

//Custom list adapter,
// used for showing favorites data from the database to a list
public class ForeignFavFragmentListAdapter extends BaseAdapter {

    static LayoutInflater inflater;
    ArrayList<CurrencyMap> data;
    ForeignFavFragmentListAdapter(Activity activity, ArrayList<CurrencyMap> data){
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        //checking if the layout already created or not
        if(convertView != null){
            viewHolder = (ViewHolder) convertView.getTag();
        }else{
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_foreign_fav, parent, false);
            viewHolder.textViewBaseSymbol = convertView.findViewById(R.id.textViewBaseSymbol);
            viewHolder.textViewForeignSymbol = convertView.findViewById(R.id.textViewForeignSymbol);
            convertView.setTag(viewHolder);
        }
        viewHolder.textViewBaseSymbol.setText(data.get(position).getBaseSymbol());
        viewHolder.textViewForeignSymbol.setText(data.get(position).getForeignSymbol());
        return convertView;
    }

    class ViewHolder{
        TextView textViewBaseSymbol, textViewForeignSymbol;
    }
}
