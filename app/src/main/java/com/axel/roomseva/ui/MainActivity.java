package com.axel.roomseva.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.axel.roomseva.R;
import com.axel.roomseva.ui.fragments.CheckInFragment;
import com.axel.roomseva.ui.fragments.CheckOutFragment;
import com.axel.roomseva.ui.fragments.DefaultFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the default fragment initially
        if (savedInstanceState == null) {
            replaceFragment(new DefaultFragment(), false);
        }

        // Set up button click listeners
        findViewById(R.id.checkInButton).setOnClickListener(v -> replaceFragment(new CheckInFragment(), true));
        findViewById(R.id.checkOutButton).setOnClickListener(v -> replaceFragment(new CheckOutFragment(), true));
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);

        if (addToBackStack) {
            // Clear the back stack if a new fragment is added
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
