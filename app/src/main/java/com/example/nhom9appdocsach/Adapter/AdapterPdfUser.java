package com.example.nhom9appdocsach.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhom9appdocsach.Activities.PdfDetailActivity;
import com.example.nhom9appdocsach.Filter.FilterPdfUser;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.MyApplication;
import com.example.nhom9appdocsach.databinding.RowPdfUserBinding;

import java.util.ArrayList;

public class AdapterPdfUser extends RecyclerView.Adapter<AdapterPdfUser.HolderPdfUser> implements Filterable {
    private Context context;
    public ArrayList<Pdf> pdfArrayList, filterList;
    private RowPdfUserBinding binding;
    private static final String TAG = "PDF_ADAPTER_TAG";
    private FilterPdfUser filterPdfUser;

    public AdapterPdfUser(Context context, ArrayList<Pdf> pdfArrayList) {
        this.context = context;
        this.pdfArrayList = pdfArrayList;
        this.filterList = pdfArrayList;
        Toast.makeText(context, "Doi ty", Toast.LENGTH_SHORT).show();
    }
    @NonNull
    @Override
    public HolderPdfUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false);
        return new HolderPdfUser(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPdfUser holder, int position) {
        Pdf modelPdf = pdfArrayList.get(position);
        String pdfId = modelPdf.getId();
        String categoryId = modelPdf.getCategoryId();
        String title = modelPdf.getTitle();
        String description = modelPdf.getDescription();
        String pdfUrl = modelPdf.getUrl();
        Long timestamp = modelPdf.getTimestamp();

        String formattedDate = MyApplication.formatTimestamp(timestamp);
        holder.txttilte.setText(title);
        holder.txtdes.setText(description);
        holder.txtdate.setText(formattedDate);
        MyApplication.loadCategory(
                context,"" + categoryId, holder.txttheloai
        );
//        MyApplication.loadPdfFromUrlSinglePage(
//                ""+pdfUrl,
//                ""+title,
//                holder.pdfView,
//                holder.progressBar
//                ,null
//        );
                MyApplication.loadImageFromUrl(context,""+pdfId,holder.imageThumb,holder.progressBar);
        MyApplication.LoadPdfSize(context,
                ""+pdfUrl,
                ""+title,
                holder.txtsize);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PdfDetailActivity.class);
                intent.putExtra("bookId",pdfId);
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return pdfArrayList.size();
    }
    @Override
    public Filter getFilter() {
        if (filterPdfUser == null){
            filterPdfUser = new FilterPdfUser(filterList, this);
        }
        return filterPdfUser;
    }
    class HolderPdfUser extends RecyclerView.ViewHolder{
        ImageView imageThumb;
        ProgressBar progressBar;
        TextView txttilte, txtdes, txttheloai, txtsize, txtdate;
        public HolderPdfUser(@NonNull View itemView) {
            super(itemView);
            imageThumb = binding.ImageThumb;
            progressBar = binding.progressBar;
            txttilte = binding.txttitle;
            txtdes = binding.txtdes;
            txttheloai = binding.txttheloai;
            txtsize = binding.txtsize;
            txtdate = binding.txtdate;
        }
    }


}
