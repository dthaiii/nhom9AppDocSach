package com.example.nhom9appdocsach.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Comment;
import com.example.nhom9appdocsach.MyApplication;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.databinding.RowCommentsDetailBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterCommentDetails extends RecyclerView.Adapter<AdapterCommentDetails.HolderComment> {

    private Context context;
    private ArrayList<Comment> commentArrayList;
    private RowCommentsDetailBinding binding;
    private String currentUid;
    private int highlightedPosition = -1;

    public AdapterCommentDetails(Context context, ArrayList<Comment> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
        this.currentUid = currentUid;
    }

    public void setHighlightedPosition(int position) {
        this.highlightedPosition = position;
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCommentsDetailBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderComment(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderComment holder, int position) {
        Comment modelComment = commentArrayList.get(position);
        String commentId = modelComment.getId();
        String bookId = modelComment.getBookId();
        String comment = modelComment.getComment();
        String uid = modelComment.getUid();
        Long timestamp = modelComment.getTimestamp();
        String date = MyApplication.formatTimestamp(timestamp);

        holder.txtdate.setText(date);
        holder.txtcomment.setText(comment);
        loadUserDetails(modelComment, holder);

        // Highlight if needed
        if (position == highlightedPosition) {
            holder.itemView.setBackgroundColor(Color.YELLOW);
            // Remove highlight after a short delay
            new android.os.Handler().postDelayed(() -> {
                highlightedPosition = -1;
                notifyItemChanged(position);
            }, 1000);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.itemView.setOnClickListener(v -> {
            if (currentUid != null && uid.equals(currentUid)) {
                deleteComment(modelComment, holder, position);
            }
        });
    }

    private void deleteComment(Comment modelComment, HolderComment holder, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Xóa bình luận")
                .setMessage("Bạn có muốn xóa bình luận này?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    DatabaseHandel dbHelper = new DatabaseHandel(context);
                    boolean deleted = dbHelper.deleteCommentById(modelComment.getId(), modelComment.getBookId());
                    if (deleted) {
                        commentArrayList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Lỗi khi xóa", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadUserDetails(Comment modelComment, HolderComment holder) {
        String uid = modelComment.getUid();
        DatabaseHandel dbHelper = new DatabaseHandel(context);
        String[] userInfo = dbHelper.getUserNameAndAvatar(uid);
        String name = userInfo[0];
        String profileImage = userInfo[1];

        holder.txtname.setText(name);
        try {
            Glide.with(context)
                    .load(profileImage)
                    .placeholder(R.drawable.avatar)
                    .into(holder.imagecomment);
        } catch (Exception e) {
            holder.imagecomment.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder{
        CircleImageView imagecomment;
        TextView txtname, txtdate, txtcomment;
        public HolderComment(@NonNull android.view.View itemView) {
            super(itemView);

//            imagecomment = binding.imagecomment;
            txtname = binding.txtname;
            txtdate = binding.txtdate;
            txtcomment = binding.txtcomment;
        }
    }
}