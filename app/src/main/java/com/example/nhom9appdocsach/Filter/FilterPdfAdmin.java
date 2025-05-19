package com.example.nhom9appdocsach.Filter;

import android.widget.Filter;

import com.example.nhom9appdocsach.Adapter.AdapterPdfAdmin;
import com.example.nhom9appdocsach.Model.Pdf;

import java.util.ArrayList;

public class FilterPdfAdmin extends Filter {
    ArrayList<Pdf> filterList;
    AdapterPdfAdmin adapterPdfAdmin;
    public FilterPdfAdmin(ArrayList<Pdf> filterList, AdapterPdfAdmin adapterPdfAdmin) {
        this.filterList = filterList;
        this.adapterPdfAdmin = adapterPdfAdmin;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        // Thực hiện tìm kiếm và trả về kết quả
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toUpperCase();
            ArrayList<Pdf> filterdModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).toString().toUpperCase().contains(constraint)) {
                    filterdModels.add(filterList.get(i));
                }
            }
            results.count = filterdModels.size();
            results.values = filterdModels;
        }else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterPdfAdmin.pdfArrayList = (ArrayList<Pdf>) results.values;
        adapterPdfAdmin.notifyDataSetChanged();
    }
}
