package com.example.nhom9appdocsach.Adapter;

import android.app.AlertDialog;
import android.content.Context;
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
import com.example.nhom9appdocsach.databinding.RowCommentBinding;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.HolderComment> {

    private Context context;
    private ArrayList<Comment> commentArrayList;
    private RowCommentBinding binding;
    private String currentUid;

    public AdapterComment(Context context, ArrayList<Comment> commentArrayList, String currentUid) {
        this.context = context;
        this.commentArrayList = commentArrayList;
        this.currentUid = currentUid; // lấy từ user hiện tại
    }

    @NonNull
    @Override
    public HolderComment onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent, false);
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
        String date = MyApplication.formatTimestamp((timestamp));

        holder.txtdate.setText(date);
        holder.txtcomment.setText(comment);
        loadUserDetails(modelComment, holder);

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
        // Giả sử có hàm getUserNameAndAvatar trả về pair [name, avatarUrl]
        String[] userInfo = dbHelper.getUserNameAndAvatar(uid);
        String name = userInfo[0];
        String profileImage = userInfo[1];

        holder.txtname.setText(name);
//        try {
//            Glide.with(context)
//                    .load(profileImage)
//                    .placeholder(R.drawable.avatar)
//                    .into(holder.imagecomment);
//        } catch (Exception e) {
//            holder.imagecomment.setImageResource(R.drawable.avatar);
//        }
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    class HolderComment extends RecyclerView.ViewHolder {
//        CircleImageView imagecomment;
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