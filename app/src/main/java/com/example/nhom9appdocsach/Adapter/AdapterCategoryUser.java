package com.example.nhom9appdocsach.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhom9appdocsach.Activities.PdfListUserActivity;
import com.example.nhom9appdocsach.Filter.FilterCategoryUser;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.databinding.RowCategoryUserBinding;

import java.util.ArrayList;

public class AdapterCategoryUser extends RecyclerView.Adapter<AdapterCategoryUser.HolderCategory> implements Filterable {

    private Context context;
    public ArrayList<Category> catagoryArrayList , filterList;
    private RowCategoryUserBinding binding;

    private FilterCategoryUser filterCategoryUser;

    public AdapterCategoryUser(Context context, ArrayList<Category> catagoryArrayList) {
        this.context = context;
        this.catagoryArrayList = catagoryArrayList;
        this.filterList = catagoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCategoryUserBinding.inflate(LayoutInflater.from(context),parent,false);

        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        Category modelCatagory = catagoryArrayList.get(position);
        String id = modelCatagory.getId();
        String category = modelCatagory.getCategory();

        holder.edttentl.setText(category);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfListUserActivity.class);
                intent.putExtra("categoryId",id);
                intent.putExtra("categoryTitle",category);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catagoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterCategoryUser == null){
            filterCategoryUser = new FilterCategoryUser(filterList,this);
        }
        return filterCategoryUser;
    }

    class HolderCategory extends RecyclerView.ViewHolder{
        TextView edttentl;
        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            edttentl = binding.edttentl;
        }
    }
}