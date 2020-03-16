package com.smartsum.smartsumparking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smartsum.smartsumparking.databinding.ActivityMainBinding;
import com.smartsum.smartsumparking.databinding.ActivityMapsBinding;
import com.smartsum.smartsumparking.pojo.Parking;
import com.smartsum.smartsumparking.pojo.ParkingSpace;

import android.app.Fragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LoginDialogFragment.logDialogFragmentSignInUser {

    //drawer layout
    private DrawerLayout drawer;

    //Navigation View
    private NavigationView navView;

    //viewBinding
    private ActivityMainBinding binding;

    //Firebase
        //Firebase auth
        private FirebaseAuth mAuth = FirebaseAuth.getInstance();
        private FirebaseUser currentUser;

        //Firebase database
        private FirebaseDatabase db = FirebaseDatabase.getInstance();

        //Firebase Reference
        private DatabaseReference parking1Ref = db.getReference("0");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Toolbar
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

        //Nav drawer
        drawer = binding.drawerLayout;
        navView = binding.navView;
        navView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MapsFragment()).commit();

        //Tying nav drawer open icon to the action bar
        ActionBarDrawerToggle navBarToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navDrawOpen, R.string.navDrawClose);
        drawer.addDrawerListener(navBarToggle);
        navBarToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        //If nav drawer is open, if you press back button do not close the app but close the nav drawer
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //TODO Still need to add the Legend Dialog
    //Method that handles clicks on the nav drawer items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.navMap:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new MapsFragment()).commit();
                break;

            case R.id.navLogin:
                LoginDialogFragment logDiagFrag = new LoginDialogFragment();
                logDiagFrag.show(getSupportFragmentManager(), "login_dialog_fragment");
                break;

            case R.id.navLogout:
                mAuth.getInstance().signOut();

                finishAffinity();
                restartActivity();

                break;

            case R.id.navLegend:
                LegendDialog legDialog = new LegendDialog();
                legDialog.show(getSupportFragmentManager(), "legend_dialog");
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and restart an activity accordingly.
        if(checkLogin() == true){
            currentUser = mAuth.getCurrentUser();
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.drawer_menu_logged_in);

            navView.setCheckedItem(R.id.navMap);

        } else {
            currentUser = null;
            navView.getMenu().clear();
            navView.inflateMenu(R.menu.drawer_menu_logged_out);

            navView.setCheckedItem(R.id.navMap);
        }
    }

    private boolean checkLogin(){
        if(mAuth.getCurrentUser() != null){
            return true;
        }

        return false;
    }

    public void restartActivity(){
        finish();
        startActivity(getIntent());
    }

    @Override
    public void signInUser(FirebaseUser user) {
        currentUser = mAuth.getCurrentUser();
        restartActivity();
    }
}
