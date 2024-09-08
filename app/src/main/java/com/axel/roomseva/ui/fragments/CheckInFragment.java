package com.axel.roomseva.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CheckInFragment extends Fragment {

    private static final String TAG = "CheckInFragment";

    private TextView timePicker;
    private LinearLayout timeLayout;
    private EditText edCustomerName, edCustomerAadhar, edCustomerPhone, edCustomerRoomNo;
    private Button checkInBtn;

    private String cuName, cuAadharNum, cuPhoneNum, cuRoomNo;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check_in, container, false);

        // Initialize views
        timePicker = view.findViewById(R.id.timePicker);
        timeLayout = view.findViewById(R.id.timeLayout);
        edCustomerName = view.findViewById(R.id.edCustomerName);
        edCustomerAadhar = view.findViewById(R.id.edCustomerAadhar);
        edCustomerPhone = view.findViewById(R.id.edCustomerPhone);
        edCustomerRoomNo = view.findViewById(R.id.edCustomerRoomNo);
        checkInBtn = view.findViewById(R.id.CustomercheckInBtn);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("hotels");

        // Retrieve room number from arguments (if any)
        if (getArguments() != null) {
            String roomNumber = getArguments().getString("roomNumber");

            if (roomNumber != null && roomNumber.contains("-")) {
                // Extract the numeric part of the room number
                String numericRoomNumber = roomNumber.split("-")[1];
                edCustomerRoomNo.setText(numericRoomNumber); // Pre-fill only the numeric part
            } else {
                edCustomerRoomNo.setText(roomNumber); // Pre-fill the room number as it is, if no hyphen
            }
        }


        // Set up listeners
        timeLayout.setOnClickListener(v -> showDateTimePicker());

        checkInBtn.setOnClickListener(v -> {
            cuName = edCustomerName.getText().toString();
            cuAadharNum = edCustomerAadhar.getText().toString();
            cuPhoneNum = edCustomerPhone.getText().toString();
            cuRoomNo = ("D-" + edCustomerRoomNo.getText().toString());

            if (validateInput(cuName, cuAadharNum, cuPhoneNum, cuRoomNo)) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    findRoomByRoomNumber(currentUser.getUid(), cuRoomNo);
                }
            }
        });

        return view;
    }

    private boolean validateInput(String cuName, String cuAadharNum, String cuPhoneNum, String cuRoomNo) {
        boolean isValid = true;

        // Validate inputs and show error messages if needed
        if (cuName.isEmpty()) {
            edCustomerName.setError("Enter Name");
            isValid = false;
        }

        if (cuAadharNum.isEmpty() || cuAadharNum.length() != 12) {
            edCustomerAadhar.setError("Enter Valid Aadhar No");
            isValid = false;
        }

        if (cuPhoneNum.isEmpty() || cuPhoneNum.length() != 10) {
            edCustomerPhone.setError("Phone Number is invalid");
            isValid = false;
        }

        if (cuRoomNo.isEmpty()) {
            edCustomerRoomNo.setError("Must Enter Room No.");
            isValid = false;
        }

        return isValid;
    }

    private void showDateTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, month1, dayOfMonth) -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute1) -> {
                String dateTime = String.format("%02d-%02d-%d %02d:%02d", dayOfMonth, month1 + 1, year1, hourOfDay, minute1);
                timePicker.setText(dateTime);
            }, hour, minute, true);
            timePickerDialog.show();
        }, year, month, day);
        datePickerDialog.show();
    }

    private void findRoomByRoomNumber(String hotelId, String roomNumber) {
        DatabaseReference roomsRef = databaseReference.child(hotelId).child("Rooms");

        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean roomFound = false;
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    String currentRoomNumber = roomSnapshot.child("roomNumber").getValue(String.class);
                    if (currentRoomNumber != null && currentRoomNumber.equals(roomNumber)) {
                        checkInCustomer(hotelId, roomSnapshot.getKey());
                        roomFound = true;
                        break;
                    }
                }

                if (!roomFound) {
                    Toast.makeText(getActivity(), "Room not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read rooms: ", error.toException());
            }
        });
    }

    private void checkInCustomer(String hotelId, String roomId) {
        databaseReference.child(hotelId).child("Rooms").child(roomId).child("status").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String status = task.getResult().getValue(String.class);
                if ("Available".equals(status)) {
                    updateRoomStatusAndCustomerData(hotelId, roomId);
                } else {
                    Toast.makeText(getActivity(), "Room is not available", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Failed to get room status.", task.getException());
            }
        });
    }

    private void updateRoomStatusAndCustomerData(String hotelId, String roomId) {
        String roomPath = "Rooms/" + roomId + "/status";
        String customerPath = "CustomerData/" + cuAadharNum;

        Map<String, Object> updates = new HashMap<>();
        updates.put(roomPath, "Occupied");

        Map<String, Object> customerData = new HashMap<>();
        customerData.put("name", cuName);
        customerData.put("phone", cuPhoneNum);
        customerData.put("aadhar", cuAadharNum);

        databaseReference.child(hotelId).child(customerPath).updateChildren(customerData).addOnCompleteListener(customerTask -> {
            if (customerTask.isSuccessful()) {
                saveVisitRecordAndUpdateRoom(hotelId, customerPath, cuRoomNo, updates);
            } else {
                Log.e(TAG, "Failed to save customer data.", customerTask.getException());
            }
        });
    }

    private void saveVisitRecordAndUpdateRoom(String hotelId, String customerPath, String roomId, Map<String, Object> updates) {
        String visitId = databaseReference.child(hotelId).child(customerPath).child("visits").push().getKey();
        String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        if (visitId != null) {
            updates.put(customerPath + "/visits/" + visitId + "/dateTime", dateTime);
            updates.put(customerPath + "/visits/" + visitId + "/roomNo", roomId);

            databaseReference.child(hotelId).updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    navigateToDefaultFragment();
                } else {
                    Log.e(TAG, "Failed to check in the customer.", task.getException());
                }
            });
        }
    }

    private void navigateToDefaultFragment() {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.replaceFragment(new DefaultFragment(), false); // Do not add to back stack
        }
    }

}
