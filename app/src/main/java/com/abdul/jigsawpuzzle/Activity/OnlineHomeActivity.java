package com.abdul.jigsawpuzzle.Activity;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.abdul.jigsawpuzzle.Adapter.FragmentAdapter;
import com.abdul.jigsawpuzzle.Common.Common;
import com.abdul.jigsawpuzzle.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class OnlineHomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    ViewPager viewPager;
    TabLayout tabLayout;
    DrawerLayout drawer;

    GoogleApiClient googleApiClient;
    BottomNavigationView menu_bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Holma Puzzle Game");
        toolbar.setLogo(R.drawable.ic_computer_black_24dp);
        setSupportActionBar(toolbar);



        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, OnlineHomeActivity.this).
                addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();

        signIn();


        menu_bottom = (BottomNavigationView)findViewById(R.id.navigation);
        menu_bottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_upload){
                    Intent intent = new Intent(OnlineHomeActivity.this, UploadActivity.class);
                    startActivity(intent);
                }

                return false;
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                Snackbar.make(viewPager, "Signed Out", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signIntent, Common.SIGN_IN_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Common.SIGN_IN_REQUEST_CODE)
            if (resultCode == RESULT_OK){
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(googleSignInResult);
            }
    }

    //Displays User email
    private void handleSignInResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()){
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            Snackbar.make(viewPager, "Welcome: " +googleSignInAccount.getEmail(), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_sign_in:
                signIn();
                break;

            case R.id.action_sign_out:
                signOut();
                break;

            case R.id.action_settings:
                Intent intent = new Intent(OnlineHomeActivity.this, UploadActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "connectionResult"+ connectionResult, Toast.LENGTH_SHORT).show();
    }

}
