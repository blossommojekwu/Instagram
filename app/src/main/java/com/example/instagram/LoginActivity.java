package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagram.databinding.ActivityLoginBinding;
import com.example.instagram.fragments.SignupFragment;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginBinding loginBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        // layout of activity is stored in a special property called root
        View loginView = loginBinding.getRoot();
        setContentView(loginView);
        //if user already logged in, go to MainActivity
        if (ParseUser.getCurrentUser() != null){
            getMainActivity();
        }

        etUsername = loginBinding.etUsername;
        etPassword = loginBinding.etPassword;
        btnLogin = loginBinding.btnLogin;
        btnSignUp = loginBinding.btnSignUp;
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked Login Button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked SignUp Button");
                showDialogFragment();
            }
        });
    }

    //show signup dialog to create account
    private void showDialogFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        SignupFragment signupFragment = SignupFragment.newInstance("Some Title");
        signupFragment.show(fragmentManager, "fragment_signup");
    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user " + username);
        //navigate to main activity if user has signed in properly
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with Login", e);
                    Toast.makeText(LoginActivity.this, "Incorrect Login", Toast.LENGTH_SHORT).show();
                    return;
                }
                getMainActivity();
                Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //Finish LoginActivity once navigated to MainActivity
        finish();
    }
}