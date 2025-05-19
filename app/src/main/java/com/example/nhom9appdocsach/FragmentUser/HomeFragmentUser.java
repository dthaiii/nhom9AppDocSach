package com.example.nhom9appdocsach.FragmentUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.nhom9appdocsach.Activities.DashBoardUserActivity;
import com.example.nhom9appdocsach.Activities.PdfDetailActivity;
import com.example.nhom9appdocsach.Adapter.AdapterCategoryHome;
import com.example.nhom9appdocsach.Adapter.AdapterPdfListUser;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.Model.ListPdf;
import com.example.nhom9appdocsach.Model.User;
import com.example.nhom9appdocsach.databinding.FragmentHomeUserBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragmentUser extends Fragment {
    private static final String TAG = "HomeFragmentUser";
    private static final int MAX_BOOKS_TO_SHOW = 20;
    private static final int MAX_DOWNLOADED_BOOKS = 10;

    private FragmentHomeUserBinding binding;
    private ArrayList<ListPdf> allBooksArrayList;
    private ArrayList<ListPdf> mostDownloadedArrayList;
    private AdapterPdfListUser adapterPdfListUser;
    private AdapterCategoryHome categoryAdapter;
    private String categoryId, categoryTitle;
    private DatabaseHandel dbHelper;
    private boolean isDataLoading = false;
    private boolean isAdded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAdded = true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeUserBinding.inflate(inflater, container, false);

        dbHelper = new DatabaseHandel(requireContext());
        initializeArrayLists();
        getIntentData();
        setupClickListeners();
        setupSwipeRefresh();
        initializeRecyclerViews();

        if (!isDataLoading) {
            loadAllData();
        }
        return binding.getRoot();
    }

    private void initializeArrayLists() {
        allBooksArrayList = new ArrayList<>();
        mostDownloadedArrayList = new ArrayList<>();
    }

    private void getIntentData() {
        if (getActivity() != null && getActivity().getIntent() != null) {
            Intent intent = getActivity().getIntent();
            categoryId = intent.getStringExtra("categoryId");
            categoryTitle = intent.getStringExtra("categoryTitle");
        }
    }

    private void setupClickListeners() {
        View.OnClickListener dashboardClickListener = v -> safeNavigateToDashboard();
        View.OnClickListener mostDownloadedClickListener = v -> safeNavigateToMostDownloaded();

        binding.edtdecu.setOnClickListener(mostDownloadedClickListener);
        binding.edtdocnhieu.setOnClickListener(mostDownloadedClickListener);
        binding.buttonkhampha.setOnClickListener(dashboardClickListener);
        binding.imageSlider.setOnClickListener(dashboardClickListener);
    }

    private void setupSwipeRefresh() {
        if (binding != null && binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                if (!isDataLoading) {
                    loadAllData();
                }
                binding.swipeRefreshLayout.setRefreshing(false);
            });
        }
    }

    private void initializeRecyclerViews() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.booksRCV.setLayoutManager(gridLayoutManager);
        binding.booksRCV.setHasFixedSize(true);

        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.HORIZONTAL, false
        );
        binding.downloadRCV.setLayoutManager(horizontalLayoutManager);
        binding.downloadRCV.setHasFixedSize(true);

        LinearLayoutManager categoryLayoutManager = new LinearLayoutManager(getContext());
        binding.theloaiRCV.setLayoutManager(categoryLayoutManager);
        binding.theloaiRCV.setHasFixedSize(true);
    }

    private void loadAllData() {
        isDataLoading = true;
        loadImageSlider();
        loadAllBooks();
        loadMostDownloadedBooks();
        loadCategories();
        loadUserInfo();
        isDataLoading = false;
    }

    private void loadImageSlider() {
        // Lấy danh sách ListPdf có ảnh thumbnail, giới hạn 5-8 cuốn
        List<ListPdf> pdfs = dbHelper.getAllBookWithThumb(8);
        final List<SlideModel> imageList = new ArrayList<>();
        final List<String> bookIdList = new ArrayList<>();
        int count = 0;

        Log.d(TAG, "loadImageSlider: Number of books with thumb: " + pdfs.size()); // Log

        for (ListPdf pdf : pdfs) {
            if (pdf.getImageThumb() != null && !pdf.getImageThumb().isEmpty()) {
                Log.d(TAG, "loadImageSlider: Adding book to slider - ID: " + pdf.getId() + ", Thumb: " + pdf.getImageThumb()); // Log
                imageList.add(new SlideModel(pdf.getImageThumb(), ScaleTypes.FIT));
                bookIdList.add(pdf.getId());
                count++;
                if (count >= 5) break;
            } else {
                Log.w(TAG, "loadImageSlider: Book has no thumbnail - ID: " + pdf.getId()); // Log
            }
        }
        if (!imageList.isEmpty()) {
            binding.imageSlider.setImageList(imageList, ScaleTypes.FIT);
            binding.imageSlider.setItemClickListener(new ItemClickListener() {
                @Override
                public void doubleClick(int i) { }
                @Override
                public void onItemSelected(int position) {
                    if (position < bookIdList.size()) {
                        String bookId = bookIdList.get(position);
                        Intent intent = new Intent(requireActivity(), PdfDetailActivity.class);
                        intent.putExtra("bookId", bookId);
                        startActivity(intent);
                    }
                }
            });
        } else {
            Log.i(TAG, "loadImageSlider: No images to display in slider."); // Log
        }
    }

    private void loadAllBooks() {
        allBooksArrayList.clear();
        ArrayList<ListPdf> list = dbHelper.getAllBooksListPdf();

        Log.d(TAG, "loadAllBooks: Number of all books: " + list.size()); // Log

        for (int i = 0; i < Math.min(list.size(), MAX_BOOKS_TO_SHOW); i++) {
            allBooksArrayList.add(list.get(i));
        }
        updateBooksAdapter();
    }

    private void loadMostDownloadedBooks() {
        mostDownloadedArrayList.clear();
        ArrayList<ListPdf> list = dbHelper.getAllBooksListPdf();
        Collections.sort(list, (a, b) -> Long.compare(b.getDownloadsCount(), a.getDownloadsCount()));

        Log.d(TAG, "loadMostDownloadedBooks: Number of all books: " + list.size()); // Log

        for (int i = 0; i < Math.min(list.size(), MAX_DOWNLOADED_BOOKS); i++) {
            mostDownloadedArrayList.add(list.get(i));
        }
        updateDownloadsAdapter();
    }

    private void loadCategories() {
        ArrayList<Category> categories = dbHelper.getAllCategories();
        Category khacCategory = null;

        // Đưa "Khác" xuống cuối
        for (Category c : new ArrayList<>(categories)) {
            if ("Khác".equals(c.getCategory())) {
                khacCategory = c;
                categories.remove(c);
                break;
            }
        }
        if (khacCategory != null) categories.add(khacCategory);

        updateCategoriesAdapter(categories);
    }

    private void loadUserInfo() {
        // Lấy uid từ SharedPreferences
        String uid = requireActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE).getString("uid", "");
        if (uid != null && !uid.isEmpty()) {
            User user = dbHelper.getUserById(uid);
            if (user != null && user.getName() != null && !user.getName().isEmpty()) {
                binding.txtname.setText(user.getName());
                binding.txtname.setVisibility(View.VISIBLE);
            } else {
                binding.txtname.setVisibility(View.GONE);
            }
        } else {
            binding.txtname.setVisibility(View.GONE);
        }
    }

    private void updateBooksAdapter() {
        adapterPdfListUser = new AdapterPdfListUser(getContext(), allBooksArrayList);
        binding.booksRCV.setAdapter(adapterPdfListUser);
    }

    private void updateDownloadsAdapter() {
        AdapterPdfListUser downloadsAdapter = new AdapterPdfListUser(getContext(), mostDownloadedArrayList);
        binding.downloadRCV.setAdapter(downloadsAdapter);
    }

    private void updateCategoriesAdapter(ArrayList<Category> categories) {
        categoryAdapter = new AdapterCategoryHome(getContext(), categories);
        binding.theloaiRCV.setAdapter(categoryAdapter);
    }

    private void safeNavigateToMostDownloaded() {
        Intent intent = new Intent(getActivity(), DashBoardUserActivity.class);
        intent.putExtra("category", "Đọc nhiều nhất");
        startActivity(intent);
    }

    private void safeNavigateToDashboard() {
        startActivity(new Intent(getActivity(), DashBoardUserActivity.class));
    }

    private void safeShowToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        allBooksArrayList = null;
        mostDownloadedArrayList = null;
        adapterPdfListUser = null;
        categoryAdapter = null;
    }
}