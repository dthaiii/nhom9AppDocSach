package com.example.nhom9appdocsach.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nhom9appdocsach.Adapter.AdapterPdfAdmin;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Pdf;
import com.example.nhom9appdocsach.databinding.ActivityPdfListAdminBinding;

import java.util.ArrayList;

public class PdfListAdminActivity extends AppCompatActivity {

    private ActivityPdfListAdminBinding binding;
    private ArrayList<Pdf> pdfArrayList;
    private AdapterPdfAdmin adapterPdfAdmin;

    private static final String TAG = "PDF_LIST_TAG";

    private String categoryId, categoryTitle;
    private DatabaseHandel dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPdfListAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHandel(this);

        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryTitle = intent.getStringExtra("categoryTitle");

        binding.txtsubTitle.setText(categoryTitle);
        loadPdfList();

        binding.edtsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (adapterPdfAdmin != null) {
                        adapterPdfAdmin.getFilter().filter(s);
                    }
                } catch (Exception e) {
                    Log.d(TAG,"onTextChanged: "+e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnback.setOnClickListener(v -> onBackPressed());
    }

    private void loadPdfList() {
        pdfArrayList = dbHelper.getBooksByCategory(categoryId);
        if (pdfArrayList == null) pdfArrayList = new ArrayList<>();
        for (Pdf modelPdf : pdfArrayList) {
            Log.d(TAG, "loadPdfList: " + modelPdf.getId() + " " + modelPdf.getTitle());
        }
        adapterPdfAdmin = new AdapterPdfAdmin(PdfListAdminActivity.this, pdfArrayList);
        binding.Rcvbook.setAdapter(adapterPdfAdmin);
    }
}