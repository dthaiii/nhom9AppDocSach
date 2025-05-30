package com.example.nhom9appdocsach.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.nhom9appdocsach.Activities.PdfDetailActivity;
import com.example.nhom9appdocsach.Model.Noti;
import com.example.nhom9appdocsach.R;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterNoti extends RecyclerView.Adapter<AdapterNoti.HolderNotification> {

    private Context context;
    private ArrayList<Noti> notificationArrayList;

    public AdapterNoti(Context context, ArrayList<Noti> notificationArrayList) {
        this.context = context;
        this.notificationArrayList = notificationArrayList;
    }

    @NonNull
    @Override
    public HolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_noti, parent, false);
        return new HolderNotification(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderNotification holder, int position) {
        Noti model = notificationArrayList.get(position);

        String title = model.getTitle();
        String message = model.getMessage();
        String bookId = model.getBookId();
        long timestamp = model.getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(timestamp);
        String dateFormat = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

        holder.titleTv.setText(title);
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateFormat);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfDetailActivity.class);
            intent.putExtra("bookId", bookId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notificationArrayList.size();
    }

    class HolderNotification extends RecyclerView.ViewHolder {

        TextView titleTv, messageTv, timeTv;

        public HolderNotification(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}