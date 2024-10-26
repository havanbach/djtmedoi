package com.example.djtmedoi.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.djtmedoi.Models.Product;
import com.example.djtmedoi.R;

import java.util.ArrayList;
import java.util.List;

public class AutoTextAdapter extends ArrayAdapter<Product> {
    private List<Product> mlistProduct;
    public AutoTextAdapter(@NonNull Context context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        mlistProduct = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_dong_auto_text, parent, false);
        }

        TextView tvcountryname = convertView.findViewById(R.id.textViewCountryName);
        Product product = getItem(position);
        tvcountryname.setText(product.getTensp());
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Product> mListSuggest = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0){
                    mListSuggest.addAll(mlistProduct);
                } else {
                    String filter = charSequence.toString().toLowerCase().trim();
                    for (Product product: mlistProduct){
                        if (product.getTensp().toLowerCase().contains(filter)){
                            mListSuggest.add(product);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListSuggest;
                filterResults.count = mListSuggest.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clear();
                addAll((List<Product>) filterResults.values);
                notifyDataSetInvalidated();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((Product) resultValue).getTensp();
            }
        };
    }
}
