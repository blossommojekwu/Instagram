package com.example.instagram.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.instagram.R;
import com.example.instagram.feed.MainActivity;
import com.example.instagram.databinding.ActivityLoginBinding;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String TAG = "LoginActivity";
    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnSignUp;

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

        mEtUsername = loginBinding.etUsername;
        mEtPassword = loginBinding.etPassword;
        mBtnLogin = loginBinding.btnLogin;
        mBtnSignUp = loginBinding.btnSignUp;
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked Login Button");
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                loginUser(username, password);
            }
        });
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(LoginActivity.this, R.string.incorrect_login, Toast.LENGTH_SHORT).show();
                    return;
                }
                getMainActivity();
                Toast.makeText(LoginActivity.this, R.string.welcome, Toast.LENGTH_SHORT).show();
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