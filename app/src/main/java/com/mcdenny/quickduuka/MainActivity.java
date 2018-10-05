package com.mcdenny.quickduuka;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mcdenny.quickduuka.common.Common;
import com.mcdenny.quickduuka.model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    Button logIn, signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logIn = (Button) findViewById(R.id.btn_login);
        signUp = (Button) findViewById(R.id.btn_signup);

        //init firebase
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("User");

        //listening the login button
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginDialog();
            }
        });

        //listening the sign up button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpDialog();
            }
        });
    }

    //when login button clicked
    private void showLoginDialog() {
        final AlertDialog.Builder loginDialog = new AlertDialog.Builder(this);
        loginDialog.setTitle("LOGIN");
        loginDialog.setMessage("Fill in all the fields* ");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.login_layout, null);

        final MaterialEditText phone = (MaterialEditText) view.findViewById(R.id.txt_phone);
        final MaterialEditText pswd = (MaterialEditText) view.findViewById(R.id.txt_password);

        loginDialog.setView(view);

        loginDialog.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(TextUtils.isEmpty(phone.getText().toString())) {
                    phone.setError("You must fill in the phone number!");
                    phone.requestFocus();
                }
                else if (TextUtils.isEmpty(phone.getText().toString())){
                    pswd.setError("You must fill in the password!");
                    pswd.requestFocus();
                }
                else {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //checking if the user exists
                            if (dataSnapshot.child(phone.getText().toString()).exists()){

                                //getting the users information

                                User newUser = dataSnapshot.child(phone.getText().toString()).getValue(User.class);
                                assert newUser != null;
                                newUser.setPhone(phone.getText().toString());
                                if (newUser.getPassword().equals(pswd.getText().toString())) {
                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    Common.user_current = newUser;//the user details are stored in user_current variable
                                    startActivity(intent);
                                    finish();//stops the login activity
                                }
                                else {
                                    Toast.makeText(MainActivity.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {
                                Toast.makeText(MainActivity.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                
            }
        });
        loginDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        loginDialog.show();

    }

    //when sign up button clicked
    private void showSignUpDialog() {
        final AlertDialog.Builder signupDialog = new AlertDialog.Builder(this);
        signupDialog.setTitle("SIGN UP");
        signupDialog.setMessage("Fill in all the fields* ");

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.signup_layout, null);

        final MaterialEditText name = (MaterialEditText) view.findViewById(R.id.txt_name);
        final MaterialEditText rphone = (MaterialEditText) view.findViewById(R.id.txt_rPhone);
        final MaterialEditText rpswd = (MaterialEditText) view.findViewById(R.id.txt_rpassword);

        signupDialog.setView(view);

        signupDialog.setPositiveButton("SIGN UP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(TextUtils.isEmpty(rphone.getText().toString())) {
                    rphone.setError("You must fill in the phone number!");
                    rphone.requestFocus();
                }
                else if (rpswd.getText().toString().isEmpty()){
                    rpswd.setError("You must fill in the password!");
                    rpswd.requestFocus();
                }
                else if (name.getText().toString().isEmpty()){
                    rpswd.setError("You must fill in the name!");
                    rpswd.requestFocus();
                }
                else {
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            //checking if the user exists
                            if (dataSnapshot.child(rphone.getText().toString()).exists()){
                                Toast.makeText(MainActivity.this, "User with this phone number exists", Toast.LENGTH_SHORT).show();
                            }
                            // if the user doesn't exist
                            else {
                                User user = new User(name.getText().toString(), rpswd.getText().toString());
                                reference.child(rphone.getText().toString()).setValue(user);
                                Toast.makeText(MainActivity.this, "Succesfully registered!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }
        });
        signupDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        signupDialog.show();
    }
}
