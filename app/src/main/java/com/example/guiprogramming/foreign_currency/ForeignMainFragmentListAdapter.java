package com.example.guiprogramming.foreign_currency;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.guiprogramming.R;

import java.util.ArrayList;

//custom listview adapterfor main listing fragment
public class ForeignMainFragmentListAdapter extends BaseAdapter {

    ArrayList<ForeignSymbols> data;
    static LayoutInflater inflater;

    ForeignMainFragmentListAdapter(Activity activity, ArrayList<ForeignSymbols> data){
        this.data = data;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder{
        TextView textForeignListSymbol, textForeignListRate;
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
        ViewHolder holder;
        //checking if row view is already created or not
        if(convertView != null){
            holder = (ViewHolder) convertView.getTag();
        }else{
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_foreign_main_base, parent, false);
            holder.textForeignListRate = convertView.findViewById(R.id.textForeignListRate);
            holder.textForeignListSymbol = convertView.findViewById(R.id.textForeignListSymbol);

            convertView.setTag(holder);
        }
        String rate = String.valueOf(data.get(position).getRate());
        holder.textForeignListRate.setText(rate);
        holder.textForeignListSymbol.setText(data.get(position).getSymbol());
        return convertView;
    }
}
