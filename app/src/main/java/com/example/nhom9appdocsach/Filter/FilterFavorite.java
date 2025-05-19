package com.example.nhom9appdocsach.Filter;

import android.widget.Filter;

import com.example.nhom9appdocsach.Adapter.AdapterFavoriteBook;
import com.example.nhom9appdocsach.Model.Pdf;

import java.util.ArrayList;

public class FilterFavorite extends Filter {
    ArrayList<Pdf> filterList;
    AdapterFavoriteBook adapterFavoriteBook;
    public FilterFavorite(ArrayList<Pdf> filterList, AdapterFavoriteBook adapterFavoriteBook) {
        this.filterList = filterList;
        this.adapterFavoriteBook = adapterFavoriteBook;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Pdf> filterdModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++){
                if (filterList.get(i).getTitle().toUpperCase().contains(constraint)){
                    filterdModels.add(filterList.get(i));
                }
            }
            results.count = filterdModels.size();
            results.values = filterdModels;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterFavoriteBook.pdfArrayList = (ArrayList<Pdf>) results.values;
        adapterFavoriteBook.notifyDataSetChanged();
    }
}
