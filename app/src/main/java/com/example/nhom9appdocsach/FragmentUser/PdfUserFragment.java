package com.example.nhom9appdocsach.FragmentUser;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nhom9appdocsach.Adapter.AdapterPdfUser;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.databinding.FragmentPdfUserBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PdfUserFragment extends Fragment {

    private String categoryId;
    private String category;
    private String uid;
    private ArrayList<Pdf> pdfArrayList;
    private AdapterPdfUser adapterPdfUser;
    private FragmentPdfUserBinding binding;
    private DatabaseHandel dbHelper;

    private static final String TAG = "BOOK_USER_TAG";

    public PdfUserFragment() {
        // Required empty public constructor
    }

    public static PdfUserFragment newInstance(String categoryId, String category, String uid) {
        PdfUserFragment fragment = new PdfUserFragment();
        Bundle args = new Bundle();
        args.putString("categoryId", categoryId);
        args.putString("category", category);
        args.putString("uid", uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId");
            category = getArguments().getString("category");
            uid = getArguments().getString("uid");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPdfUserBinding.inflate(inflater, container, false);
        dbHelper = new DatabaseHandel(requireContext());
        Log.d(TAG, "onCreateView: Thể loại: " + category);

        if (category == null) category = "";
        if (category.equals("Tất cả")) {
            loadAllBooks();
        } else if (category.equals("Tải nhiều nhất")) {
            loadMostViewedDownloadedBooks("downloadsCount");
        } else if (category.equals("Đọc nhiều nhất")) {
            loadMostViewedDownloadedBooks("viewsCount");
        } else {
            loadCategorizedBooks();
        }

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (adapterPdfUser != null) {
                        adapterPdfUser.getFilter().filter(s);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onTextChanged: " + e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return binding.getRoot();
    }

    private void loadMostViewedDownloadedBooks(String orderBy) {
        pdfArrayList = dbHelper.getAllBooksPdf();
        if (pdfArrayList == null) pdfArrayList = new ArrayList<>();
        // Sắp xếp giảm dần theo trường orderBy
        if (orderBy.equals("downloadsCount")) {
            Collections.sort(pdfArrayList, new Comparator<Pdf>() {
                @Override
                public int compare(Pdf a, Pdf b) {
                    return Long.compare(b.getDownloadsCount(), a.getDownloadsCount());
                }
            });
        } else if (orderBy.equals("viewsCount")) {
            Collections.sort(pdfArrayList, new Comparator<Pdf>() {
                @Override
                public int compare(Pdf a, Pdf b) {
                    return Long.compare(b.getViewsCount(), a.getViewsCount());
                }
            });
        }
        // Lấy tối đa 10 cuốn
        ArrayList<Pdf> topList = new ArrayList<>();
        for (int i = 0; i < Math.min(10, pdfArrayList.size()); i++) {
            topList.add(pdfArrayList.get(i));
        }
        adapterPdfUser = new AdapterPdfUser(getContext(), topList);
        binding.booksRCV.setAdapter(adapterPdfUser);
    }

    private void loadCategorizedBooks() {
        pdfArrayList = dbHelper.getBooksByCategory(categoryId);
        if (pdfArrayList == null) pdfArrayList = new ArrayList<>();
        adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
        binding.booksRCV.setAdapter(adapterPdfUser);
    }

    private void loadAllBooks() {
        pdfArrayList = dbHelper.getAllBooksPdf();
        if (pdfArrayList == null) pdfArrayList = new ArrayList<>();
        adapterPdfUser = new AdapterPdfUser(getContext(), pdfArrayList);
        binding.booksRCV.setAdapter(adapterPdfUser);
    }
}