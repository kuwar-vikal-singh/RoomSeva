package com.axel.roomseva.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.axel.roomseva.R;
import com.axel.roomseva.ui.Adapter.RoomAdapter;
import com.axel.roomseva.ui.Model.Customer;
import com.axel.roomseva.ui.Model.Room;
import com.axel.roomseva.ui.Model.Visit;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckOutFragment extends Fragment {

    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private List<Room> occupiedRoomsList = new ArrayList<>();
    String userId;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_out, container, false);

        recyclerViewRooms = view.findViewById(R.id.recyclerViewRooms);
        recyclerViewRooms.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // Initialize adapter here if needed
        roomAdapter = new RoomAdapter(occupiedRoomsList, CheckOutFragment.this::onRoomClicked);
        recyclerViewRooms.setAdapter(roomAdapter);

        // Get the current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        fetchOccupiedRooms();  // Fetch the occupied rooms from Firebase

        return view;
    }

    private void fetchOccupiedRooms() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("hotels")
                .child(userId)
                .child("Rooms");

        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                occupiedRoomsList.clear();  // Clear the list before adding new data
                for (DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    if (room != null && "Occupied".equals(room.getStatus())) {
                        occupiedRoomsList.add(room);  // Add only occupied rooms
                    }
                }

                // Check if adapter is set
                if (roomAdapter == null) {
                    roomAdapter = new RoomAdapter(occupiedRoomsList, CheckOutFragment.this::onRoomClicked);
                    recyclerViewRooms.setAdapter(roomAdapter);
                } else {
                    roomAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    // This method will handle the click event and navigate to the customer details fragment
    private void onRoomClicked(Room room) {
        DatabaseReference customerDataRef = FirebaseDatabase.getInstance().getReference("hotels")
                .child(userId)
                .child("CustomerData");

        customerDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean found = false;
                    for (DataSnapshot customerSnapshot : dataSnapshot.getChildren()) {
                        Customer customer = customerSnapshot.getValue(Customer.class);
                        if (customer != null) {
                            Visit visit = getVisitForRoom(customer.getVisits(), room.getRoomNumber());
                            if (visit != null) {
                                String checkInDate = visit.getDateTime() != null ? visit.getDateTime() : visit.getDateTime();

                                Log.d("CustomerDetail", "Aadhar: " + customer.getAadhar());
                                Log.d("CustomerDetail", "Name: " + customer.getName());
                                Log.d("CustomerDetail", "Phone: " + customer.getPhone());
                                Log.d("CustomerDetail", "Visit Phone: " + (visit.getPhoneNumber() != null ? visit.getPhoneNumber() : customer.getPhone()));
                                Log.d("CustomerDetail", "Check-In Date: " + checkInDate);
                                Log.d("CustomerDetail", "Room Number: " + room.getRoomNumber());

                                CustomerDetailFragment customerDetailFragment = CustomerDetailFragment.newInstance(
                                        customer.getAadhar(), customer.getName(), customer.getPhone(),
                                        (visit.getPhoneNumber() != null ? visit.getPhoneNumber() : customer.getPhone()),
                                        checkInDate, room.getRoomNumber());

                                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, customerDetailFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        Log.d("CustomerDetail", "No customer data found for room number: " + room.getRoomNumber());
                    }
                } else {
                    Log.d("CustomerDetail", "No customer data found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }



    // This method retrieves the visit information for the given room number
    private Visit getVisitForRoom(Map<String, Visit> visits, String roomNumber) {
        for (Visit visit : visits.values()) {
            if (roomNumber.equals(visit.getRoomNo())) {
                return visit;
            }
        }
        return null;
    }
}
