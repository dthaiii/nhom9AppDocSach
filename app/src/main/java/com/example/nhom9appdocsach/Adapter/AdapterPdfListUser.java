package com.example.nhom9appdocsach.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Activities.PdfDetailActivity;
import com.example.nhom9appdocsach.Model.ListPdf;
import com.example.nhom9appdocsach.MyApplication;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.RowPdfListBinding;

import java.util.ArrayList;

public class AdapterPdfListUser extends RecyclerView.Adapter<AdapterPdfListUser.HolderPdfListUser> {
    private Context context;
    private ArrayList<ListPdf> pdfArrayListUser;

    public AdapterPdfListUser(Context context, ArrayList<ListPdf> pdfArrayListUser) {
        this.context = context;
        this.pdfArrayListUser = pdfArrayListUser;
    }

    @NonNull
    @Override
    public HolderPdfListUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowPdfListBinding binding = RowPdfListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfListUser(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfListUser holder, int position) {
        ListPdf modelListPdf = pdfArrayListUser.get(position);
        String title = modelListPdf.getTitle();
        holder.txttitle.setText(title);
        holder.progressBar.setVisibility(ViewGroup.GONE);
        Glide.with(context).load(modelListPdf.getImageThumb())
                        .placeholder(R.drawable.skeleton)
                                .error(R.drawable.warning)
                                        .centerCrop()
                                                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfDetailActivity.class);
            intent.putExtra("bookId", modelListPdf.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return pdfArrayListUser.size();
    }

    class HolderPdfListUser extends RecyclerView.ViewHolder {
        TextView txttitle;
        ImageView imageView;
        ProgressBar progressBar;

        public HolderPdfListUser(@NonNull RowPdfListBinding binding) {
            super(binding.getRoot());
            txttitle = binding.txttitle;
            imageView = binding.ImageThumb;
            progressBar = binding.progressBar;
        }
    }
}