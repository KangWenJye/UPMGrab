package com.example.user.upmgrab;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverSignUpActivity extends AppCompatActivity {

    private EditText mName, mEmail, mMatricNum, mPassword;

    private Button mRegister;

    private TextView mLogin;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    //private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        // mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_sign_up);

        mAuth = FirebaseAuth.getInstance();

        mName = (EditText) findViewById(R.id.name);

        mEmail = (EditText) findViewById(R.id.email);

        mMatricNum = (EditText) findViewById(R.id.signUpMatricNum);

        mPassword = (EditText) findViewById(R.id.signUpPassword);

        mRegister = (Button) findViewById(R.id.btnRegister);

        mLogin = (TextView) findViewById(R.id.loginNow);

        mProgress = new ProgressDialog(this);

        mRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverSignUpActivity.this, DriverLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
      /*  mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    finish();
                    startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));
                }
            }
        };*/

    }

    private void startRegister() {

        final String name = mName.getText().toString().trim();
        final String email = mEmail.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        final String matricNum = mMatricNum.getText().toString().trim();

        if (name.isEmpty() || name.length() < 3) {
            Toast.makeText(this,"Please enter your name",Toast.LENGTH_LONG).show();
            return;
        }

        if (email.isEmpty() ) {
            Toast.makeText(this,"Please enter your email",Toast.LENGTH_LONG).show();
            return;
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Your email is invalid",Toast.LENGTH_LONG).show();
            return;
        }

        if (matricNum.isEmpty() || matricNum.length() < 6) {
            Toast.makeText(this,"Please enter a valid matric number",Toast.LENGTH_LONG).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(this,"Please enter your password",Toast.LENGTH_LONG).show();
            return;
        }else if(password.length() < 6){
            Toast.makeText(this,"Password should be consist 6 character",Toast.LENGTH_LONG).show();
            return;
        }

        {
            mProgress.setMessage("Registering, please wait...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if (task.isSuccessful()) {
                                mAuth.signInWithEmailAndPassword(email, password);
                                //Toast.makeText(ActivityRegister.this, user_id, Toast.LENGTH_SHORT).show();
                                Toast.makeText(DriverSignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
                                DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                                currentUserDB.child("name").setValue(name);
                                currentUserDB.child("email").setValue(email);
                                currentUserDB.child("password").setValue(password);
                                currentUserDB.child("matric number").setValue(matricNum);

                            } else
                                Toast.makeText(DriverSignUpActivity.this, "error registering user", Toast.LENGTH_SHORT).show();

                        }
                    });
        }
    }

}