package com.example.nhom9appdocsach.Filter;

import android.widget.Filter;

import com.example.nhom9appdocsach.Adapter.AdapterCategory;
import com.example.nhom9appdocsach.Adapter.AdapterCategoryUser;
import com.example.nhom9appdocsach.Model.Category;

import java.util.ArrayList;

public class FilterCategoryUser extends Filter {
    ArrayList<Category> filterList;
    AdapterCategoryUser adapterCategoryUser;
    public FilterCategoryUser(ArrayList<Category> filterList, AdapterCategoryUser adapterCategoryUser) {
        this.filterList = filterList;
        this.adapterCategoryUser = adapterCategoryUser;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0){
            constraint = constraint.toString().toUpperCase();
            ArrayList<Category> filterdModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++){
                if (filterList.get(i).getCategory().toUpperCase().contains(constraint)){
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
        adapterCategoryUser.catagoryArrayList = (ArrayList<Category>) results.values;
        adapterCategoryUser.notifyDataSetChanged();
    }
}
