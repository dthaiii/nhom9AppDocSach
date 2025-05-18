package com.example.nhom9appdocsach.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Filter.FilterCategory;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.Activities.PdfListAdminActivity;
import com.example.nhom9appdocsach.databinding.RowCategoryBinding;
import com.example.nhom9appdocsach.Database.DatabaseHandel; // Thêm dòng này

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> implements Filterable {

    private Context context;
    public ArrayList<Category> catagoryArrayList, filterList;
    private RowCategoryBinding binding;
    private FilterCategory filterCategory;

    public AdapterCategory(Context context, ArrayList<Category> catagoryArrayList) {
        this.context = context;
        this.catagoryArrayList = catagoryArrayList;
        this.filterList = catagoryArrayList;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderCategory(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        Category modelCatagory = catagoryArrayList.get(position);
        String id = modelCatagory.getId();
        String category = modelCatagory.getCategory();
        String uid = modelCatagory.getUid();
        long timestamp = modelCatagory.getTimestamp();

        holder.edttentl.setText(category);

        holder.btnxoatl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa thể loại")
                        .setMessage("Tất cả sách trong thể loại này sẽ được chuyển sang thể loại 'Khác'. Bạn có chắc chắn muốn xóa?")
                        .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCategory(modelCatagory);
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfListAdminActivity.class);
                intent.putExtra("categoryId", id);
                intent.putExtra("categoryTitle", category);
                context.startActivity(intent);
            }
        });
    }

    private void deleteCategory(Category modelCategory) {
        String categoryId = modelCategory.getId();
        String category = modelCategory.getCategory();

        if (category.equals("Khác")) {
            Toast.makeText(context, "Không thể xóa thể loại 'Khác'", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHandel dbHelper = new DatabaseHandel(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Kiểm tra thể loại "Khác"
        String otherId = dbHelper.getCategoryByName("Khác");


        // Cập nhật tất cả sách thuộc categoryId này sang "Khác"
        dbHelper.updateBooksCategory(categoryId, otherId);

        // Xóa thể loại khỏi bảng Category
        dbHelper.deleteCategory(categoryId);

        // Cập nhật lại danh sách thể loại hiển thị
        catagoryArrayList.remove(modelCategory);
        notifyDataSetChanged();

        Toast.makeText(context, "Đã xóa thể loại và chuyển sách sang thể loại 'Khác'", Toast.LENGTH_SHORT).show();

        db.close();
    }

    @Override
    public int getItemCount() {
        return catagoryArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterCategory == null) {
            filterCategory = new FilterCategory(filterList, this);
        }
        return filterCategory;
    }

    class HolderCategory extends RecyclerView.ViewHolder {
        TextView edttentl;
        ImageButton btnxoatl;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            edttentl = binding.edttentl;
            btnxoatl = binding.btnxoatl;
        }
    }
}