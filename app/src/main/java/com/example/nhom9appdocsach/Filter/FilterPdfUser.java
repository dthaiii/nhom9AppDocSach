package com.example.nhom9appdocsach.Filter;

import android.widget.Filter;

import com.example.nhom9appdocsach.Adapter.AdapterPdfUser;
import com.example.nhom9appdocsach.Model.Pdf;

import java.util.ArrayList;

public class FilterPdfUser extends Filter {
    ArrayList<Pdf> filterList;
    AdapterPdfUser adapterPdfUser;
    public FilterPdfUser(ArrayList<Pdf> filterList, AdapterPdfUser adapterPdfUser) {
        this.filterList = filterList;
        this.adapterPdfUser = adapterPdfUser;
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
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPdfUser.pdfArrayList = (ArrayList<Pdf>) results.values;
        adapterPdfUser.notifyDataSetChanged();
    }
}
