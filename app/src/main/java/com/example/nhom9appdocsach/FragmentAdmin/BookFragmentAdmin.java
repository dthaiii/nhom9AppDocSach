package com.example.nhom9appdocsach.FragmentAdmin;

import android.content.Intent;
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

import com.example.nhom9appdocsach.Adapter.AdapterCategory;
import com.example.nhom9appdocsach.Activities.AddCategoryActivity;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.databinding.FragmentBookAdminBinding;
import com.example.nhom9appdocsach.Database.DatabaseHandel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BookFragmentAdmin extends Fragment {

    private FragmentBookAdminBinding binding;
    private ArrayList<Category> categoryArrayList;
    private AdapterCategory adapterCategory;

    public BookFragmentAdmin() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadCategories();
        binding.btnthemtheloai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireActivity(), AddCategoryActivity.class));
            }
        });

        binding.edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (adapterCategory != null) {
                        adapterCategory.getFilter().filter(s);
                    }
                } catch (Exception e) {
                    // Ignore filter errors
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

    }

    private void loadCategories() {
        categoryArrayList = new ArrayList<>();
        DatabaseHandel dbHelper = new DatabaseHandel(requireContext());
        categoryArrayList = dbHelper.getAllCategories();

        // Sắp xếp danh sách theo tên category
        Collections.sort(categoryArrayList, new Comparator<Category>() {
            @Override
            public int compare(Category c1, Category c2) {
                return c1.getCategory().compareToIgnoreCase(c2.getCategory());
            }
        });

        // Tùy chọn: đưa category "Khác" xuống cuối danh sách
        if (categoryArrayList.size() > 1) {
            for (int i = 0; i < categoryArrayList.size(); i++) {
                if ("Khác".equals(categoryArrayList.get(i).getCategory())) {
                    Category other = categoryArrayList.remove(i);
                    categoryArrayList.add(other);
                    break;
                }
            }
        }

        adapterCategory = new AdapterCategory(requireActivity(), categoryArrayList);
        binding.catagoryRecycleview.setAdapter(adapterCategory);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}