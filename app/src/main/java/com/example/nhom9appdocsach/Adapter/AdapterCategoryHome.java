package com.example.nhom9appdocsach.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhom9appdocsach.Activities.PdfListUserActivity;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.Model.ListPdf;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.ItemListHomeBinding;
import com.example.nhom9appdocsach.Database.DatabaseHandel; // Thêm dòng này

import java.util.ArrayList;

public class AdapterCategoryHome extends RecyclerView.Adapter<AdapterCategoryHome.ViewHolder> {

    public Context context;
    public ArrayList<Category> categoryArrayList;

    public AdapterCategoryHome(Context context, ArrayList<Category> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category modelCategory = categoryArrayList.get(position);
        String id = modelCategory.getId();
        String category = modelCategory.getCategory();

        holder.binding.txttheloai.setText(category);

        holder.binding.txtmore.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfListUserActivity.class);
            intent.putExtra("categoryId", id);
            intent.putExtra("categoryTitle", category);
            context.startActivity(intent);
        });

        loadBooksInCategory(id, holder.binding.theloaiRCV);
    }

    private void loadBooksInCategory(String categoryId, RecyclerView recyclerView) {
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        ArrayList<ListPdf> booksList = dbHelper.getBooksByCategoryId(categoryId);

        AdapterPdfListUser bookAdapter = new AdapterPdfListUser(context, booksList);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        recyclerView.setAdapter(bookAdapter);
    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    // Thêm phương thức updateList
    public void updateList(ArrayList<Category> newList) {
        categoryArrayList.clear();
        categoryArrayList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemListHomeBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemListHomeBinding.bind(itemView);
        }
    }
}