package com.cjz.wasif.lifesaverapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Driver_contact extends AppCompatActivity {

    private Button send;
    private EditText driver_email;
    private EditText driver_message;
    private String driver_messages;
    private String driver_emaill;
    private DatabaseReference mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_contact);

        driver_email=(EditText)findViewById(R.id.email_driver);
        driver_message=(EditText)findViewById(R.id.message_driver);
        send=(Button)findViewById(R.id.send_driver);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(driver_email.getText().toString())){

                    Toast.makeText(Driver_contact.this, "Please Entered first email !!", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(driver_message.getText().toString())){

                    Toast.makeText(Driver_contact.this, "Please Entered first Message !!", Toast.LENGTH_SHORT).show();
                }
                else {

                    if(isEmailValid(driver_email.getText().toString())==true){

                        driver_emaill=driver_email.getText().toString();
                        driver_messages=driver_message.getText().toString();

                        mydatabase= FirebaseDatabase.getInstance().getReference("Driver Message");
                        mydatabase.child("Email").setValue(driver_emaill);
                        mydatabase.child("Message").setValue(driver_messages);

                        Toast.makeText(Driver_contact.this, "Message send Successfully!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Driver_contact.this,Driver_contact_extented.class));
                    }
                    else {
                        Toast.makeText(Driver_contact.this, "Email is not valid!", Toast.LENGTH_SHORT).show();
                    }
                }






            }
        });






    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}
