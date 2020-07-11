package com.example.instagram.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.instagram.LoginActivity;
import com.example.instagram.MainActivity;
import com.example.instagram.R;
import com.example.instagram.models.Post;
import com.example.instagram.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends DialogFragment {
    public static final String TAG = "SignupFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    private EditText etSignupUsername;
    private EditText etSignupPassword;
    private Button btnSignupPic;
    private ImageView ivProfilePic;
    private Button btnCreateAccount;
    private File profilePicFile;
    private String profilePicFileName = "profile_photo.jpg";

    public SignupFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SignupFragment newInstance(String title) {
        SignupFragment dialogFragment= new SignupFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialogFragment.setArguments(args);
        return dialogFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etSignupUsername = view.findViewById(R.id.etSignupUsername);
        etSignupPassword = view.findViewById(R.id.etSignupPassword);
        btnSignupPic = view.findViewById(R.id.btnSignupPic);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        btnCreateAccount = view.findViewById(R.id.btnCreateAccount);

        btnSignupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = etSignupUsername.getText().toString();
                String password = etSignupPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(getContext(), "Username and/or password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (profilePicFile == null || ivProfilePic.getDrawable() == null){
                    Toast.makeText(getContext(), "You need a profile picture!", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveUser(username, password, profilePicFile);
                SignupFragment.this.dismiss();
                //getActivity().onBackPressed();
            }
        });
    }

    private void saveUser(String username, String password, File profilePicFile) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setProfilePicture(new ParseFile(profilePicFile));
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "User save was successful!");
                Toast.makeText(getContext(), "Welcome to Instagram!", Toast.LENGTH_SHORT).show();
                //clear out previous data
                etSignupUsername.setText("");
                etSignupPassword.setText("");
                ivProfilePic.setImageResource(0);
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        profilePicFile = getPhotoFileUri(profilePicFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", profilePicFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(profilePicFile.getAbsolutePath());
                // Load the taken image into a preview
                ivProfilePic.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}