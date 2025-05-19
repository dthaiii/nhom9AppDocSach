package com.example.nhom9appdocsach.FragmentUser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nhom9appdocsach.Adapter.AdapterNoti;
import com.example.nhom9appdocsach.Database.DatabaseHandel;
import com.example.nhom9appdocsach.Model.Noti;
import com.example.nhom9appdocsach.R;

import java.util.ArrayList;
import java.util.Collections;

public class NotiFragmentUser extends Fragment {

    private RecyclerView notificationsRv;
    public ArrayList<Noti> notificationsList;
    public AdapterNoti adapterNotification;
    private DatabaseHandel dbHelper;

    public NotiFragmentUser() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noti_user, container, false);

        dbHelper = new DatabaseHandel(requireContext());

        notificationsRv = view.findViewById(R.id.notificationsRv);
        notificationsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        loadNotifications();

        return view;
    }

    private void loadNotifications() {
        notificationsList = dbHelper.getAllNotifications();
        if (notificationsList == null) notificationsList = new ArrayList<>();
        // Đảo ngược danh sách để notification mới nhất lên đầu
        Collections.sort(notificationsList, (a, b) -> Long.compare(b.getTimestamp(), a.getTimestamp()));
        adapterNotification = new AdapterNoti(getContext(), notificationsList);
        notificationsRv.setAdapter(adapterNotification);
    }
}