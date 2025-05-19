package com.example.nhom9appdocsach.FragmentUser;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.nhom9appdocsach.Adapter.AdapterCategoryUser;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.databinding.FragmentBookUserBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BookFragmentUser extends Fragment {

    private FragmentBookUserBinding binding;
    private ArrayList<Category> categoryArrayList;
    private AdapterCategoryUser adapterCategoryUser;
    private DatabaseHandel dbHelper;

    public BookFragmentUser() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookUserBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHandel(requireContext());
        loadCategories();

        binding.edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (adapterCategoryUser != null) {
                        adapterCategoryUser.getFilter().filter(s);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadCategories() {
        categoryArrayList = dbHelper.getAllCategories();
        if (categoryArrayList == null) categoryArrayList = new ArrayList<>();

        // Sắp xếp danh sách theo tên category
        Collections.sort(categoryArrayList, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                return c1.getCategory().compareToIgnoreCase(c2.getCategory());
            }
        });

        // Đưa category "Khác" xuống cuối
        if (categoryArrayList.size() > 1) {
            for (int i = 0; i < categoryArrayList.size(); i++) {
                if ("Khác".equals(categoryArrayList.get(i).getCategory())) {
                    Category other = categoryArrayList.remove(i);
                    categoryArrayList.add(other);
                    break;
                }
            }
        }

        adapterCategoryUser = new AdapterCategoryUser(requireActivity(), categoryArrayList);
        binding.catagoryRecycleview.setAdapter(adapterCategoryUser);

        if (categoryArrayList.isEmpty()) {
            Toast.makeText(requireActivity(), "Không có thể loại nào!", Toast.LENGTH_SHORT).show();
        }
    }
}