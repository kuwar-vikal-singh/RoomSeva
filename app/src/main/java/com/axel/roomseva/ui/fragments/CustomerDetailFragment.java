package com.axel.roomseva.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.axel.roomseva.R;
import com.axel.roomseva.ui.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CustomerDetailFragment extends Fragment {

    private String aadhar, roomNo;
    private DatabaseReference hotelRef;
    private String userId;
    FirebaseAuth mAuth;


    public static CustomerDetailFragment newInstance(String aadhar, String name, String phone, String visitPhone, String checkInDate, String roomNo) {
        CustomerDetailFragment fragment = new CustomerDetailFragment();
        Bundle args = new Bundle();
        args.putString("aadhar", aadhar);
        args.putString("name", name);
        args.putString("phone", phone);
        args.putString("visitPhone", visitPhone);
        args.putString("checkInDate", checkInDate);
        args.putString("roomNo", roomNo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.customer_detail_fragment, container, false);

        if (getArguments() != null) {
            aadhar = getArguments().getString("aadhar");
            String name = getArguments().getString("name");
            String phone = getArguments().getString("phone");
            String visitPhone = getArguments().getString("visitPhone");
            String checkInDate = getArguments().getString("checkInDate");
            roomNo = getArguments().getString("roomNo");

            TextView tvName = view.findViewById(R.id.customerName);
            TextView tvPhone = view.findViewById(R.id.customerPhone);
            TextView tvCheckInDate = view.findViewById(R.id.checkinDate);
            TextView tvRoomNo = view.findViewById(R.id.roomNum);

            tvName.setText(name);
            tvPhone.setText(visitPhone != null && !visitPhone.isEmpty() ? visitPhone : phone);
            tvCheckInDate.setText(checkInDate);
            tvRoomNo.setText(roomNo);

            mAuth = FirebaseAuth.getInstance();


            FirebaseUser currentUser = mAuth.getCurrentUser();
            userId = currentUser.getUid();

            hotelRef = FirebaseDatabase.getInstance().getReference("hotels/"+userId);
        }

        Button checkoutButton = view.findViewById(R.id.checkOutButton);
        checkoutButton.setOnClickListener(v -> handleCheckout());

        return view;
    }

    private static final long TOAST_DEBOUNCE_DELAY_MS = 2000;
    private long lastToastTime = 0;

    private void showToast(String message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToastTime > TOAST_DEBOUNCE_DELAY_MS) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            lastToastTime = currentTime;
        }
    }

    private void handleCheckout() {
        if (roomNo != null && !roomNo.isEmpty() && aadhar != null && !aadhar.isEmpty()) {
            DatabaseReference roomRef = hotelRef.child("Rooms").orderByChild("roomNumber").equalTo(roomNo).getRef();
            roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String roomId = snapshot.getKey();
                            if (roomId != null) {
                                DatabaseReference updateRoomRef = hotelRef.child("Rooms").child(roomId);
                                Map<String, Object> roomUpdates = new HashMap<>();
                                roomUpdates.put("status", "Available");

                                updateRoomRef.updateChildren(roomUpdates).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        DatabaseReference visitRef = hotelRef.child("CustomerData").child(aadhar).child("visits").orderByChild("roomNo").equalTo(roomNo).getRef();
                                        visitRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot visitSnapshot) {
                                                if (visitSnapshot.exists()) {
                                                    for (DataSnapshot visit : visitSnapshot.getChildren()) {
                                                        String visitId = visit.getKey();
                                                        if (visitId != null) {
                                                            String checkoutTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                                                            Map<String, Object> visitUpdates = new HashMap<>();
                                                            visitUpdates.put("checkOut", checkoutTime);

                                                            DatabaseReference updateVisitRef = hotelRef.child("CustomerData").child(aadhar).child("visits").child(visitId);
                                                            updateVisitRef.updateChildren(visitUpdates).addOnCompleteListener(visitTask -> {
                                                                if (visitTask.isSuccessful()) {
                                                                    showToast("Checkout successful!");
                                                                    new Handler(Looper.getMainLooper()).post(() -> {
                                                                        if (getActivity() instanceof MainActivity) {
                                                                            MainActivity mainActivity = (MainActivity) getActivity();
                                                                            mainActivity.replaceFragment(new DefaultFragment(), false);
                                                                        }
                                                                    });
                                                                } else {
                                                                    showToast("Failed to update checkout time.");
                                                                }
                                                            });
                                                        }
                                                    }
                                                } else {
                                                    showToast("Visit record not found.");
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                showToast("Failed to retrieve visit records.");
                                            }
                                        });
                                    } else {
                                        showToast("Failed to update room status.");
                                    }
                                });
                            }
                        }
                    } else {
                        showToast("Room not found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showToast("Failed to retrieve room data.");
                }
            });
        } else {
            showToast("Room number or Aadhar is missing.");
        }
    }
}
