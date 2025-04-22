package com.example.e_almawar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_almawar.viewmodel.Facility;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SiswaSarprasFragment extends Fragment {

    private RecyclerView recyclerView;
    private FacilityAdapter adapter;
    private List<Facility> facilityList;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_siswa_sarpras, container, false);

        recyclerView = view.findViewById(R.id.recyclerFacilities);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        facilityList = new ArrayList<>();
        adapter = new FacilityAdapter(getContext(), facilityList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        loadFacilities();

        return view;
    }

    private void loadFacilities() {
        db.collection("sarana_prasarana")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    facilityList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Facility facility = doc.toObject(Facility.class);
                        facilityList.add(facility);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // handle error
                });
    }
}