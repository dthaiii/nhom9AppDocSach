package com.example.nhom9appdocsach.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.FragmentUser.PdfUserFragment;
import com.example.nhom9appdocsach.Model.Category;
import com.example.nhom9appdocsach.databinding.ActivityDashBoardUserBinding;

import java.util.ArrayList;

public class DashBoardUserActivity extends AppCompatActivity {
    public ArrayList<Category> categoryArrayList;
    private ArrayList<PdfUserFragment> fragmentList = new ArrayList<>();

    public ViewPagerAdapter viewPagerAdapter;
    private ActivityDashBoardUserBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashBoardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String category = getIntent().getStringExtra("category");

        setupViewPagerAdapter(binding.viewPager);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        PdfUserFragment pdfUserFragment = PdfUserFragment.newInstance(null, category, null);
    }

    private void setupViewPagerAdapter(ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, this);
        categoryArrayList = new ArrayList<>();

        // Lấy dữ liệu từ SQLite thay vì Firebase
        DatabaseHandel dbHelper = new DatabaseHandel(this);

        categoryArrayList.clear();

        // Thêm 3 tab đặc biệt
        Category modelAll = new Category("01", "Tất cả", "", 1);
        Category modelMostViewed = new Category("02", "Đọc nhiều nhất", "", 1);
        Category modelMostDownloaded = new Category("03", "Tải nhiều nhất", "", 1);

        categoryArrayList.add(modelAll);
        categoryArrayList.add(modelMostViewed);
        categoryArrayList.add(modelMostDownloaded);

        viewPagerAdapter.addFragment(PdfUserFragment.newInstance(
                "" + modelAll.getId(),
                "" + modelAll.getCategory(),
                "" + modelAll.getUid()
        ), modelAll.getCategory());

        viewPagerAdapter.addFragment(PdfUserFragment.newInstance(
                "" + modelMostViewed.getId(),
                "" + modelMostViewed.getCategory(),
                "" + modelMostViewed.getUid()
        ), modelMostViewed.getCategory());

        viewPagerAdapter.addFragment(PdfUserFragment.newInstance(
                "" + modelMostDownloaded.getId(),
                "" + modelMostDownloaded.getCategory(),
                "" + modelMostDownloaded.getUid()
        ), modelMostDownloaded.getCategory());

        // Thêm các thể loại lấy từ SQLite
        ArrayList<Category> allCategoriesInDb = dbHelper.getAllCategories();
        for (Category Category : allCategoriesInDb) {
            categoryArrayList.add(Category);
            viewPagerAdapter.addFragment(PdfUserFragment.newInstance(
                    "" + Category.getId(),
                    "" + Category.getCategory(),
                    "" + Category.getUid()), Category.getCategory());
        }

        viewPager.setAdapter(viewPagerAdapter);

        binding.btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private final ArrayList<PdfUserFragment> fragmentList = new ArrayList<>();
        private final ArrayList<String> fragmentTitleList = new ArrayList<>();
        private final Context context;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
            super(fm, behavior);
            this.context = context;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (position < 0 || position >= fragmentList.size()) {
                return new Fragment(); // hoặc throw new IllegalStateException(...)
            }
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        private void addFragment(PdfUserFragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }
}