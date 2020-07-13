package com.example.instagram.login;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.example.instagram.R;
import com.example.instagram.models.User;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class SignupFragment extends DialogFragment {
    public static final String TAG = "SignupFragment";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 101;
    private EditText mEtSignupUsername;
    private EditText mEtSignupPassword;
    private Button mBtnSignupPic;
    private ImageView mIvProfilePic;
    private Button mBtnCreateAccount;
    private File mProfilePicFile;
    private String mProfilePicFileName = "profile_photo.jpg";

    public SignupFragment() {
        // Empty constructor is required for DialogFragment
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
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEtSignupUsername = view.findViewById(R.id.etSignupUsername);
        mEtSignupPassword = view.findViewById(R.id.etSignupPassword);
        mBtnSignupPic = view.findViewById(R.id.btnSignupPic);
        mIvProfilePic = view.findViewById(R.id.ivProfilePic);
        mBtnCreateAccount = view.findViewById(R.id.btnCreateAccount);

        mBtnSignupPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        mBtnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = mEtSignupUsername.getText().toString();
                final String password = mEtSignupPassword.getText().toString();
                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(getContext(), "Username and/or password cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mProfilePicFile == null || mIvProfilePic.getDrawable() == null){
                    Toast.makeText(getContext(), "You need a profile picture!", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ParseFile photo = new ParseFile(mProfilePicFile);
                photo.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //if successful adds photo file to user and signUpInbackground
                        if (e == null){
                            saveUser(username, password, photo);
                            ParseUser.logInInBackground(username, password, new LogInCallback() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (user != null){
                                        Log.i(TAG, R.string.login_new_user + ": " + username);
                                        Toast.makeText(getContext(), R.string.login_new_user, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void saveUser(String username, String password, ParseFile profilePicFile) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("profilePicture", profilePicFile);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while signing up", e);
                    Toast.makeText(getContext(), R.string.signup_error, Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "User save was successful!");
                Toast.makeText(getContext(), R.string.welcome_instagram, Toast.LENGTH_SHORT).show();
                //clear out previous data
                mEtSignupUsername.setText("");
                mEtSignupPassword.setText("");
                mIvProfilePic.setImageResource(0);
            }
        });
        Log.i(TAG, "New user: " + ParseUser.getCurrentUser());
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mProfilePicFile = getPhotoFileUri(mProfilePicFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", mProfilePicFile);
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
                Bitmap takenImage = BitmapFactory.decodeFile(mProfilePicFile.getAbsolutePath());
                // Load the taken image into a preview
                mIvProfilePic.setImageBitmap(takenImage);
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