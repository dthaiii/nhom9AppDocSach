package com.example.nhom9appdocsach.Filter;

import android.widget.Filter;

import com.example.nhom9appdocsach.Adapter.AdapterCategory;
import com.example.nhom9appdocsach.Model.Category;

import java.util.ArrayList;

public class FilterCategory extends Filter {
//    danh sách gốc chứa tất cả thể loại sách (chưa lọc)
    ArrayList<Category> filterList;
//    Adapter sẽ cập nhật mỗi khi có sự thay đổi
    AdapterCategory adapterCategory;

    public FilterCategory(ArrayList<Category> filterList, AdapterCategory adapterCategory) {
        this.filterList = filterList;
        this.adapterCategory = adapterCategory;
    }

// xử lý dữ liệu lọc theo input của người dùng
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        if (constraint != null && constraint.length() > 0) {
            constraint = constraint.toString().toLowerCase();
            ArrayList<Category> filteredModels = new ArrayList<>();
            for (int i = 0; i < filterList.size(); i++) {
                if (filterList.get(i).getCategory().toLowerCase().contains(constraint)) {
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        } else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
//    gán danh sách kết quả lọc cho adapter, cập nhật lại giao diện RecylerView
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapterCategory.categoryArrayList = (ArrayList<Category>) results.values;
        adapterCategory.notifyDataSetChanged();

    }
}
