package com.example.nhom9appdocsach.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Activities.PdfDetailActivity;
import com.example.nhom9appdocsach.Filter.FilterFavorite;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.MyApplication;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.RowPdfFavoriteBinding;
import com.example.nhom9appdocsach.Database.DatabaseHandel;

import java.util.ArrayList;

public class AdapterFavoriteBook extends RecyclerView.Adapter<AdapterFavoriteBook.HolderFavoriteBook> implements Filterable {

    private Context context;
    public ArrayList<Pdf> pdfArrayList, filterList;
    private RowPdfFavoriteBinding binding;
    private static final String TAG = "FAV_BOOK_TAG";
    private FilterFavorite filterFavorite;
    private String uid;

    public AdapterFavoriteBook(Context context, ArrayList<Pdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;

        // Lấy uid từ SharedPreferences
        uid = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE).getString("uid", null);
    }

    @NonNull
    @Override
    public HolderFavoriteBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfFavoriteBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderFavoriteBook(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFavoriteBook holder, int position) {
        Pdf modelPdf = pdfArrayList.get(position);
        loadBookDetails(modelPdf, holder);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfDetailActivity.class);
            intent.putExtra("bookId", modelPdf.getId());
            context.startActivity(intent);
        });
        holder.btnfavor.setOnClickListener(v -> {
            MyApplication.removeFromFavorite(context, modelPdf.getId(), uid);
            // Cập nhật lại giao diện sau khi xóa khỏi yêu thích
            pdfArrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, pdfArrayList.size());
        });
    }

    private void loadBookDetails(Pdf modelPdf, HolderFavoriteBook holder) {
        String bookId = modelPdf.getId();
        Log.d(TAG, "LoadBookDetails: Chi tiet: " + bookId);

        DatabaseHandel dbHelper = new DatabaseHandel(context);
        Pdf dbPdf = dbHelper.getBookPdfById(bookId);

        if (dbPdf != null) {
            modelPdf.setFavorite(true);
            modelPdf.setTitle(dbPdf.getTitle());
            modelPdf.setDescription(dbPdf.getDescription());
            modelPdf.setTimestamp(dbPdf.getTimestamp());
            modelPdf.setCategoryId(dbPdf.getCategoryId());
            modelPdf.setUid(dbPdf.getUid());
            modelPdf.setUrl(dbPdf.getUrl());

            String date = MyApplication.formatTimestamp(dbPdf.getTimestamp());

            MyApplication.loadCategory(context, dbPdf.getCategoryId(), holder.txttheloai);
            MyApplication.loadImageFromUrl(context, bookId, holder.imageThumb, holder.progressBar);
            MyApplication.LoadPdfSize(context, dbPdf.getUrl(), bookId, holder.txtsize);

            holder.txttitle.setText(dbPdf.getTitle());
            holder.txtdes.setText(dbPdf.getDescription());
            holder.txtdate.setText(date);
            try {
                Glide.with(context)
                        .load(dbPdf.getImageThumb())
                        .placeholder(R.drawable.avatar)
                        .into(holder.imageThumb);
            } catch (Exception e) {
                holder.imageThumb.setImageResource(R.drawable.avatar);
            }
        }
    }

    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filterFavorite == null) {
            filterFavorite = new FilterFavorite(filterList, this);
        }
        return filterFavorite;
    }

    class HolderFavoriteBook extends RecyclerView.ViewHolder {
        ImageView imageThumb;
        ProgressBar progressBar;
        TextView txttitle, txtdes, txttheloai, txtsize, txtdate;
        ImageButton btnfavor;

        public HolderFavoriteBook(@NonNull View itemView) {
            super(itemView);
            imageThumb = binding.ImageThumb;
            progressBar = binding.progressBar;
            txttitle = binding.txttitle;
            txtdes = binding.txtdes;
            txttheloai = binding.txttheloai;
            txtsize = binding.txtsize;
            txtdate = binding.txtdate;
            btnfavor = binding.btnfavor;
        }
    }
}