package com.example.nhom9appdocsach.FragmentAdmin;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhom9appdocsach.Adapter.AdapterNoti;
import com.example.nhom9appdocsach.Model.Noti;
import com.example.nhom9appdocsach.R;
import com.example.nhom9appdocsach.Database.DatabaseHandel;

import java.util.ArrayList;
import java.util.Collections;

public class NotiFragmentAdmin extends Fragment {

    private RecyclerView notificationsRv;
    private ArrayList<Noti> notificationsList;
    private AdapterNoti adapterNotification;

    public NotiFragmentAdmin() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noti_admin, container, false);

        notificationsRv = view.findViewById(R.id.notificationsRv);
        notificationsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        notificationsList = new ArrayList<>();
        Context context = getContext();
        if (context == null) return;

        DatabaseHandel dbHelper = new DatabaseHandel(context);
        notificationsList = dbHelper.getAllNotifications();

        // Sắp xếp giảm dần theo timestamp
        Collections.sort(notificationsList, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));

        adapterNotification = new AdapterNoti(getContext(), notificationsList);
        notificationsRv.setAdapter(adapterNotification);
    }
}