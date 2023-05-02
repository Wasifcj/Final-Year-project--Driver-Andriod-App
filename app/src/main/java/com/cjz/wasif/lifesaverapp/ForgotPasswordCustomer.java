package com.cjz.wasif.lifesaverapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordCustomer extends AppCompatActivity {

    Button reset_button;
    EditText customer_Em;
    ProgressDialog lodingbar;

    FirebaseAuth auth;

    //String reset_validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_customer);

        reset_button = (Button) findViewById(R.id.Reset_passeword_customer);
        customer_Em = (EditText) findViewById(R.id.customer_email);
        lodingbar = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        auth = FirebaseAuth.getInstance();
        //reset_validation = customer_Em.getText().toString();


            reset_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (TextUtils.isEmpty(customer_Em.getText().toString())) {
                        Toast.makeText(ForgotPasswordCustomer.this, "Please entered the Email", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    else{


                    lodingbar.setTitle("Please wait...");
                    lodingbar.setMessage("While reseting your password");
                    lodingbar.show();

                    auth.sendPasswordResetEmail(customer_Em.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                lodingbar.dismiss();
                                Toast.makeText(ForgotPasswordCustomer.this, "Reset Password link is Send Successfully", Toast.LENGTH_SHORT).show();
                            } else {

                                lodingbar.dismiss();
                                Toast.makeText(ForgotPasswordCustomer.this, "Email is not valid or registered in our System", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }}
            });
        }
    }