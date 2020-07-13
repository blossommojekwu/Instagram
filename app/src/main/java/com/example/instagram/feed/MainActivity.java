package com.example.instagram.feed;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.databinding.ActivityMainBinding;
import com.example.instagram.login.LoginActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView mBottomNavigationView;
    private MaterialToolbar mInstagramToolBar;
    private Button mBtnProfilePic;
    private Button mBtnLogOut;
    private File mProfilePhotoFile;
    private String mProfilePhotoFileName = "profile_photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View mainView = mainBinding.getRoot();
        setContentView(mainView);

        mInstagramToolBar = mainBinding.instagramToolBar;
        mBtnProfilePic = mainBinding.btnProfilePic;
        mBtnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        mBtnLogOut = mainBinding.btnLogOut;
        mBtnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        mBottomNavigationView = mainBinding.bottomNavigation;
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.actionHome:fragment = new PostsFragment();
                        Toast.makeText(MainActivity.this, R.string.home, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionCompose:fragment = new ComposeFragment();
                        Toast.makeText(MainActivity.this, R.string.post, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.actionProfile:
                    default:fragment = new ProfileFragment();
                        Toast.makeText(MainActivity.this, R.string.profile, Toast.LENGTH_SHORT).show();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        mBottomNavigationView.setSelectedItemId(R.id.actionHome);

    }
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        mProfilePhotoFile = getPhotoFileUri(mProfilePhotoFileName);

        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", mProfilePhotoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}