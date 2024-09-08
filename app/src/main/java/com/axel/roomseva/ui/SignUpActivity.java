package com.axel.roomseva.ui;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.axel.roomseva.R;
import com.axel.roomseva.ui.Model.Hotel;
import com.axel.roomseva.ui.Model.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputName, inputHotelName, inputEmail, inputPass, inputAvlRoom;
    private Button registerBtn;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final View rootView = findViewById(R.id.main);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            if (keypadHeight > screenHeight * 0.15) {  // Keyboard is open
                findViewById(R.id.lin).setPadding(0, 0, 0, keypadHeight);
            } else {  // Keyboard is closed
                findViewById(R.id.lin).setPadding(0, 0, 0, 0);
            }
        });

        hook();
        registerBtn.setOnClickListener(view -> registerUser());
    }


    private void hook() {
        inputName = findViewById(R.id.inputNameEditText);
        inputEmail = findViewById(R.id.inputEmailEditText);
        inputHotelName = findViewById(R.id.inputHotelNameEditText);
        inputPass = findViewById(R.id.inputPassEditText);
        inputAvlRoom = findViewById(R.id.inputAvlRoomEditText);

        registerBtn = findViewById(R.id.registerButton);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("hotels");
    }

    private void registerUser() {
        String name = inputName.getText().toString().trim();
        String hotelName = inputHotelName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPass.getText().toString().trim();
        String availableRooms = inputAvlRoom.getText().toString().trim();

        // Convert availableRooms to an integer
        int totalRooms = Integer.parseInt(availableRooms);

        // Register the user in Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Get the currently signed-in user
                    FirebaseUser user = auth.getCurrentUser();

                    // Create a hotel object with the provided details
                    Hotel newHotel = new Hotel(name, hotelName, email, password, availableRooms);

                    // Save user data to Firebase Realtime Database
                    if (user != null) {
                        databaseReference.child(user.getUid()).setValue(newHotel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    // Registration successful, now create Room nodes
                                    DatabaseReference roomsReference = databaseReference.child(user.getUid()).child("Rooms");

                                    // Loop to create room nodes based on the number of available rooms
                                    for (int i = 1; i <= totalRooms; i++) {
                                        String roomId = "roomID-" + i;
                                        String roomNumber = "D-" +(100 + i);
                                        String status = "Available";
                                        Room room = new Room(roomNumber, status);
                                        roomsReference.child(roomId).setValue(room);
                                    }

                                    Toast.makeText(SignUpActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                                    // Redirect to login or main activity
                                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(SignUpActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                } else {
                    // Registration failed
                    Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
