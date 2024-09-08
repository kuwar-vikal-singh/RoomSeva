package com.axel.roomseva.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axel.roomseva.R;
import com.axel.roomseva.ui.Adapter.RoomAdapter;
import com.axel.roomseva.ui.MainActivity;
import com.axel.roomseva.ui.Model.Room;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultFragment extends Fragment {

    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private List<Room> roomList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_default, container, false);


        // Initialize views
        recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        recyclerViewRooms.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns for the grid layout

        // Initialize list and adapter
        roomList = new ArrayList<>();

        // Set adapter with the list and a click listener (implementing a no-op listener here)
        roomAdapter = new RoomAdapter(roomList, this::onRoomClicked);
        recyclerViewRooms.setAdapter(roomAdapter);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userId = currentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("hotels");

        // Load data
        loadRooms();

        return view;
    }

    private void loadRooms() {
        if (userId != null) {
            databaseReference.child(userId).child("Rooms").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    roomList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Room room = snapshot.getValue(Room.class);
                        // Only add available rooms to the list
                        if ("Available".equals(room.getStatus())) {
                            roomList.add(room);
                        }
                    }

                    // Notify the adapter of the updated list
                    roomAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors here if needed
                }
            });
        } else {
            // Handle case where userId is not available
        }
    }


    // This method will handle the room click, you can implement actual logic here
    private void onRoomClicked(Room room) {
        // Create a new instance of CheckInFragment
        CheckInFragment checkInFragment = new CheckInFragment();

        // Create a bundle and pass the room number
        Bundle bundle = new Bundle();
        bundle.putString("roomNumber", room.getRoomNumber());

        // Set the arguments to the fragment
        checkInFragment.setArguments(bundle);

        // Replace the fragment with CheckInFragment
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.replaceFragment(checkInFragment, true); // Add to back stack if needed
        }
    }

}
