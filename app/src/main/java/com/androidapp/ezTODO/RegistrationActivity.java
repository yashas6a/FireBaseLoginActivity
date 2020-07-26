package com.androidapp.ezTODO;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private EditText mFullName, mEmail, mPassword, mPhone;
    private Button mRegisterbtn;
    private ProgressBar progressBarR;
    FirebaseAuth fAuth;

    //public static Credentials credentials;

//    SharedPreferences sharedPreferences;
//    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mFullName = findViewById(R.id.etRegName);
        mEmail = findViewById(R.id.etEmail);
        mPassword = findViewById(R.id.etRegPassword);
        mPhone = findViewById(R.id.etPhoneNumber);
        mRegisterbtn = findViewById(R.id.btnRegister);

        fAuth = FirebaseAuth.getInstance();
        progressBarR = findViewById(R.id.progressBarRegister);

        /* ver E04 */
//        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
//        sharedPreferencesEditor = sharedPreferences.edit();

        //to check if user is already registered so that he can login
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }

                if(password.length() < 8){
                    mPassword.setError("Password must be atleast 8 characters long");
                    return;
                }

                progressBarR.setVisibility(View.VISIBLE);

                //register the user in firebase

                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "user Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    /*
    private boolean validate(String username, String password){

        if(username.isEmpty() || password.length()<8){
            Toast.makeText(this, "Invalid input! Password must be atleast 8 characters long!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }*/

}
