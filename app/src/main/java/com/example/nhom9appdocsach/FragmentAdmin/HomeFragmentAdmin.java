package com.example.nhom9appdocsach.FragmentAdmin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.Model.ListPdf;
import com.example.nhom9appdocsach.Model.User;
import com.example.nhom9appdocsach.databinding.FragmentHomeAdminBinding;
import com.example.nhom9appdocsach.Database.DatabaseHandel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragmentAdmin extends Fragment {
    private static final String TAG = "HomeFragmentAdmin";
    private static final int MAX_BOOKS_TO_SHOW = 20;
    private static final int MAX_DOWNLOADED_BOOKS = 10;

    private FragmentHomeAdminBinding binding;
    private ArrayList<ListPdf> allBooksArrayList;
    private ArrayList<ListPdf> mostDownloadedArrayList;
    private AdapterPdfListUser adapterPdfListUser;
    private AdapterCategoryHome categoryAdapter;
    private String categoryId, categoryTitle;

    private DatabaseHandel dbHelper;
    private String currentUid;

    private boolean isDataLoading = false;
    private boolean isAdded = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isAdded = true;
        dbHelper = new DatabaseHandel(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            binding = FragmentHomeAdminBinding.inflate(inflater, container, false);
            return initializeView();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreateView: " + e.getMessage());
            safeShowToast("Đã xảy ra lỗi khi tải giao diện");
            return null;
        }
    }

    private View initializeView() {
        if (binding == null) {
            Log.e(TAG, "Binding failed to initialize");
            return null;
        }

        initializeArrayLists();
        getIntentData();
        setupClickListeners();
        setupSwipeRefresh();
        initializeRecyclerViews();
        loadUserInfo();

        if (!isDataLoading) {
            loadAllData();
        }

        return binding.getRoot();
    }

    private void initializeArrayLists() {
        allBooksArrayList = new ArrayList<ListPdf>();
        mostDownloadedArrayList = new ArrayList<>();
    }

    private void getIntentData() {
        try {
            if (getActivity() != null && getActivity().getIntent() != null) {
                Intent intent = getActivity().getIntent();
                categoryId = intent.getStringExtra("categoryId");
                categoryTitle = intent.getStringExtra("categoryTitle");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data: " + e.getMessage());
        }
    }

    private void setupClickListeners() {
        if (binding != null) {
            binding.edtdecu.setOnClickListener(v -> safeNavigateToMostDownloaded());
            binding.edtdocnhieu.setOnClickListener(v -> safeNavigateToMostDownloaded());
            binding.buttonkhampha.setOnClickListener(v -> safeNavigateToDashboard());
        }
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
        try {
            if (binding == null || !isAdded() || getContext() == null) return;

            setupBooksRecyclerView();
            setupDownloadsRecyclerView();
            setupCategoriesRecyclerView();

        } catch (Exception e) {
            Log.e(TAG, "Error initializing RecyclerViews: " + e.getMessage());
        }
    }

    private void setupBooksRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        binding.booksRCV.setLayoutManager(gridLayoutManager);
        binding.booksRCV.setHasFixedSize(true);
    }

    private void setupDownloadsRecyclerView() {
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        binding.downloadRCV.setLayoutManager(horizontalLayoutManager);
        binding.downloadRCV.setHasFixedSize(true);
    }

    private void setupCategoriesRecyclerView() {
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
        try {
            final List<SlideModel> imageList = new ArrayList<>();
            final List<String> bookIdList = new ArrayList<>();
            // Lấy 5 sách đầu tiên có imageThumb
            ArrayList<ListPdf> booksWithThumb = dbHelper.getAllBookWithThumb(5);
            Log.d(TAG, "loadImageSlider: Number of books with thumb: " + booksWithThumb.size());

            for (ListPdf book : booksWithThumb) {
                if (book.getImageThumb() != null && !book.getImageThumb().isEmpty()) {
                    Log.d(TAG, "loadImageSlider: Adding book to slider - ID: " + book.getId() + ", Thumb: " + book.getImageThumb());
                    imageList.add(new SlideModel(book.getImageThumb(), ScaleTypes.FIT));
                    bookIdList.add(book.getId());
                } else {
                    Log.w(TAG, "loadImageSlider: Book has no thumbnail - ID: " + book.getId());
                }
            }
            if (!imageList.isEmpty() && binding.imageSlider != null) {
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
                Log.i(TAG, "loadImageSlider: No images to display in slider.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image slider: " + e.getMessage());
        }
    }

    private void loadAllBooks() {
        try {
            allBooksArrayList.clear();
            ArrayList<ListPdf> all = dbHelper.getAllBooksListPdf();
            Log.d(TAG, "loadAllBooks: Number of all books: " + all.size());
            // Đảo ngược danh sách để truyện mới nhất lên đầu
            Collections.reverse(all);
            for (int i = 0; i < Math.min(MAX_BOOKS_TO_SHOW, all.size()); i++) {
                allBooksArrayList.add(all.get(i));
            }
            updateBooksAdapter();
        } catch (Exception e) {
            Log.e(TAG, "Error loading all books: " + e.getMessage());
        }
    }

    private void updateBooksAdapter() {
        if (getContext() == null || binding == null) return;

        adapterPdfListUser = new AdapterPdfListUser(getContext(), allBooksArrayList);
        binding.booksRCV.setAdapter(adapterPdfListUser);
    }

    private void loadMostDownloadedBooks() {
        try {
            mostDownloadedArrayList.clear();
            ArrayList<ListPdf> all = dbHelper.getAllBooksListPdf();
            // Sắp xếp giảm dần theo downloadsCount
            Collections.sort(all, (a, b) -> Long.compare(b.getDownloadsCount(), a.getDownloadsCount()));
            Log.d(TAG, "loadMostDownloadedBooks: Number of all books: " + all.size());
            for (int i = 0; i < Math.min(MAX_DOWNLOADED_BOOKS, all.size()); i++) {
                mostDownloadedArrayList.add(all.get(i));
            }
            updateDownloadsAdapter();
        } catch (Exception e) {
            Log.e(TAG, "Error loading most downloaded books: " + e.getMessage());
        }
    }

    private void updateDownloadsAdapter() {
        if (getContext() == null || binding == null) return;

        AdapterPdfListUser downloadsAdapter = new AdapterPdfListUser(getContext(), mostDownloadedArrayList);
        binding.downloadRCV.setAdapter(downloadsAdapter);
    }

    private void loadCategories() {
        try {
            ArrayList<Category> categoriesAll = dbHelper.getAllCategories();
            ArrayList<Category> categories = new ArrayList<>();
            Category khacCategory = null;
            for (Category model : categoriesAll) {
                if (model.getCategory().equals("Khác")) {
                    khacCategory = model;
                } else {
                    categories.add(model);
                }
            }
            if (khacCategory != null) {
                categories.add(khacCategory);
            }
            updateCategoriesAdapter(categories);
        } catch (Exception e) {
            Log.e(TAG, "Error loading categories: " + e.getMessage());
        }
    }

    private void updateCategoriesAdapter(ArrayList<Category> categories) {
        try {
            if (getContext() == null || binding == null || !isAdded()) return;

            categoryAdapter = new AdapterCategoryHome(getContext(), categories);
            binding.theloaiRCV.setAdapter(categoryAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error updating categories adapter: " + e.getMessage());
        }
    }

    private void safeNavigateToMostDownloaded() {
        try {
            if (isAdded() && getActivity() != null) {
                Intent intent = new Intent(getActivity(), DashBoardUserActivity.class);
                intent.putExtra("category", "Đọc nhiều nhất");
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to most downloaded: " + e.getMessage());
            safeShowToast("Không thể chuyển trang");
        }
    }

    private void safeNavigateToDashboard() {
        try {
            if (isAdded() && getActivity() != null) {
                startActivity(new Intent(getActivity(), DashBoardUserActivity.class));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to dashboard: " + e.getMessage());
            safeShowToast("Không thể chuyển trang");
        }
    }

    private void safeShowToast(String message) {
        try {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing toast: " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            binding = null;
            allBooksArrayList = null;
            mostDownloadedArrayList = null;
            adapterPdfListUser = null;
            categoryAdapter = null;
        } catch (Exception e) {
            Log.e(TAG, "Error in onDestroyView: " + e.getMessage());
        }
    }

    private void loadUserInfo() {
        // Lấy uid hiện tại từ SharedPreferences
        String uid = getCurrentUserId();
        if (uid == null) {
            Log.e(TAG, "User ID is null");
            return;
        }
        try {
            User user = dbHelper.getUserById(uid);
            if (user != null && user.getName() != null && !user.getName().isEmpty()) {
                binding.txtname.setText(user.getName());
                binding.txtname.setVisibility(View.VISIBLE);
            } else {
                binding.txtname.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading user info: " + e.getMessage());
        }
    }

    private String getCurrentUserId() {
        // Lấy userId từ SharedPreferences hoặc truyền vào Fragment
        // Ví dụ:
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return prefs.getString("uid", null);
//        return null; // <-- bạn cần hiện thực lại chỗ này cho app của bạn!
    }
}